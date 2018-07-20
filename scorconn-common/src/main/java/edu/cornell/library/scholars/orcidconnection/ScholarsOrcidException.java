/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.library.scholars.orcidconnection;

/**
 * The JSON data that came from Scholars is not in the correct form.
 */
public class ScholarsOrcidException extends Exception {
    public ScholarsOrcidException(String message) {
        super(message);
    }

    public ScholarsOrcidException(Throwable cause) {
        super(cause);
    }

    public ScholarsOrcidException(String message, Throwable cause) {
        super(message, cause);
    }
}
