/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.library.scholars.orcidconnection.accesstokens;

import static edu.cornell.library.orcidclient.auth.AuthorizationStateProgress.NO_URI;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.library.orcidclient.actions.ApiScope;
import edu.cornell.library.orcidclient.auth.AccessToken;
import edu.cornell.library.orcidclient.auth.AuthorizationStateProgress;
import edu.cornell.library.orcidclient.auth.AuthorizationStateProgressCache;
import edu.cornell.library.orcidclient.exceptions.OrcidClientException;

/**
 * A cache that starts off with one entry, and will temporarily accept more.
 */
public class BogusCache implements AuthorizationStateProgressCache {
    private static final Log log = LogFactory.getLog(BogusCache.class);
    
    public static synchronized BogusCache getCache(HttpServletRequest req, String netId) {
        Object cache = req.getSession().getAttribute("BOGUS_CACHE");
        if (cache == null) {
            cache = new BogusCache(netId);
            req.getSession().setAttribute("BOGUS_CACHE", cache);
        }
        return (BogusCache) cache;
    }

    private final String netId;
    private final Map<String, AuthorizationStateProgress> progressMap = new HashMap<>();

    private BogusCache(String netId) {
        this.netId = netId;

        if (netId.equals("bogus")) {
            AuthorizationStateProgress dummyEntry = buildTheDummyEntry();
            progressMap.put(dummyEntry.getId(), dummyEntry);
            log.error("BOGUS -- BogusCache with bogus entry" + progressMap);
        } else {
            log.error("BOGUS -- BogusCache");
        }
    }

    private AuthorizationStateProgress buildTheDummyEntry() {
        try {
            AccessToken token = AccessToken.parse("{" //
                    + "\"access_token\":\"2147393a-3f88-4d53-9409-f3f1f86a97c4\","
                    + "\"token_type\":\"bearer\","
                    + "\"refresh_token\":\"68ba737a-90be-4cc9-9ab4-d4989b653437\","
                    + "\"expires_in\":631138518,"
                    + "\"scope\":\"/person/update\","
                    + "\"name\":\"James1 Blake\","
                    + "\"orcid\":\"0000-0003-0550-2950\"" + "}");
            AuthorizationStateProgress asp = AuthorizationStateProgress
                    .create(token.getScope(), NO_URI, NO_URI);
            return asp.addAccessToken(token);
        } catch (OrcidClientException e) {
            throw new RuntimeException(
                    "Failed to parse hard-coded access token!", e);
        }
    }

    @Override
    public void store(AuthorizationStateProgress progress)
            throws OrcidClientException {
        progressMap.put(progress.getId(), progress);
    }

    @Override
    public AuthorizationStateProgress getByID(String id)
            throws OrcidClientException {
        log.debug("BOGUS -- get by ID: " + id + "\n" + progressMap);
        return progressMap.get(id);
    }

    @Override
    public AuthorizationStateProgress getByScope(ApiScope scope)
            throws OrcidClientException {
        for (AuthorizationStateProgress progress : progressMap.values()) {
            if (scope == progress.getScope()) {
                return progress;
            }
        }
        return null;
    }

    @Override
    public void clearScopeProgress(ApiScope scope) throws OrcidClientException {
        Iterator<AuthorizationStateProgress> it = progressMap.values()
                .iterator();
        while (it.hasNext()) {
            AuthorizationStateProgress progress = it.next();
            if (progress.getScope() == scope) {
                it.remove();
            }
        }
    }

}
