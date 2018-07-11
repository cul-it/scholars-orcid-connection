/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.library.scholars.orcidconnection.ws.filters;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;

import edu.cornell.library.scholars.orcidconnection.ws.utils.StartupStatus;

/**
 * TODO
 */
@WebFilter(filterName = "displayStatusFilter")
public class DisplayStatusFilter implements Filter {
    public static final String SERVLET_STARTUP_STATUS_PAGE = "StartupStatusPage";

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

    private static class FilterCore {
        private final ServletRequest req;
        private final ServletResponse resp;
        private final FilterChain chain;

        public FilterCore(ServletRequest req, ServletResponse resp,
                FilterChain chain) {
            this.req = req;
            this.resp = resp;
            this.chain = chain;
        }

        private void filter() throws IOException, ServletException {
            if (StartupStatus.getInstance().hasErrors()) {
                showStartupStatusPage();
            } else {
                chain.doFilter(req, resp);
            }
        }

        private void showStartupStatusPage()
                throws ServletException, IOException {
            RequestDispatcher dispatcher = req.getServletContext()
                    .getNamedDispatcher(SERVLET_STARTUP_STATUS_PAGE);
            dispatcher.forward(req, resp);
        }
    }
}
