/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.library.scholars.orcidconnection;

import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.library.scholars.orcidconnection.scholarslink.Publication;
import edu.cornell.library.scholars.orcidconnection.scholarslink.ScholarsLinkException;

/**
 * TODO
 */
public class ScholarsOrcidConnectionImpl extends ScholarsOrcidConnection {
    private static final Log log = LogFactory
            .getLog(ScholarsOrcidConnectionImpl.class);

    private static final String DUMMY_PUBLICATIUONS = "" //
            + "[ \n" //
            + "  { \n" //
            + "    \"workType\": \"journal-article\", \n" //
            + "    \"title\": \"A model system for developing a tissue engineered meniscal enthesis\", \n" //
            + "    \"publicationDate\": \"2018-01-01\", \n" //
            + "    \"language\": \"en\", \n" //
            + "    \"country\": \"US\", \n" //
            + "    \"journalTitle\": \"Acta Biomaterialia\", \n" //
            + "    \"externalIds\": [ \n" //
            + "      { \n" //
            + "        \"type\": \"other-id\", \n" //
            + "        \"url\": \"https://scholars.cornell.edu/display/UR-458129\", \n" //
            + "        \"displayValue\": \"Scholars@Cornell URL\" \n" //
            + "      }, \n" //
            + "      { \n" //
            + "        \"type\": \"doi\", \n" //
            + "        \"url\": \"http://dx.doi.org/10.1016/j.actbio.2016.10.040\", \n" //
            + "        \"displayValue\": \"10.1016/j.actbio.2016.10.040\" \n" //
            + "      } \n" //
            + "    ] \n" //
            + "  } \n" //
            + "]"; //

    /**
     * @param properties
     */
    public ScholarsOrcidConnectionImpl(Map<String, String> properties)
            throws IllegalPropertiesException {
        log.error(
                "BOGUS -- ScholarsOrcidConnectionImpl Constructor not implemented.");
    }

    @Override
    public List<Publication> getPublications(String localId) throws ScholarsLinkException {
        log.error(
                "BOGUS -- ScholarsOrcidConnectionImpl.getPublications() not implemented.");
        return Publication.fromJson(DUMMY_PUBLICATIUONS);
    }

}
