/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.library.scholars.orcidconnection.publications;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.cornell.library.scholars.orcidconnection.scholarslink.ScholarsLink;
import edu.cornell.library.scholars.orcidconnection.scholarslink.ScholarsLinkException;

/**
 * Ask Scholars for a list of publications for an individual.
 */
public class PublicationsFromScholarsList implements PublicationsFromScholars {
    private final Map<String, Publication> publications;

    public PublicationsFromScholarsList(String localId) throws ScholarsLinkException {
        List<Publication> pubs = ScholarsLink.instance().getPublications(localId);

        Map<String, Publication> map = new HashMap<>();
        for (Publication pub: pubs) {
            map.put(pub.getScholarsUri(), pub);
        }
        this.publications = map;
    }

    @Override
    public Set<String> getUris() {
        return publications.keySet();
    }

    @Override
    public boolean hasUri(String scholarsUri) {
        return publications.containsKey(scholarsUri);
    }

    @Override
    public Publication getPublication(String scholarsUri) {
        return publications.get(scholarsUri);
    }

    @Override
    public String getHash(String scholarsUri) {
        return String.valueOf(publications.get(scholarsUri).hashCode());
    }

}
