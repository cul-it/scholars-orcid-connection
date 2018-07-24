/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.library.scholars.orcidconnection.ws.filters;

import static edu.cornell.library.scholars.orcidconnection.ws.utils.ServletUtils.FILTER_CHECK_AUTH;
import static edu.cornell.library.scholars.orcidconnection.ws.utils.ServletUtils.SERVLET_FAKE_LOGIN_PAGE;
import static edu.cornell.library.scholars.orcidconnection.ws.utils.ServletUtils.getExternalAuthHeaderName;
import static edu.cornell.library.scholars.orcidconnection.ws.utils.ServletUtils.getLocalId;
import static java.util.regex.Pattern.CASE_INSENSITIVE;

import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.library.scholars.orcidconnection.ws.utils.RuntimeProperties;

/**
 * Filter all requests. Check for authorization from CUWebAuth, or fake it.
 * 
 * Honor any request that contains a netid header, provided by CUWebAuth.
 * 
 * Otherwise, go through the faking process: present a page that asks for the
 * site password and the desired netid. On subsequent requests, that netid will
 * be added as a header.
 * 
 * Note: some requests must be honored even without authorization, such as a CSS
 * file for the fake login page. For now, there are no such requests.
 */
@WebFilter(filterName = FILTER_CHECK_AUTH)
public class CheckAuthFilter implements Filter {
    private static final Log log = LogFactory.getLog(CheckAuthFilter.class);

    public static final String PARAMETER_TARGET_URL = "targetUrl";
    public static final String PARAMETER_FAKE_NETID = "fakeNetid";
    public static final String PARAMETER_SITE_PASSWORD = "sitePassword";

    public static final String MESSAGE_WRONG_PASSWORD = "Password is incorrect";

    public static final String ATTRIBUTE_FAKE_NETID = CheckAuthFilter.class
            .getName() + "FakeNetid";
    public static final String ATTRIBUTE_ERROR_MESSAGE = CheckAuthFilter.class
            .getName() + "ErrorMessage";

    public static final Pattern[] UNRESTRICTED_URLS = new Pattern[] {
            Pattern.compile(".*\\.png$", CASE_INSENSITIVE) };

    @Override
    public void init(FilterConfig arg0) throws ServletException {
        // Nothing to initialize
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp,
            FilterChain chain) throws IOException, ServletException {
        log.debug("Doing the filter!!");
        new FilterCore(req, resp, chain).filter();
    }

    @Override
    public void destroy() {
        // Nothing to destroy.
    }

    /**
     * This inner class is thread-safe, even with instance variables.
     */
    private static class FilterCore {
        private final HttpServletRequest req;
        private final HttpServletResponse resp;
        private final HttpSession session;
        private final FilterChain chain;

        FilterCore(ServletRequest req, ServletResponse resp,
                FilterChain chain) {
            this.req = (HttpServletRequest) req;
            this.resp = (HttpServletResponse) resp;
            this.session = this.req.getSession();
            this.chain = chain;
        }

        void filter() throws IOException, ServletException {
            if (requestIsForUnrestrictedMaterial()) {
                honorTheRequest();
            } else if (requestContainsNetidHeader()) {
                honorTheRequest();
            } else if (sessionContainsNetid()) {
                honorTheRequestWithFakeNetid();
            } else if (requestProvidesNetidAndPassword()) {
                if (passwordIsCorrect()) {
                    recordNetidAndRedirectToTarget();
                } else {
                    showFakeLoginPage(MESSAGE_WRONG_PASSWORD);
                }
            } else {
                showFakeLoginPage();
            }
        }

        private boolean requestIsForUnrestrictedMaterial() {
            for (Pattern p: UNRESTRICTED_URLS) {
                Matcher m = p.matcher(req.getRequestURI());
                if (m.matches()) {
                    log.debug("request is unrestricted: " + p);
                    return true;
                }
            }
            log.debug("request is restricted");
            return false;
        }

        private boolean requestContainsNetidHeader() {
            String header = getLocalId(req);
            log.debug("netid header is '" + header + "'");
            return StringUtils.isNotBlank(header);
        }

        private boolean sessionContainsNetid() {
            String netid = getNetidFromSession();
            log.debug("netid from session is '" + netid + "'");
            return StringUtils.isNotBlank(netid);
        }

        private boolean requestProvidesNetidAndPassword() {
            String netid = req.getParameter(PARAMETER_FAKE_NETID);
            String targetUrl = req.getParameter(PARAMETER_TARGET_URL);
            String password = req.getParameter(PARAMETER_SITE_PASSWORD);
            log.debug("netid from request is '" + netid + "', target url is '"
                    + targetUrl + "', password is '" + password + "'");

            return StringUtils.isNotBlank(netid)
                    && StringUtils.isNotBlank(targetUrl)
                    && StringUtils.isNotBlank(password);
        }

        private boolean passwordIsCorrect() {
            String password = req.getParameter(PARAMETER_SITE_PASSWORD);
            return Objects.equals(password,
                    RuntimeProperties.getValue("sitePassword"));
        }

        private void honorTheRequest() throws IOException, ServletException {
            log.debug("honor the request");
            chain.doFilter(req, resp);
        }

        private void honorTheRequestWithFakeNetid()
                throws IOException, ServletException {
            log.debug("honor the request with fake netid");
            NetidFakerRequest wrappedRequest = new NetidFakerRequest(req,
                    getExternalAuthHeaderName(), getNetidFromSession());
            chain.doFilter(wrappedRequest, resp);
        }

        private void recordNetidAndRedirectToTarget() throws IOException {
            log.debug("record netid and redirect");
            session.setAttribute(ATTRIBUTE_FAKE_NETID,
                    req.getParameter(PARAMETER_FAKE_NETID));
            resp.sendRedirect(req.getParameter(PARAMETER_TARGET_URL));
        }

        private void showFakeLoginPage() throws ServletException, IOException {
            log.debug("show fake login page");
            RequestDispatcher dispatcher = req.getServletContext()
                    .getNamedDispatcher(SERVLET_FAKE_LOGIN_PAGE);
            dispatcher.forward(req, resp);
        }

        private void showFakeLoginPage(String message)
                throws ServletException, IOException {
            req.setAttribute(ATTRIBUTE_ERROR_MESSAGE, message);
            showFakeLoginPage();
        }

        private String getNetidFromSession() {
            return (String) session.getAttribute(ATTRIBUTE_FAKE_NETID);
        }

    }

    /**
     * This request has all of the parameters, attributes, and headers of the
     * wrapped request, plus one more header.
     */
    private static class NetidFakerRequest extends HttpServletRequestWrapper {
        private final String netidHeaderName;
        private final String netid;

        public NetidFakerRequest(HttpServletRequest req, String netidHeaderName,
                String netid) {
            super(req);
            this.netidHeaderName = netidHeaderName;
            this.netid = netid;
        }

        @Override
        public Enumeration<String> getHeaderNames() {
            List<String> list = Collections.list(super.getHeaderNames());
            list.add(netidHeaderName);
            return Collections.enumeration(new HashSet<>(list));
        }

        @Override
        public String getHeader(String name) {
            if (name.equals(netidHeaderName)) {
                return netid;
            } else {
                return super.getHeader(name);
            }
        }

        @Override
        public Enumeration<String> getHeaders(String name) {
            if (name.equals(netidHeaderName)) {
                return Collections.enumeration(Collections.singleton(netid));
            } else {
                return super.getHeaders(name);
            }
        }

        @Override
        public int getIntHeader(String name) {
            if (name.equals(netidHeaderName)) {
                return 0;
            } else {
                return super.getIntHeader(name);
            }
        }

    }
}
