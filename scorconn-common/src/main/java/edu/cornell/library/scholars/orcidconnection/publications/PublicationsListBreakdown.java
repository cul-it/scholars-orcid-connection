/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.library.scholars.orcidconnection.publications;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Gather the list of publications for one person. Figure out what to do with
 * them.
 * 
 * Gather the lists from Scholars, from ORCID, and from the database. For each
 * publication, decide whether to ADD, UPDATE, DELETE, IGNORE, or COMPLAIN.
 * </pre>
 */
public class PublicationsListBreakdown {
    private static final Log log = LogFactory
            .getLog(PublicationsListBreakdown.class);

    private final PublicationsFromScholars pubsFromScholars;
    private final PublicationsFromDatabase pubsFromDatabase;
    private final PublicationsFromOrcid pubsFromOrcid;

    private final Map<String, Publication> adds = new HashMap<>();
    private final Map<String, PublicationUpdate> updates = new HashMap<>();
    private final Map<String, String> deletes = new HashMap<>();
    private final Set<String> ignores = new HashSet<>();
    private final Set<String> surprises = new HashSet<>();

    public PublicationsListBreakdown(PublicationsFromScholars pubsFromScholars,
            PublicationsFromDatabase pubsFromDatabase,
            PublicationsFromOrcid pubsFromOrcid) {
        this.pubsFromScholars = pubsFromScholars;
        this.pubsFromDatabase = pubsFromDatabase;
        this.pubsFromOrcid = pubsFromOrcid;

        breakdownTheLists();
    }

    private void breakdownTheLists() {
        for (String uri : allUris()) {
            if (isNewPublication(uri)) {
                adds.put(uri, publicationForUri(uri));
            } else if (pubHasChanged(uri)) {
                updates.put(uri, new PublicationUpdate(putcodeForUri(uri),
                        publicationForUri(uri)));
            } else if (pubIsTheSame(uri)) {
                ignores.add(uri);
            } else if (userDeletedFromOrcid(uri)) {
                ignores.add(uri);
            } else if (wasDeletedFromScholars(uri)) {
                deletes.put(uri, putcodeForUri(uri));
            } else { // some strange status
                surprises.add(uri);
            }
        }
        logAnySurprises();
    }

    private Set<String> allUris() {
        Set<String> allUris = new HashSet<>();
        allUris.addAll(pubsFromScholars.getUris());
        allUris.addAll(pubsFromDatabase.getUris());
        allUris.addAll(pubsFromOrcid.getUris());
        return allUris;
    }

    private boolean isNewPublication(String uri) {
        return pubsFromScholars.hasUri(uri) //
                && !pubsFromOrcid.hasUri(uri) //
                && !pubsFromDatabase.hasUri(uri);
    }

    private boolean pubHasChanged(String uri) {
        return pubsFromScholars.hasUri(uri) && //
                !pubsFromOrcid.hasUri(uri) //
                && !pubsFromScholars.getHash(uri)
                        .equals(pubsFromDatabase.getHash(uri));
    }

    private boolean pubIsTheSame(String uri) {
        return pubsFromScholars.hasUri(uri) && //
                !pubsFromOrcid.hasUri(uri) //
                && pubsFromScholars.getHash(uri)
                        .equals(pubsFromDatabase.getHash(uri));
    }

    private boolean userDeletedFromOrcid(String uri) {
        return pubsFromScholars.hasUri(uri) && //
                !pubsFromOrcid.hasUri(uri) && pubsFromDatabase.hasUri(uri);
    }

    private boolean wasDeletedFromScholars(String uri) {
        return !pubsFromScholars.hasUri(uri) && //
                pubsFromOrcid.hasUri(uri) && pubsFromDatabase.hasUri(uri);
    }

    private Publication publicationForUri(String uri) {
        return pubsFromScholars.getPublication(uri);
    }

    private String putcodeForUri(String uri) {
        return pubsFromOrcid.getPutCode(uri);
    }

    private void logAnySurprises() {
        for (String uri : surprises) {
            String scholarsStatus = "absent";
            if (pubsFromScholars.hasUri(uri)) {
                scholarsStatus = String.format("present (hash=%s)",
                        pubsFromScholars.getHash(uri));
            }

            String databaseStatus = "absent";
            if (pubsFromDatabase.hasUri(uri)) {
                databaseStatus = String.format("present (hash=%s)",
                        pubsFromDatabase.getHash(uri));
            }

            String orcidStatus = "absent";
            if (pubsFromOrcid.hasUri(uri)) {
                orcidStatus = String.format("present (putcode=%s)",
                        pubsFromOrcid.getPutCode(uri));
            }

            log.warn(String.format(
                    "Surprising status for publication '%s', scholars: %s, database: %s, orcid: %s",
                    uri, scholarsStatus, databaseStatus, orcidStatus));
        }
    }

    public Set<Publication> getPublicationsToAdd() {
        return new HashSet<>(adds.values());
    }

    public Collection<PublicationUpdate> getPublicationsToUpdate() {
        return updates.values();
    }

    public Set<String> getPutCodesToDelete() {
        return new HashSet<>(deletes.values());
    }

    public Set<String> getPubUrisToAdd() {
        return adds.keySet();
    }

    public Set<String> getPubUrisToUpdate() {
        return updates.keySet();
    }

    public Set<String> getPubUrisToDelete() {
        return deletes.keySet();
    }

    public Set<String> getPubUrisToIgnore() {
        return ignores;
    }

    // ----------------------------------------------------------------------
    // Helper class
    // ----------------------------------------------------------------------

    public static class PublicationUpdate {
        public final String putCode;
        public final Publication publication;

        public PublicationUpdate(String putCode, Publication publication) {
            this.putCode = putCode;
            this.publication = publication;
        }
    }
}
