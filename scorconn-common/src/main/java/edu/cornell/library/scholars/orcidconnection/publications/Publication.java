/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.library.scholars.orcidconnection.scholarslink;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.fasterxml.jackson.databind.ObjectMapper;

import edu.cornell.library.orcidclient.elements.ExternalIdBuilder;
import edu.cornell.library.orcidclient.elements.WorkBuilder;
import edu.cornell.library.orcidclient.orcid_message_2_1.common.FuzzyDate;
import edu.cornell.library.orcidclient.orcid_message_2_1.common.FuzzyDate.Day;
import edu.cornell.library.orcidclient.orcid_message_2_1.common.FuzzyDate.Month;
import edu.cornell.library.orcidclient.orcid_message_2_1.common.FuzzyDate.Year;
import edu.cornell.library.orcidclient.orcid_message_2_1.common.LanguageCode;
import edu.cornell.library.orcidclient.orcid_message_2_1.work.WorkElement;
import edu.cornell.library.orcidclient.orcid_message_2_1.work.WorkType;

/**
 * A transition-layer object that holds the info between the received JSON
 * publications, and the ORCID WorkElement instances.
 * 
 * The iternal structure should remain hidden. The function is to translate from
 * JSON to WorkElement and back again.
 */
public class Publication {
    
    // ----------------------------------------------------------------------
    // The factory
    // ----------------------------------------------------------------------

    public static List<Publication> fromJson(String jsonString)
            throws DataFormatException {
        try {
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> listOfMaps = new ObjectMapper()
                    .readValue(jsonString, ArrayList.class);

            List<Publication> pubs = new ArrayList<>();
            for (Map<String, Object> map : listOfMaps) {
                pubs.add(new Parser(map).parse());
            }

            return pubs;
        } catch (IOException e) {
            throw new DataFormatException("Bad syntax in JSON string.", e);
        }
    }

    public static Publication fromWorkElement(WorkElement work) {
        return new WorkReader(work).read();
    }
    
    public static final String EMPTY_TITLE = "EMPTY_PUBLICATION_TITLE";
    
    public static Publication emptyPub() {
        Publication pub = new Publication();
        pub.title = EMPTY_TITLE;
        return pub;
    }

    // ----------------------------------------------------------------------
    // The instance
    // ----------------------------------------------------------------------

    private WorkType workType;
    private String title;
    private int[] publicationDate;
    private LanguageCode language;
    private String country;
    private String journalTitle;
    private Map<String, ExternalId> externalIds = new HashMap<>();

    public WorkElement toOrcidWork() {
        WorkBuilder builder = new WorkBuilder(workType, title)
                .setPublicationDate(publicationDate) //
                .setLanguageCode(language) //
                .setCountry(country) //
                .setJournalTitle(journalTitle);
        for (ExternalId externalId : externalIds.values()) {
            builder.addExternalId(new ExternalIdBuilder() //
                    .setType(externalId.type) //
                    .setUrl(externalId.url) //
                    .setValue(externalId.displayValue));
        }
        return builder.build();
    }

    public String getScholarsUri() {
        return externalIds.get("other-id").url;
    }

