/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.library.scholars.orcidconnection.publications;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import edu.cornell.library.orcidclient.elements.ExternalIdBuilder;
import edu.cornell.library.orcidclient.elements.WorkBuilder;
import edu.cornell.library.orcidclient.orcid_message_2_1.common.ExternalId;
import edu.cornell.library.orcidclient.orcid_message_2_1.common.FuzzyDate;
import edu.cornell.library.orcidclient.orcid_message_2_1.common.LanguageCode;
import edu.cornell.library.orcidclient.orcid_message_2_1.work.WorkElement;
import edu.cornell.library.orcidclient.orcid_message_2_1.work.WorkType;
import edu.cornell.library.scholars.orcidconnection.publications.Publication;
import edu.cornell.library.scholars.orcidconnection.scholarslink.DataFormatException;
import edu.cornell.mannlib.vitro.testing.AbstractTestClass;

/**
 * TODO
 */
public class PublicationTest extends AbstractTestClass {
    private ObjectMapper mapper;

    private Map<String, Object> fullInputMap;
    private WorkBuilder fullResultBuilder;

    @Before
    public void setup() {
        mapper = new ObjectMapper();
    }

    @Before
    public void setupSuccessMaps() {
        fullInputMap = new HashMap<String, Object>() {
            {
                put("workType", "journal-article");
                put("title", "Atissue engineered meniscal enthesis");
                put("publicationDate", "2018-01-01");
                put("language", "en");
                put("country", "US");
                put("journalTitle", "Acta Biomaterialia");
                put("externalIds", new ArrayList<Map<String, Object>>() {
                    {
                        add(new HashMap<String, Object>() {
                            {
                                put("type", "other-id");
                                put("url",
                                        "https://scholars.cornell.edu/display/UR-458129");
                                put("displayValue", "Scholars@Cornell URL");
                            }
                        });
                        add(new HashMap<String, Object>() {
                            {
                                put("type", "doi");
                                put("url",
                                        "http://dx.doi.org/10.1016/j.actbio.2016.10.040");
                                put("displayValue", "j.actbio.2016.10.040");
                            }
                        });
                    }
                });
            }
        };

        fullResultBuilder = new WorkBuilder(WorkType.JOURNAL_ARTICLE,
                "Atissue engineered meniscal enthesis") //
                        .setPublicationDate(new int[] { 2018, 1, 1 }) //
                        .setLanguageCode(LanguageCode.EN) //
                        .setCountry("US") //
                        .setJournalTitle("Acta Biomaterialia") //
                        .addExternalId(new ExternalIdBuilder() //
                                .setType("other-id") //
                                .setUrl("https://scholars.cornell.edu/display/UR-458129") //
                                .setValue("Scholars@Cornell URL")) //
                        .addExternalId(new ExternalIdBuilder() //
                                .setType("doi") //
                                .setUrl("http://dx.doi.org/10.1016/j.actbio.2016.10.040") //
                                .setValue("j.actbio.2016.10.040"));
    }

    // ----------------------------------------------------------------------
    // The tests
    // ----------------------------------------------------------------------

    @Test
    public void fullSuccess() throws Exception {
        assertExpectedPublication(fullInputMap, fullResultBuilder.build());
    }

    /**
     * These fields are optional: publicationDate, language, country,
     * journalTitle, external IDs other than "other-id".
     */
    @Test
    public void minimalSuccess() throws Exception {
        fullInputMap.remove("publicationDate");
        fullInputMap.remove("language");
        fullInputMap.remove("country");
        fullInputMap.remove("journalTitle");
        getExternalIds(fullInputMap).remove(1);

        WorkElement minimalResult = new WorkBuilder(WorkType.JOURNAL_ARTICLE,
                "Atissue engineered meniscal enthesis") //
                        .addExternalId(new ExternalIdBuilder() //
                                .setType("other-id") //
                                .setUrl("https://scholars.cornell.edu/display/UR-458129") //
                                .setValue("Scholars@Cornell URL")) //
                        .build();

        assertExpectedPublication(fullInputMap, minimalResult);
    }

    @Test
    public void workTypeMissing_throwsException() throws Exception {
        fullInputMap.remove("workType");

        expectException(DataFormatException.class, "'workType' is required");
        assertExpectedPublication(fullInputMap, fullResultBuilder.build());
    }

    @Test
    public void workTypeInvalid_throwsException() throws Exception {
        fullInputMap.put("workType", "BOGUS");

        expectException(DataFormatException.class,
                "Invalid value for 'workType'");
        assertExpectedPublication(fullInputMap, fullResultBuilder.build());
    }

    @Test
    public void titleMissing_throwsException() throws Exception {
        fullInputMap.remove("title");

        expectException(DataFormatException.class, "'title' is required");
        assertExpectedPublication(fullInputMap, fullResultBuilder.build());
    }

    @Test
    public void publicationDateContainsSurpriseCharacters_throwsException()
            throws Exception {
        fullInputMap.put("publicationDate", "2018-01-01XX");

        expectException(DataFormatException.class, "'publicationDate' must be");
        assertExpectedPublication(fullInputMap, fullResultBuilder.build());
    }

    @Test
    public void publicationDateHasTooManyFields_throwsException()
            throws Exception {
        fullInputMap.put("publicationDate", "2018-01-01-00");

        expectException(DataFormatException.class, "'publicationDate' must be");
        assertExpectedPublication(fullInputMap, fullResultBuilder.build());
    }

