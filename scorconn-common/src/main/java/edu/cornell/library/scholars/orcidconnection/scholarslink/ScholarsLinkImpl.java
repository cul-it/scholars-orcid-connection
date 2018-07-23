/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.library.scholars.orcidconnection.scholarslink;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.library.scholars.orcidconnection.ScholarsOrcidConnection;
import edu.cornell.library.scholars.orcidconnection.ScholarsOrcidConnection.IllegalPropertiesException;

/**
 * TODO
 */
public class ScholarsLinkImpl extends ScholarsLink {
    private static final Log log = LogFactory.getLog(ScholarsLinkImpl.class);

    /**
     * @param instance
     */
    public ScholarsLinkImpl(ScholarsOrcidConnection instance) {
        log.error("BOGUS -- ActivitiesLinkImpl Constructor not implemented.");
    }

    @Override
    public void checkConnection() throws IllegalPropertiesException {
        log.error("BOGUS -- ActivitiesLink.checkConnection() not implemented.");
    }

    
    //////////////// BOGUS
    /**
     * <pre>
     * Constructor requires properties map and HttpWrapper
     * 
     * Do it here. getPublications(localId)
     * 
     * Given the base Scholars URL, call api/dataRequest/listPublicationsForOrcidConnection?localID=mj495
     * Parse the results into publications.
     * </pre>
     */
}
