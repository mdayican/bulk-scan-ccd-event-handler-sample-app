package uk.gov.hmcts.reform.bulkscanccdeventhandler.ocrvalidation.model.in;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class OcrDataField {

    @JsonProperty
    public final String name;

    @JsonProperty
    public final String value;

    @JsonCreator
    public OcrDataField(
        @JsonProperty("name") String name,
        @JsonProperty("value") String value
    ) {
        this.name = name;
        this.value = value;
    }
}
