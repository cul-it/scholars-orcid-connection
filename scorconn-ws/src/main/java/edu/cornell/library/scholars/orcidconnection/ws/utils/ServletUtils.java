/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.library.scholars.orcidconnection.ws.utils;

import javax.servlet.http.HttpServletRequest;

/**
 * TODO
 */
public class ServletUtils {
    public static final String FILTER_CHECK_AUTH = "checkAuthFilter";
    public static final String FILTER_DISPLAY_STATUS = "displayStatusFilter";
    
    public static final String SERVLET_FAKE_LOGIN_PAGE = "FakeLoginPage";
    public static final String SERVLET_STARTUP_STATUS_PAGE = "StartupStatusPage";
    public static final String SERVLET_PROCESS_PUSH_REQUEST = "ProcessPushRequest";

    private static final String ATTRIBUTE_COMPLETION_URL = ServletUtils.class
            .getName() + "CompletionUrl";

    public static String getExternalAuthHeaderName() {
        return RuntimeProperties.getValue("externalAuth.headerName");
    }

    public static String getLocalId(HttpServletRequest req) {
        return req.getHeader(getExternalAuthHeaderName());
    }

    public static void setCompletionUrl(HttpServletRequest req, String url) {
        req.getSession().setAttribute(ATTRIBUTE_COMPLETION_URL, url);
    }

    public static String getCompletionUrl(HttpServletRequest req) {
        return (String) req.getSession().getAttribute(ATTRIBUTE_COMPLETION_URL);
    }

    private ServletUtils() {
        // No need to instantiate.
    }
}
