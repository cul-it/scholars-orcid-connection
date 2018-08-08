/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.library.scholars.orcidconnection.accesstokens;

import edu.cornell.library.orcidclient.actions.ApiScope;
import edu.cornell.library.orcidclient.auth.AccessToken;
import edu.cornell.library.orcidclient.auth.AccessTokenCache;
import edu.cornell.library.orcidclient.exceptions.OrcidClientException;
import edu.cornell.library.scholars.orcidconnection.data.DataLayer;
import edu.cornell.library.scholars.orcidconnection.data.DataLayerException;
import edu.cornell.library.scholars.orcidconnection.data.mapping.Person;

/**
 * TODO
 */
public class AccessTokenCacheDataLayerImpl implements AccessTokenCache {

    // ----------------------------------------------------------------------
    // Factory
    // ----------------------------------------------------------------------

    public static AccessTokenCacheDataLayerImpl instance(String localId) {
        return new AccessTokenCacheDataLayerImpl(localId);
    }

    // ----------------------------------------------------------------------
    // The instance
    // ----------------------------------------------------------------------

    private final String localId;

    private AccessTokenCacheDataLayerImpl(String localId) {
        this.localId = localId;
    }

    @Override
    public void addAccessToken(AccessToken token) throws OrcidClientException {
        try {
            DataLayer.instance().writePerson(
                    new Person(localId, token.getOrcid(), token.getName()));
            DataLayer.instance().writeAccessToken(
                    new edu.cornell.library.scholars.orcidconnection.data.mapping.AccessToken(
                            token.getOrcid(), token.getScope().getScope(),
                            token.getToken(), token.getType(),
                            token.getRefreshToken(), token.getExpiresIn(),
                            token.getJsonString()));
        } catch (DataLayerException e) {
            throw new OrcidClientException("Failed to write the access token.",
                    e);
        }
    }

    @Override
    public AccessToken getToken(ApiScope scope) throws OrcidClientException {
        try {
            Person person = DataLayer.instance().findPerson(localId);
            if (person == null) {
                return null;
            }
            edu.cornell.library.scholars.orcidconnection.data.mapping.AccessToken token = DataLayer
                    .instance()
                    .findAccessToken(person.getOrcidId(), scope.getScope());
            if (token == null) {
                return null;
            } else {
                return new AccessToken(token.getJson(), token.getAccessToken(),
                        token.getTokenType(), token.getRefreshToken(),
                        token.getExpiresIn(), ApiScope.parse(token.getScope()),
                        "BOGUS", token.getOrcidId());
            }
        } catch (DataLayerException e) {
            throw new OrcidClientException("Failed to read the access token.",
                    e);
        }
    }

}
