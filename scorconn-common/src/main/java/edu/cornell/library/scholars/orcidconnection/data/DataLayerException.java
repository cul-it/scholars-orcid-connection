/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.library.scholars.orcidconnection.data;

import edu.cornell.library.scholars.orcidconnection.ScholarsOrcidException;

/**
 * Problem somewhere with the persistence layer.
 */
public class DataLayerException extends ScholarsOrcidException {
    public DataLayerException(String message) {
        super(message);
    }
    
    public DataLayerException(Throwable cause) {
        super(cause);
    }
    
    public DataLayerException(String message, Throwable cause) {
        super(message, cause);
    }
}
