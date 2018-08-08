/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.library.scholars.orcidconnection.data;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.library.scholars.orcidconnection.data.mapping.LogEntry;
import edu.cornell.library.scholars.orcidconnection.data.mapping.Person;
import edu.cornell.library.scholars.orcidconnection.data.mapping.Token;
import edu.cornell.library.scholars.orcidconnection.data.mapping.Work;

/**
 * A wrapper around the persistence layer.
 */
public abstract class DataLayer {
    private static final Log log = LogFactory.getLog(DataLayer.class);

    // ----------------------------------------------------------------------
    // The factory
    // ----------------------------------------------------------------------

    private static volatile DataLayer instance = new DataLayerNotInitialized();

    public static synchronized void initialize(DataLayer newInstance) {
        if (instance == null || instance instanceof DataLayerNotInitialized) {
            instance = newInstance;
            log.debug("initialized: " + instance);
        } else {
            throw new IllegalStateException("Already initialized: " + instance);
        }
    }

    public static DataLayer instance() {
        return instance;
    }

    // ----------------------------------------------------------------------
    // The interface
    // ----------------------------------------------------------------------

    public abstract void checkConnection() throws DataLayerException;

    public abstract void writeAccessToken(Token accessToken)
            throws DataLayerException;

    public abstract Token findAccessToken(String localId, String scope)
            throws DataLayerException;

    public abstract void writePerson(Person person) throws DataLayerException;

    public abstract Person findPerson(String localId) throws DataLayerException;

    public abstract void writeWork(Work work) throws DataLayerException;

    public abstract Work findWork(String scholarsUri, String orcidId)
            throws DataLayerException;

    public abstract List<Work> findWorks(String orcidId)
            throws DataLayerException;

    public abstract void deleteWork(String scholarsUri, String orcidId)
            throws DataLayerException;

    public abstract void writeLogEntry(LogEntry logEntry)
            throws DataLayerException;

    // ----------------------------------------------------------------------
    // The empty implementation
    // ----------------------------------------------------------------------

    private static class DataLayerNotInitialized extends DataLayer {
        private static final String MESSAGE = "DataLayer has not been initialized";

        @Override
        public void checkConnection() throws DataLayerException {
            throw new IllegalStateException(MESSAGE);
        }

        @Override
        public void writeAccessToken(Token accessToken) {
            throw new IllegalStateException(MESSAGE);
        }

        @Override
        public Token findAccessToken(String localId, String scope) {
            throw new IllegalStateException(MESSAGE);
        }

        @Override
        public void writePerson(Person person) {
            throw new IllegalStateException(MESSAGE);
        }

        @Override
        public Person findPerson(String localId) {
            throw new IllegalStateException(MESSAGE);
        }

        @Override
        public void writeWork(Work work) {
            throw new IllegalStateException(MESSAGE);
        }

        @Override
        public Work findWork(String scholarsUri, String orcidId) {
            throw new IllegalStateException(MESSAGE);
        }

        @Override
        public List<Work> findWorks(String orcidId) {
            throw new IllegalStateException(MESSAGE);
        }

        @Override
        public void deleteWork(String scholarsUri, String orcidId) {
            throw new IllegalStateException(MESSAGE);
        }

        @Override
        public void writeLogEntry(LogEntry logEntry) throws DataLayerException {
            throw new IllegalStateException(MESSAGE);
        }
    }
}
