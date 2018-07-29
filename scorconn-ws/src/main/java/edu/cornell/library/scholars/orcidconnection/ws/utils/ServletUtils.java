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
