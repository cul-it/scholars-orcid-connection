/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.library.scholars.orcidconnection.ws;

/**
 * Useful values for servlets and filters.
 */
public interface WebServerConstants {
    static final String FILTER_CHECK_AUTH = "checkAuthFilter";
    static final String FILTER_DISPLAY_STATUS = "displayStatusFilter";

    static final String SERVLET_FAKE_LOGIN_PAGE = "FakeLoginPage";
    static final String SERVLET_STARTUP_STATUS_PAGE = "StartupStatusPage";
    static final String SERVLET_PROCESS_PUSH_REQUEST = "ProcessPushRequest";

    static final String TEMPLATE_FAKE_LOGIN_PAGE = "/templates/fakeLoginPage.twig.html";
    static final String TEMPLATE_LANDING_WITHOUT_TOKEN_PAGE = "/templates/landingPageNoToken.twig.html";
    static final String TEMPLATE_LANDING_WITH_TOKEN_PAGE = "/templates/landingPageValidToken.twig.html";
    static final String TEMPLATE_LANDING_INVALID_TOKEN_PAGE = "/templates/landingPageInvalidToken.twig.html";
    static final String TEMPLATE_ERROR_PAGE = "/templates/errorPage.twig.html";
    static final String TEMPLATE_USER_DENIED_ACCESS_PAGE = "/templates/userDeniedAccessPage.twig.html";
    static final String TEMPLATE_ACKNOWLEDGE_PUSH_PROCESSING_PAGE = "/templates/acknowledgePushProcessingPage.twig.html";
    static final String TEMPLATE_STARTUP_STATUS_PAGE = "/templates/startupStatusPage.twig.html";

}
