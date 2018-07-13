/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.library.scholars.orcidconnection;

import java.util.List;
import java.util.Map;

import edu.cornell.library.scholars.orcidconnection.activitieslink.Publication;

/**
 * TODO
 */
public abstract class ScholarsOrcidConnection {
    private static volatile ScholarsOrcidConnection instance;

    public static synchronized void init(Map<String, String> properties)
            throws IllegalPropertiesException {
        if (instance == null) {
            instance = new ScholarsOrcidConnectionImpl(properties);
        } else {
            throw new IllegalStateException(
                    "ScholarsOrcidConnection has already been initialized.");
        }
    }

    public static synchronized ScholarsOrcidConnection instance() {
        if (instance == null) {
            throw new IllegalStateException(
                    "ScholarsOrcidConnection must be initialized first.");
        } else {
            return instance;
        }
    }

    public abstract List<Publication> getPublications(String localId);

    public class IllegalPropertiesException extends Exception {

        public IllegalPropertiesException(String message) {
            super(message);
        }

        public IllegalPropertiesException(Throwable cause) {
            super(cause);
        }

        public IllegalPropertiesException(String message, Throwable cause) {
            super(message, cause);
        }

    }
}
