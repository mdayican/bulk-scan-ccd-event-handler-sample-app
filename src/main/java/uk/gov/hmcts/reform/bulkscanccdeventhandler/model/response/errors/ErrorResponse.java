package uk.gov.hmcts.reform.bulkscanccdeventhandler.model.response.errors;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ErrorResponse {

    @JsonProperty("message")
    public final String message;

    public ErrorResponse(String message) {
        this.message = message;
    }
}
