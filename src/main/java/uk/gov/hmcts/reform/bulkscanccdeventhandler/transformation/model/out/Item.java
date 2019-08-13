package uk.gov.hmcts.reform.bulkscanccdeventhandler.transformation.model.out;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Wrapper class for CCD list items.
 */
public class Item<T> {

    @JsonProperty("value")
    public final T value;

    public Item(T value) {
        this.value = value;
    }
}
