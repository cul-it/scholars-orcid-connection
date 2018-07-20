/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.library.scholars.orcidconnection.scholarslink;

import edu.cornell.library.scholars.orcidconnection.ScholarsOrcidException;

/**
 * Problem somewhere with the ScholarsLink
 */
public class ScholarsLinkException extends ScholarsOrcidException {
    public ScholarsLinkException(String message) {
        super(message);
    }
    
    public ScholarsLinkException(Throwable cause) {
        super(cause);
    }
    
    public ScholarsLinkException(String message, Throwable cause) {
        super(message, cause);
    }
}
