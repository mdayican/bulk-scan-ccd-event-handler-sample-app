package uk.gov.hmcts.reform.bulkscanccdeventhandler.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class OcrDataField {

    @JsonProperty
    public final String key;

    @JsonProperty
    public final String value;

    @JsonCreator
    public OcrDataField(
        @JsonProperty("key") String key,
        @JsonProperty("value") String value
    ) {
        this.key = key;
        this.value = value;
    }
}
