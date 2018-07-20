/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.library.scholars.orcidconnection;

import static edu.cornell.library.scholars.orcidconnection.data.mapping.LogEntry.Severity.INFO;

import java.util.List;

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
import edu.cornell.library.scholars.orcidconnection.scholarslink.Publication;

/**
 * TODO
 * 
 * Full implementation (phase 1):
 * 
 * <pre>
 * Get the list of Publications from Scholars
 * 
 * Ask ORCID for all of the Works.
 * Tell ORCID to delete all of the Publications that we created.
 * 
 * Tell ORCID to add the Publications that we got from Scholars.
 * </pre>
 * 
 * Partial implementation
 * 
 * <pre>
 * Just push them, one at a time, and throw an exception if it won't go.
 * </pre>
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
            List<Publication> pubsNow = ScholarsOrcidConnection.instance()
                    .getPublications(localId);
            log.info(String.format("Pushing %d publications for %s",
                    pubsNow.size(), localId));

            for (Publication pub : pubsNow) {
                pushOne(pub);
            }
        } catch (Exception e) {
            log.error("Failed to push publications", e);
        }
    }

    private void pushOne(Publication pub) throws OrcidClientException {
        WorkElement work = pub.toOrcidWork();
        String putCode = actions.createEditWorksAction().add(accessToken, work);
        writePubToDB(pub, putCode);
        DbLogger.writeLogEntry(INFO, "Pushed publication %s, put code was %s",
                pub.getScholarsUri(), putCode);
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
