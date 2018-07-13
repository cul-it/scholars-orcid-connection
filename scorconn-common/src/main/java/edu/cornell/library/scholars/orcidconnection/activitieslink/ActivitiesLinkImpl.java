/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.library.scholars.orcidconnection.activitieslink;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.library.scholars.orcidconnection.ScholarsOrcidConnection;
import edu.cornell.library.scholars.orcidconnection.ScholarsOrcidConnection.IllegalPropertiesException;

/**
 * TODO
 */
public class ActivitiesLinkImpl extends ActivitiesLink {
    private static final Log log = LogFactory.getLog(ActivitiesLinkImpl.class);

    /**
     * @param instance
     */
    public ActivitiesLinkImpl(ScholarsOrcidConnection instance) {
        log.error("BOGUS -- ActivitiesLinkImpl Constructor not implemented.");
    }

    @Override
    public void checkConnection() throws IllegalPropertiesException {
        log.error("BOGUS -- ActivitiesLink.checkConnection() not implemented.");
    }

}
