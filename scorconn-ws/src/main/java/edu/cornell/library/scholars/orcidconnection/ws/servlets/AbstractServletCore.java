/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.library.scholars.orcidconnection.ws.servlets;

import static edu.cornell.library.orcidclient.actions.ApiScope.ACTIVITIES_UPDATE;
import static edu.cornell.library.orcidclient.auth.AccessToken.NO_TOKEN;
import static edu.cornell.library.scholars.orcidconnection.ws.utils.ServletUtils.getLocalId;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.cornell.library.orcidclient.actions.OrcidActionClient;
import edu.cornell.library.orcidclient.auth.AccessToken;
import edu.cornell.library.orcidclient.auth.AuthorizationStateProgress;
import edu.cornell.library.orcidclient.auth.OrcidAuthorizationClient;
import edu.cornell.library.orcidclient.context.OrcidClientContext;
import edu.cornell.library.orcidclient.exceptions.OrcidClientException;
import edu.cornell.library.orcidclient.http.BaseHttpWrapper;
import edu.cornell.library.scholars.orcidconnection.accesstokens.BogusCache;

/**
 * Variables and methods that will come in handy to the ServletCore instances.
 */
public class AbstractServletCore {
    protected final HttpServletRequest req;
    protected final HttpServletResponse resp;
    protected final String localId;
    protected final BogusCache cache;
    protected final OrcidClientContext occ;
    protected final OrcidAuthorizationClient authClient;
    protected final OrcidActionClient actionClient;

    public AbstractServletCore(HttpServletRequest req,
            HttpServletResponse resp) {
        this.req = req;
        this.resp = resp;
        this.localId = getLocalId(req);
        this.cache = BogusCache.getCache(req, localId);
        this.occ = OrcidClientContext.getInstance();
        this.authClient = new OrcidAuthorizationClient(occ, cache,
                new BaseHttpWrapper());
        this.actionClient = new OrcidActionClient(occ, new BaseHttpWrapper());
    }

    protected AuthorizationStateProgress getCachedProgress() {
        try {
            return cache.getByScope(ACTIVITIES_UPDATE);
        } catch (OrcidClientException e) {
            throw new RuntimeException("Failed to read the cache.", e);
        }
    }

    protected AccessToken getCachedAccessToken() {
        AuthorizationStateProgress progress = getCachedProgress();
        if (progress == null) {
            return NO_TOKEN;
        } else {
            return progress.getAccessToken();
        }
    }

}