    @Test
    public void publicationDateMayBeYear() throws Exception {
        fullInputMap.put("publicationDate", "2018");
        fullResultBuilder.setPublicationDate(new int[] { 2018 });

        assertExpectedPublication(fullInputMap, fullResultBuilder.build());
    }

    @Test
    public void publicationDateMayBeYearMonth() throws Exception {
        fullInputMap.put("publicationDate", "2018-07");
        fullResultBuilder.setPublicationDate(new int[] { 2018, 7 });

        assertExpectedPublication(fullInputMap, fullResultBuilder.build());
    }

    @Test
    public void languageInvalid_throwsException() throws Exception {
        fullInputMap.put("language", "BOGUS");

        expectException(DataFormatException.class,
                "Invalid value for 'language'");
        assertExpectedPublication(fullInputMap, fullResultBuilder.build());
    }

    @Test
    public void externalIdOtherIDMissing_throwsException() throws Exception {
        getExternalIds(fullInputMap).remove(0);

        expectException(DataFormatException.class,
                "External ID for Scholars@Cornell");
        assertExpectedPublication(fullInputMap, fullResultBuilder.build());
    }

    @Test
    public void externalIdWithoutType_throwsException() throws Exception {
        getExternalIds(fullInputMap).get(1).remove("type");

        expectException(DataFormatException.class, "must have a 'type'");
        assertExpectedPublication(fullInputMap, fullResultBuilder.build());
    }

    @Test
    public void externalIdWithoutUrl_throwsException() throws Exception {
        getExternalIds(fullInputMap).get(1).remove("url");

        expectException(DataFormatException.class, "must have a 'url'");
        assertExpectedPublication(fullInputMap, fullResultBuilder.build());
    }

    @Test
    public void externalIdWithoutValue_throwsException() throws Exception {
        getExternalIds(fullInputMap).get(1).remove("displayValue");

        expectException(DataFormatException.class,
                "must have a 'displayValue'");
        assertExpectedPublication(fullInputMap, fullResultBuilder.build());
    }

    // ----------------------------------------------------------------------
    // Helper methods
    // ----------------------------------------------------------------------

    private void assertExpectedPublication(Map<String, Object> jsonInputMap,
            WorkElement w) throws Exception {
        String jsonString = mapper
                .writeValueAsString(Collections.singletonList(jsonInputMap));

        List<Publication> pubs = Publication.fromJson(jsonString);

        assertEquals(1, pubs.size());
        assertEqualWorkElements(w, pubs.get(0).toOrcidWork());
    }

    private void assertEqualWorkElements(WorkElement expected,
            WorkElement actual) {
        assertEquals(expected.getType(), actual.getType());
        assertEquals(expected.getTitle().getTitle(),
                actual.getTitle().getTitle());
        assertEqualsFuzzyDates(expected.getPublicationDate(),
                actual.getPublicationDate());
        assertEquals(expected.getLanguageCode(), actual.getLanguageCode());
        assertEqualValue(expected.getCountry(), actual.getCountry());
        assertEquals(expected.getJournalTitle(), actual.getJournalTitle());
        assertEqualIdLists(expected.getExternalIds().getExternalId(),
                actual.getExternalIds().getExternalId());
    }

    private void assertEqualsFuzzyDates(FuzzyDate expected, FuzzyDate actual) {
        if (expected == null && actual == null) {
            return;
        } else if (expected != null && actual != null) {
            assertEqualValue(expected.getYear(), actual.getYear());
            assertEqualValue(expected.getMonth(), actual.getMonth());
            assertEqualValue(expected.getDay(), actual.getDay());
        } else {
            fail("expected=" + expected + ", actual=" + actual);
        }
    }

    private void assertEqualIdLists(List<ExternalId> expectedIds,
            List<ExternalId> actualIds) {
        Comparator<ExternalId> comparator = (a, b) -> a.getExternalIdType()
                .compareTo(b.getExternalIdType());
        Collections.sort(expectedIds, comparator);
        Collections.sort(actualIds, comparator);

        assertEquals(expectedIds.size(), actualIds.size());

        for (int i = 0; i < actualIds.size(); i++) {
            ExternalId eId = expectedIds.get(i);
            ExternalId aId = actualIds.get(i);
            assertEquals(eId.getExternalIdRelationship(),
                    aId.getExternalIdRelationship());
            assertEquals(eId.getExternalIdType(), aId.getExternalIdType());
            assertEquals(eId.getExternalIdUrl(), aId.getExternalIdUrl());
            assertEquals(eId.getExternalIdValue(), aId.getExternalIdValue());
        }
    }

    private void assertEqualValue(Object expected, Object actual) {
        if (expected == null && actual == null) {
            return;
        } else if (expected != null && actual != null) {
            assertEquals(getValue(expected), getValue(actual));
        } else {
            fail("expected=" + expected + ", actual=" + actual);
        }
    }

    private Object getValue(Object holder) {
        try {
            Class c = holder.getClass();
            Method m = c.getMethod("getValue");
            return m.invoke(holder);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private List<Map<String, Object>> getExternalIds(Map<String, Object> map) {
        return (List<Map<String, Object>>) map.get("externalIds");
    }

}