    @Override
    public int hashCode() {
        return Objects.hash(workType.toString(), title, publicationDate,
                language, country, journalTitle, externalIds);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null) {
            return false;
        }
        if (!this.getClass().equals(other.getClass())) {
            return false;
        }
        Publication that = (Publication) other;
        return Objects.equals(this.workType, that.workType)
                && Objects.equals(this.title, that.title)
                && Objects.equals(this.publicationDate, that.publicationDate)
                && Objects.equals(this.language, that.language)
                && Objects.equals(this.country, that.country)
                && Objects.equals(this.journalTitle, that.journalTitle)
                && Objects.equals(this.externalIds, that.externalIds);
    }

    @Override
    public String toString() {
        return String.format(
                "Publication[workType=%s, title=%s, publicationDate=%s, "
                        + "language=%s, country=%s, journalTitle=%s, "
                        + "externalIds=%s]",
                workType, title, Arrays.toString(publicationDate), language,
                country, journalTitle, externalIds);
    }

    // ----------------------------------------------------------------------
    // Helper classes
    // ----------------------------------------------------------------------

    private static class ExternalId {
        private String type;
        private String url;
        private String displayValue;

        @Override
        public String toString() {
            return String.format("ExternalId[type=%s, url=%s, displayValue=%s]",
                    type, url, displayValue);
        }

    }

    private static class Parser {
        private final Map<String, Object> valuesMap;

        public Parser(Map<String, Object> valuesMap) {
            this.valuesMap = valuesMap;
        }

        public Publication parse() throws DataFormatException {
            Publication pub = new Publication();
            pub.workType = parseWorkType();
            pub.title = parseTitle();
            pub.publicationDate = parsePublicationDate();
            pub.language = parseLanguageCode();
            pub.country = parseCountryCode();
            pub.journalTitle = parseJournalTitle();
            pub.externalIds.putAll(parseExternalIds());
            return pub;
        }

        private WorkType parseWorkType() throws DataFormatException {
            String s = (String) valuesMap.get("workType");
            if (s == null) {
                throw dfe("A value for 'workType' is required.");
            }
            try {
                return WorkType.fromValue(s);
            } catch (IllegalArgumentException e) {
                throw dfe("Invalid value for 'workType': '%s'", s);
            }
        }

        private String parseTitle() throws DataFormatException {
            String s = (String) valuesMap.get("title");
            if (s == null) {
                throw dfe("A value for 'title' is required.");
            }
            return s;
        }

        private int[] parsePublicationDate() throws DataFormatException {
            String s = (String) valuesMap.get("publicationDate");
            if (s == null) {
                return null;
            }

            try {
                String[] parts = s.split("-");
                if (parts.length > 3) {
                    throw new NumberFormatException();
                }
                int[] ints = new int[parts.length];
                for (int i = 0; i < parts.length; i++) {
                    ints[i] = Integer.parseInt(parts[i]);
                }
                return ints;
            } catch (NumberFormatException e) {
                throw dfe("'publicationDate' must be in one of these formats: "
                        + "'yyyy-mm-dd', 'yyyy-mm', 'yyyy'. Value was '%s'.",
                        s);
            }
        }

        private LanguageCode parseLanguageCode() throws DataFormatException {
            String s = (String) valuesMap.get("language");
            if (s == null) {
                return null;
            }
            try {
                return LanguageCode.fromValue(s);
            } catch (IllegalArgumentException e) {
                throw dfe("Invalid value for 'language': '%s'", s);
            }
        }

        private String parseCountryCode() {
            return (String) valuesMap.get("country");
        }

        private String parseJournalTitle() {
            return (String) valuesMap.get("journalTitle");
        }

        private Map<String, ExternalId> parseExternalIds()
                throws DataFormatException {
            @SuppressWarnings("unchecked")
            List<Map<String, String>> rawIdsList = (List<Map<String, String>>) valuesMap
                    .get("externalIds");
            if (rawIdsList == null) {
                rawIdsList = Collections.emptyList();
            }

            Map<String, ExternalId> idsMap = new HashMap<>();
            for (Map<String, String> rawId : rawIdsList) {
                ExternalId id = parseExternalId(rawId);
                idsMap.put(id.type, id);
            }

            if (!idsMap.containsKey("other-id")) {
                throw dfe("An External ID for Scholars@Cornell is required.");
            }

            return idsMap;
        }

        private ExternalId parseExternalId(Map<String, String> map)
                throws DataFormatException {
            String type = map.get("type");
            if (type == null) {
                throw dfe("External ID must have a 'type': %s", map);
            }
            String url = map.get("url");
            if (url == null) {
                throw dfe("External ID must have a 'url': %s", map);
            }
            String displayValue = map.get("displayValue");
            if (displayValue == null) {
                throw dfe("External ID must have a 'displayValue': %s", map);
            }

            ExternalId id = new ExternalId();
            id.type = type;
            id.url = url;
            id.displayValue = displayValue;
            return id;
        }

        private DataFormatException dfe(String format, Object... args) {
            return new DataFormatException(String.format(format, args));
        }
    }

    private static class WorkReader {
        private final WorkElement work;

        public WorkReader(WorkElement work) {
            this.work = work;
        }

        public Publication read() {
            Publication pub = new Publication();
            pub.workType = work.getType();
            pub.title = work.getTitle().getTitle();
            pub.publicationDate = parsePublicationDate();
            pub.language = work.getLanguageCode();
            pub.country = work.getCountry().getValue();
            pub.journalTitle = work.getJournalTitle();
            pub.externalIds.putAll(readExternalIds());
            return pub;
        }

        private int[] parsePublicationDate() {
            FuzzyDate pubDate = work.getPublicationDate();
            if (pubDate == null) {
                return null;
            }

            Year year = pubDate.getYear();
            if (year == null || year.getValue() == null) {
                return null;
            }
            int yearInt = Integer.parseInt(year.getValue());

            Month month = pubDate.getMonth();
            if (month == null || month.getValue() == null) {
                return new int[] { yearInt };
            }
            int monthInt = Integer.parseInt(month.getValue());

            Day day = pubDate.getDay();
            if (day == null || day.getValue() == null) {
                return new int[] { yearInt, monthInt };
            }
            int dayInt = Integer.parseInt(day.getValue());

            return new int[] { yearInt, monthInt, dayInt };
        }

        private Map<String, ExternalId> readExternalIds() {
            Map<String, ExternalId> map = new HashMap<>();
            for (edu.cornell.library.orcidclient.orcid_message_2_1.common.ExternalId workId : work
                    .getExternalIds().getExternalId()) {
                ExternalId pubId = new ExternalId();
                pubId.type = workId.getExternalIdType();
                pubId.url = workId.getExternalIdUrl();
                pubId.displayValue = workId.getExternalIdValue();
                map.put(pubId.type, pubId);
            }
            return map;
        }
    }
}
