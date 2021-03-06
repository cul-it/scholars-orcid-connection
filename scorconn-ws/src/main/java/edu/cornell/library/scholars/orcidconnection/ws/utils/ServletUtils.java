/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.library.scholars.orcidconnection.ws.utils;

import static edu.cornell.library.scholars.orcidconnection.scholarslink.ScholarsLink.PROPERTY_SCHOLARS_BASE_URL;

import javax.servlet.http.HttpServletRequest;

import edu.cornell.library.orcidclient.auth.AccessTokenCache;
import edu.cornell.library.orcidclient.auth.OauthProgressCache;
import edu.cornell.library.orcidclient.auth.OauthProgressCacheImpl;
import edu.cornell.library.orcidclient.auth.OrcidAuthorizationClient;
import edu.cornell.library.orcidclient.context.OrcidClientContext;
import edu.cornell.library.orcidclient.http.BaseHttpWrapper;
import edu.cornell.library.scholars.orcidconnection.accesstokens.AccessTokenCacheDataLayerImpl;

/**
 * Some methods that might come in handy for filters and servlets.
 */
public class ServletUtils {

    public static String getExternalAuthHeaderName() {
        return RuntimeProperties.getValue("externalAuth.headerName");
    }

    public static String getLocalId(HttpServletRequest req) {
        return req.getHeader(getExternalAuthHeaderName());
    }

    public static OrcidAuthorizationClient getAuthorizationClient(
            HttpServletRequest req) {
        OrcidClientContext occ = OrcidClientContext.getInstance();
        BaseHttpWrapper httpWrapper = new BaseHttpWrapper();
        OauthProgressCache progressCache = OauthProgressCacheImpl.instance(req);
        AccessTokenCache tokenCache = AccessTokenCacheDataLayerImpl
                .instance(getLocalId(req));
        return new OrcidAuthorizationClient(occ, progressCache, tokenCache,
                httpWrapper);
    }

    public static String getOrcidRecordPageUrl(String orcidId) {
        return OrcidClientContext.getInstance().getSiteBaseUrl() + orcidId;
    }

    public static String getExitUrl(String localId) {
        if (localId == null) {
            return null;
        }

        String url = RuntimeProperties.getValue(PROPERTY_SCHOLARS_BASE_URL);
        if (!url.endsWith("/")) {
            url += "/";
        }
        return url + "display/" + localId;
    }

    private ServletUtils() {
        // No need to instantiate.
    }
}
