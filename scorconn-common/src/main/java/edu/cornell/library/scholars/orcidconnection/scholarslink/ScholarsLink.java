/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.library.scholars.orcidconnection.scholarslink;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.library.scholars.orcidconnection.publications.Publication;

/**
 * The interface used for querying Scholars.
 */
public abstract class ScholarsLink {
    private static final Log log = LogFactory.getLog(ScholarsLink.class);

    public static final String PROPERTY_SCHOLARS_BASE_URL = "scholarsBaseUrl";

    // ----------------------------------------------------------------------
    // The factory
    // ----------------------------------------------------------------------

    private static volatile ScholarsLink instance = new ScholarsLinkNotInitialized();

    public static synchronized void initialize(ScholarsLink newInstance) {
        if (instance == null
                || instance instanceof ScholarsLinkNotInitialized) {
            instance = newInstance;
            log.debug("initialized: " + instance);
        } else {
            throw new IllegalStateException("Already initialized: " + instance);
        }
    }

    public static ScholarsLink instance() {
        return instance;
    }

    // ----------------------------------------------------------------------
    // The interface
    // ----------------------------------------------------------------------

    public abstract void checkConnection() throws ScholarsLinkException;

    public abstract List<Publication> getPublications(String localId)
            throws ScholarsLinkException;

    // ----------------------------------------------------------------------
    // The empty implementation
    // ----------------------------------------------------------------------

    private static class ScholarsLinkNotInitialized extends ScholarsLink {
        private static final String MESSAGE = "ScholarsLink has not been initialized";

        @Override
        public void checkConnection() throws ScholarsLinkException {
            throw new IllegalStateException(MESSAGE);
        }

        @Override
        public List<Publication> getPublications(String localId)
                throws ScholarsLinkException {
            throw new IllegalStateException(MESSAGE);
        }
    }

}
