/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.library.scholars.orcidconnection.publications;

import java.util.Set;

public interface PublicationsFromScholars {
    Set<String> getUris();
    
    boolean hasUri(String scholarsUri);

    Publication getPublication(String scholarsUri);

    String getHash(String scholarsUri);
}