package uk.gov.hmcts.reform.bulkscanccdeventhandler.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A class used for testing the conversion of CCD raw data into strongly typed collections.
 */
public class TestCollectionItem {
    public int intField;
    public String stringField;
    public boolean boolField;

    @JsonCreator
    public TestCollectionItem(
        @JsonProperty("intField") int intField,
        @JsonProperty("stringField") String stringField,
        @JsonProperty("boolField") boolean boolField
    ) {
        this.intField = intField;
        this.stringField = stringField;
        this.boolField = boolField;
    }
}
