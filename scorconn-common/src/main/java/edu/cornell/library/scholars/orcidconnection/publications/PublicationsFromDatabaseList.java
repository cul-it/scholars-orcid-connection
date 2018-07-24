/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.library.scholars.orcidconnection.publications;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * TODO
 */
public class PublicationsFromDatabaseList implements PublicationsFromDatabase {
    private static final Log log = LogFactory
            .getLog(PublicationsFromDatabaseList.class);

    private final Map<String, String> scholarsUriToHash = new HashMap<>();

    /**
     * @param localId
     */
    public PublicationsFromDatabaseList(String localId) {
        log.error(
                "BOGUS -- PublicationsFromDatabaseList Constructor not implemented.");
    }

    @Override
    public Set<String> getUris() {
        return scholarsUriToHash.keySet();
    }

    @Override
    public boolean hasUri(String scholarsUri) {
        return scholarsUriToHash.containsKey(scholarsUri);
    }

    @Override
    public String getHash(String scholarsUri) {
        return scholarsUriToHash.get(scholarsUri);
    }

}
