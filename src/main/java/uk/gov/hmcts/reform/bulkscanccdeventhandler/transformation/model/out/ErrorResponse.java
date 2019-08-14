package uk.gov.hmcts.reform.bulkscanccdeventhandler.transformation.model.out;

import java.util.List;

public class ErrorResponse {

    public final List<String> errors;
    public final List<String> warnings;

    public ErrorResponse(List<String> errors, List<String> warnings) {
        this.errors = errors;
        this.warnings = warnings;
    }
}
