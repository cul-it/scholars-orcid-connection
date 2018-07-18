/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.library.scholars.orcidconnection;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.library.scholars.orcidconnection.scholarslink.Publication;

/**
 * TODO
 */
public class ScholarsOrcidConnectionImpl extends ScholarsOrcidConnection {
    private static final Log log = LogFactory
            .getLog(ScholarsOrcidConnectionImpl.class);

    /**
     * @param properties
     */
    public ScholarsOrcidConnectionImpl(Map<String, String> properties)
            throws IllegalPropertiesException {
        log.error(
                "BOGUS -- ScholarsOrcidConnectionImpl Constructor not implemented.");
    }

    @Override
    public List<Publication> getPublications(String localId) {
        log.error(
                "BOGUS -- ScholarsOrcidConnectionImpl.getPublications() not implemented.");
        return Collections.emptyList();
    }

}
