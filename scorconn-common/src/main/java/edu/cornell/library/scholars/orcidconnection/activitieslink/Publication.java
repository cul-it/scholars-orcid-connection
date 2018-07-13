/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.library.scholars.orcidconnection.activitieslink;

import java.util.List;

import edu.cornell.library.orcidclient.elements.ExternalIdBuilder;
import edu.cornell.library.orcidclient.elements.WorkBuilder;
import edu.cornell.library.orcidclient.orcid_message_2_1.common.LanguageCode;
import edu.cornell.library.orcidclient.orcid_message_2_1.work.WorkElement;
import edu.cornell.library.orcidclient.orcid_message_2_1.work.WorkType;

/**
 * TODO
 */
public class Publication {
    private String workType;
    private String title;
    private String publicationDate;
    private String language;
    private String country;
    private String journalTitle;
    private List<ExternalId> externalIds;

    public static class ExternalId {
        private String type;
        private String url;
        private String displayValue;
    }

    public WorkElement toOrcidWork() {
        WorkBuilder builder = new WorkBuilder(parseWorkType()) //
                .setTitle(title) //
                .setPublicationDate(parsePublicationDate()) //
                .setLanguageCode(parseLanguage()) //
                .setCountry("US") //
                .setJournalTitle("My favorite journal");
        for (ExternalId externalId : externalIds) {
            builder.addExternalId(new ExternalIdBuilder() //
                    .setType(externalId.type) //
                    .setUrl(externalId.url) //
                    .setValue(externalId.displayValue));
        }
        return builder.build();
    }

    private WorkType parseWorkType() {
        // TODO Auto-generated method stub
        throw new RuntimeException(
                "Publication.parseWorkType() not implemented.");
    }

    private int[] parsePublicationDate() {
        String[] parts = publicationDate.split("-");
        int[] ints = new int[parts.length];
        for (int i = 0; i < parts.length; i++) {
            ints[i] = Integer.parseInt(parts[i]);
        }
        return ints;
    }

    private LanguageCode parseLanguage() {
        throw new RuntimeException(
                "Publication.parseLanguage not implemented.");
    }

    /**
     * From test webapp: EditWorksRequest
     * <pre>
     * return new WorkBuilder(WorkType.JOURNAL_ARTICLE) //
     *         .setTitle("The article title") //
     *         .setSubtitle("An odyssey") //
     *         .setPublicationDate(1953, 7, 30) //
     *         .setShortDescription("A most excellent article.") //
     *         .setLanguageCode(LanguageCode.EN) //
     *         .setCountry("AR") //
     *         .setJournalTitle("My favorite journal") //
     *         .setCitation(new CitationBuilder(BIBTEX, "Some BIBTEX citation")) //
     *         .addExternalId(new ExternalIdBuilder() //
     *                 .setType("other-id") //
     *                 .setUrl("http://external/id") //
     *                 .setValue("Link to me")) //
     *         .addContributor(new ContributorBuilder(AUTHOR, FIRST) //
     *                 .setCreditName("Joe Bagadonuts") //
     *                 .setContributorEmail("joeBags@donuts.edu") //
     *                 .setOrcidId(new OrcidIdBuilder("0000-0000-0000-0000"))) //
     *         .build();
     * </pre>
     */
}
