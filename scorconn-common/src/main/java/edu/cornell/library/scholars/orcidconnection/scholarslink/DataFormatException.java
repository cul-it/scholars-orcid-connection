/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.library.scholars.orcidconnection.scholarslink;

/**
 * The JSON data that came from Scholars is not in the correct form.
 */
public class DataFormatException extends Exception {
    public DataFormatException(String message) {
        super(message);
    }

    public DataFormatException(Throwable cause) {
        super(cause);
    }

    public DataFormatException(String message, Throwable cause) {
        super(message, cause);
    }
}
