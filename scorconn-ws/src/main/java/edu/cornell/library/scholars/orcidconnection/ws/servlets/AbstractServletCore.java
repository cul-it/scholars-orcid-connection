/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.library.scholars.orcidconnection.ws.servlets;

import static edu.cornell.library.orcidclient.actions.ApiScope.ACTIVITIES_UPDATE;
import static edu.cornell.library.orcidclient.auth.AccessToken.NO_TOKEN;
import static edu.cornell.library.scholars.orcidconnection.ws.WebServerConstants.ATTRIBUTE_ERROR_MESSAGE;
import static edu.cornell.library.scholars.orcidconnection.ws.WebServerConstants.SERVLET_SHOW_ERROR;
import static edu.cornell.library.scholars.orcidconnection.ws.utils.ServletUtils.getLocalId;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.cornell.library.orcidclient.actions.OrcidActionClient;
import edu.cornell.library.orcidclient.auth.AccessToken;
import edu.cornell.library.orcidclient.auth.AccessTokenCache;
import edu.cornell.library.orcidclient.auth.OauthProgress;
import edu.cornell.library.orcidclient.auth.OauthProgressCache;
import edu.cornell.library.orcidclient.auth.OauthProgressCacheImpl;
import edu.cornell.library.orcidclient.auth.OrcidAuthorizationClient;
import edu.cornell.library.orcidclient.context.OrcidClientContext;
import edu.cornell.library.orcidclient.exceptions.OrcidClientException;
import edu.cornell.library.orcidclient.http.BaseHttpWrapper;
import edu.cornell.library.scholars.orcidconnection.accesstokens.AccessTokenCacheDataLayerImpl;

/**
 * Variables and methods that will come in handy to the ServletCore instances.
 */
public class AbstractServletCore {
    protected final HttpServletRequest req;
    protected final HttpServletResponse resp;
    protected final String localId;
    protected final OauthProgressCache progressCache;
    protected final AccessTokenCache tokenCache;
    protected final OrcidClientContext occ;
    protected final OrcidAuthorizationClient authClient;
    protected final OrcidActionClient actionClient;

    public AbstractServletCore(HttpServletRequest req,
            HttpServletResponse resp) {
        this.req = req;
        this.resp = resp;
        this.localId = getLocalId(req);
        this.progressCache = OauthProgressCacheImpl.instance(req);
        this.tokenCache = AccessTokenCacheDataLayerImpl.instance(localId);
        this.occ = OrcidClientContext.getInstance();
        this.authClient = new OrcidAuthorizationClient(occ, progressCache,
                tokenCache, new BaseHttpWrapper());
        this.actionClient = new OrcidActionClient(occ, new BaseHttpWrapper());
    }

    protected OauthProgress getCachedProgress() {
        try {
            return progressCache.getByScope(ACTIVITIES_UPDATE);
        } catch (OrcidClientException e) {
            throw new RuntimeException("Failed to read the cache.", e);
        }
    }

    protected AccessToken getCachedAccessToken() throws OrcidClientException {
        AccessToken token = tokenCache.getToken(ACTIVITIES_UPDATE);
        return (token == null) ? NO_TOKEN : token;
    }

    protected void redirectToController(String controllerPath)
            throws IOException {
        try {
            URI controllerUri = occ.resolvePathWithWebapp(controllerPath);
            resp.sendRedirect(controllerUri.toString());
        } catch (URISyntaxException e) {
            throw new RuntimeException("ORCID context failed to resolve", e);
        }
    }

    protected void redirectToErrorPage(String message) throws IOException {
        req.getSession().setAttribute(ATTRIBUTE_ERROR_MESSAGE, message);
        redirectToController(SERVLET_SHOW_ERROR);
    }

}
