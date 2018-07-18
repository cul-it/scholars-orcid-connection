/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.library.scholars.orcidconnection.ws.utils;

import javax.servlet.http.HttpServletRequest;

import edu.cornell.library.orcidclient.auth.AuthorizationStateProgressCache;
import edu.cornell.library.orcidclient.auth.OrcidAuthorizationClient;
import edu.cornell.library.orcidclient.context.OrcidClientContext;
import edu.cornell.library.orcidclient.http.BaseHttpWrapper;

/**
 * TODO
 */
public class ServletUtils {
    public static final String FILTER_CHECK_AUTH = "checkAuthFilter";
    public static final String FILTER_DISPLAY_STATUS = "displayStatusFilter";

    public static final String SERVLET_FAKE_LOGIN_PAGE = "FakeLoginPage";
    public static final String SERVLET_STARTUP_STATUS_PAGE = "StartupStatusPage";
    public static final String SERVLET_PROCESS_PUSH_REQUEST = "ProcessPushRequest";

    public static final String TEMPLATE_FAKE_LOGIN_PAGE = "/templates/fakeLoginPage.twig.html";
    public static final String TEMPLATE_LANDING_PAGE = "/templates/landingPage.twig.html";
    public static final String TEMPLATE_ERROR_PAGE = "/templates/errorPage.twig.html";
    public static final String TEMPLATE_USER_DENIED_ACCESS_PAGE = "/templates/userDeniedAccessPage.twig.html";
    public static final String TEMPLATE_ACKNOWLEDGE_PUSH_PROCESSING_PAGE = "/templates/acknowledgePushProcessingPage.twig.html";
    public static final String TEMPLATE_INVALID_TOKEN_PAGE = "/templates/invalidTokenPage.twig.html";
    public static final String TEMPLATE_STARTUP_STATUS_PAGE = "/templates/startupStatusPage.twig.html";

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

    public static OrcidAuthorizationClient getAuthorizationClient(
            AuthorizationStateProgressCache cache) {
        OrcidClientContext occ = OrcidClientContext.getInstance();
        BaseHttpWrapper httpWrapper = new BaseHttpWrapper();
        return new OrcidAuthorizationClient(occ, cache, httpWrapper);
    }

    private ServletUtils() {
        // No need to instantiate.
    }
}
