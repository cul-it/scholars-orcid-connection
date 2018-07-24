/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.library.scholars.orcidconnection;

import static edu.cornell.library.scholars.orcidconnection.data.mapping.LogEntry.Category.DELETED;
import static edu.cornell.library.scholars.orcidconnection.data.mapping.LogEntry.Category.PUSHED;
import static edu.cornell.library.scholars.orcidconnection.data.mapping.LogEntry.Category.UPDATED;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import edu.cornell.library.orcidclient.actions.OrcidActionClient;
import edu.cornell.library.orcidclient.auth.AccessToken;
import edu.cornell.library.orcidclient.context.OrcidClientContext;
import edu.cornell.library.orcidclient.exceptions.OrcidClientException;
import edu.cornell.library.orcidclient.http.BaseHttpWrapper;
import edu.cornell.library.orcidclient.orcid_message_2_1.work.WorkElement;
import edu.cornell.library.scholars.orcidconnection.data.DbLogger;
import edu.cornell.library.scholars.orcidconnection.data.HibernateUtil;
import edu.cornell.library.scholars.orcidconnection.data.mapping.Work;
import edu.cornell.library.scholars.orcidconnection.publications.Publication;
import edu.cornell.library.scholars.orcidconnection.publications.PublicationsFromDatabaseList;
import edu.cornell.library.scholars.orcidconnection.publications.PublicationsFromOrcidList;
import edu.cornell.library.scholars.orcidconnection.publications.PublicationsFromScholarsList;
import edu.cornell.library.scholars.orcidconnection.publications.PublicationsListBreakdown;
import edu.cornell.library.scholars.orcidconnection.publications.PublicationsListBreakdown.PublicationUpdate;

/**
 * Figure out which publications must be changed in ORCID, and do it.
 * 
 * Read the Publications from ORCID, from Scholars, and from the Database
 * record. Do the breakdown, to find out which ones must be add, updated, or
 * deleted.
 * 
 * TODO -- If we were really clever, we could do bulk updates, etc.
 */
public class PublicationsUpdateProcessor extends Thread {
    private static final Log log = LogFactory
            .getLog(PublicationsUpdateProcessor.class);
    private final String localId;
    private final AccessToken accessToken;
    private final OrcidActionClient actions;

    public PublicationsUpdateProcessor(String localId,
            AccessToken accessToken) {
        this.localId = localId;
        this.accessToken = accessToken;

        this.actions = new OrcidActionClient(OrcidClientContext.getInstance(),
                new BaseHttpWrapper());
    }

    @Override
    public synchronized void run() {
        try {
            PublicationsFromScholarsList pubsFromScholars = new PublicationsFromScholarsList(
                    localId);
            PublicationsFromDatabaseList pubsFromDatabase = new PublicationsFromDatabaseList(
                    localId);
            PublicationsFromOrcidList pubsFromOrcid = new PublicationsFromOrcidList(
                    accessToken);
            PublicationsListBreakdown breakdown = new PublicationsListBreakdown(
                    pubsFromScholars, pubsFromDatabase, pubsFromOrcid);

            log.debug("PUBS TO ADD:" + breakdown.getPubUrisToAdd());
            log.debug("PUBS TO UPDATE:" + breakdown.getPubUrisToUpdate());
            log.debug("PUBS TO DELETE:" + breakdown.getPubUrisToDelete());
            log.debug("PUBS TO IGNORE:" + breakdown.getPubUrisToIgnore());

            for (String putCode : breakdown.getPutCodesToDelete()) {
                deleteOne(putCode);
            }
            for (Publication pub : breakdown.getPublicationsToAdd()) {
                addOne(pub);
            }
            for (PublicationUpdate update : breakdown
                    .getPublicationsToUpdate()) {
                updateOne(update.putCode, update.publication);
            }
        } catch (Exception e) {
            log.error("Failed to update publications", e);
        }
    }

    private void deleteOne(String putCode) throws OrcidClientException {
        actions.createEditWorksAction().remove(accessToken, putCode);
        writePubToDB(Publication.emptyPub(), putCode);
        DbLogger.writeLogEntry(DELETED, "Deleted publication with put code %s",
                putCode);
    }

    private void addOne(Publication pub) throws OrcidClientException {
        WorkElement work = pub.toOrcidWork();
        String putCode = actions.createEditWorksAction().add(accessToken, work);
        writePubToDB(pub, putCode);
        DbLogger.writeLogEntry(PUSHED, "Pushed publication %s, put code was %s",
                pub.getScholarsUri(), putCode);
    }

    private void updateOne(String putCode, Publication pub)
            throws OrcidClientException {
        WorkElement work = pub.toOrcidWork();
        actions.createEditWorksAction().update(accessToken, work, putCode);
        writePubToDB(pub, putCode);
        DbLogger.writeLogEntry(UPDATED,
                "Pushed publication %s, put code was %s", pub.getScholarsUri(),
                putCode);
    }

    private void writePubToDB(Publication pub, String putCode) {
        Work w = new Work();
        w.setOrcidId(accessToken.getOrcid());
        w.setScholarsUri(pub.getScholarsUri());
        w.setPutCode(putCode);

        SessionFactory factory = HibernateUtil.getSessionFactory();
        try (Session session = factory.openSession()) {
            session.beginTransaction();
            session.save(w);
            session.getTransaction().commit();
        }
    }
}
