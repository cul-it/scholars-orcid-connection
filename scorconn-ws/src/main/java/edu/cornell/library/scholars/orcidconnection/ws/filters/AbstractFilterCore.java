/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.library.scholars.orcidconnection.ws.filters;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.FilterChain;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Common routines for the core classes of servlet filters.
 */
public abstract class AbstractFilterCore {
    private static final Log log = LogFactory.getLog(AbstractFilterCore.class);

    protected final HttpServletRequest req;
    protected final HttpServletResponse resp;
    protected final HttpSession session;
    protected final FilterChain chain;

    AbstractFilterCore(ServletRequest req, ServletResponse resp,
            FilterChain chain) {
        this.req = (HttpServletRequest) req;
        this.resp = (HttpServletResponse) resp;
        this.session = this.req.getSession();
        this.chain = chain;

        log.debug(String.format("Doing the filter: %s, requestURI is '%s'",
                this.getClass().getSimpleName(), this.req.getRequestURI()));
    }

    protected boolean isRequestForUnrestrictedMaterial(
            Pattern[] unrestrictedUrls) {
        for (Pattern p : unrestrictedUrls) {
            Matcher m = p.matcher(req.getRequestURI());
            if (m.matches()) {
                log.debug("request is unrestricted: " + p);
                return true;
            }
        }
        log.debug("request is restricted");
        return false;
    }
}
