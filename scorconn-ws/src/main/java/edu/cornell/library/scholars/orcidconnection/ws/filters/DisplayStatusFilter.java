/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.library.scholars.orcidconnection.ws.filters;

import static edu.cornell.library.scholars.orcidconnection.ws.WebServerConstants.FILTER_DISPLAY_STATUS;
import static java.util.regex.Pattern.CASE_INSENSITIVE;

import java.io.IOException;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;

import edu.cornell.library.scholars.orcidconnection.ws.WebServerConstants;
import edu.cornell.library.scholars.orcidconnection.ws.utils.StartupStatus;

/**
 * TODO
 */
@WebFilter(filterName = FILTER_DISPLAY_STATUS)
public class DisplayStatusFilter implements Filter, WebServerConstants {

    public static final Pattern[] UNRESTRICTED_URLS = new Pattern[] {
            Pattern.compile(".*\\.png$", CASE_INSENSITIVE),
            Pattern.compile(".*\\.css$", CASE_INSENSITIVE) };

    @Override
    public void init(FilterConfig config) throws ServletException {
        // Nothing to initialize
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp,
            FilterChain chain) throws IOException, ServletException {
        new FilterCore(req, resp, chain).filter();
    }

    @Override
    public void destroy() {
        // Nothing to destroy
    }

    private static class FilterCore extends AbstractFilterCore {

        public FilterCore(ServletRequest req, ServletResponse resp,
                FilterChain chain) {
            super(req, resp, chain);
        }

        private void filter() throws IOException, ServletException {
            if (isRequestForUnrestrictedMaterial(UNRESTRICTED_URLS)) {
                chain.doFilter(req, resp);
            } else if (StartupStatus.getInstance().hasErrors()) {
                showStartupStatusPage();
            } else {
                chain.doFilter(req, resp);
            }
        }

        private void showStartupStatusPage()
                throws ServletException, IOException {
            RequestDispatcher dispatcher = req.getServletContext()
                    .getNamedDispatcher(SERVLET_STARTUP_STATUS);
            dispatcher.forward(req, resp);
        }
    }
}
