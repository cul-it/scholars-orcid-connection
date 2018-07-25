/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.library.scholars.orcidconnection.publications;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.cornell.library.scholars.orcidconnection.data.DataLayer;
import edu.cornell.library.scholars.orcidconnection.data.DataLayerException;
import edu.cornell.library.scholars.orcidconnection.data.mapping.Person;
import edu.cornell.library.scholars.orcidconnection.data.mapping.Work;

/**
 * Ask the database what Works have been recorded for this user. We will need to
 * know the hash code for each work.
 */
public class PublicationsFromDatabaseList implements PublicationsFromDatabase {
    private final Map<String, Integer> scholarsUriToHash = new HashMap<>();

    public PublicationsFromDatabaseList(String localId)
            throws DataLayerException {
        Person person = DataLayer.instance().findPerson(localId);
        if (person != null) {
            List<Work> works = DataLayer.instance()
                    .findWorks(person.getOrcidId());
            for (Work work : works) {
                scholarsUriToHash.put(work.getScholarsUri(), work.getHash());
            }
        }
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
        return String.valueOf(scholarsUriToHash.get(scholarsUri));
    }

}
