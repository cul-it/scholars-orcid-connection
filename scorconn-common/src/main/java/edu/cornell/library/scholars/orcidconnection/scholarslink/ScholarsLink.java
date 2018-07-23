/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.library.scholars.orcidconnection.scholarslink;

import edu.cornell.library.scholars.orcidconnection.ScholarsOrcidConnection.IllegalPropertiesException;

/**
 * TODO
 */
public abstract class ScholarsLink {
    private static volatile ScholarsLink instance;
    
    public static init(Map<String, String> properties) {
        throw new RuntimeException("ScholarsLink.init not implemented.");
    }

    public abstract void checkConnection() throws IllegalPropertiesException;

}
