package uk.gov.hmcts.reform.bulkscanccdeventhandler.transformation.model.out;

public class ApiError {

    private final String error;

    public ApiError(String error) {
        this.error = error;
    }

    public String getError() {
        return error;
    }

}
