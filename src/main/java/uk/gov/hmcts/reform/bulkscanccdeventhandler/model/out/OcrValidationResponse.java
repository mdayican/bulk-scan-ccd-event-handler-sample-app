package uk.gov.hmcts.reform.bulkscanccdeventhandler.model.out;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class OcrValidationResponse {

    @JsonProperty("warnings")
    public final List<String> warnings;

    @JsonProperty("errors")
    public final List<String> errors;

    @JsonProperty("status")
    public final String status;

    @JsonCreator
    public OcrValidationResponse(
        List<String> warnings,
        List<String> errors,
        String status
    ) {
        this.warnings = warnings;
        this.errors = errors;
        this.status = status;
    }
}
