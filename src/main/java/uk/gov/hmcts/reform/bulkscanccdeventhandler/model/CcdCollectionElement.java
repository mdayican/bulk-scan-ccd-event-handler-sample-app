package uk.gov.hmcts.reform.bulkscanccdeventhandler.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class CcdCollectionElement<T> {

    @JsonProperty
    public final T value;

    @JsonCreator
    public CcdCollectionElement(@JsonProperty("value") T value) {
        this.value = value;
    }
}
