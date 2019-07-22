package uk.gov.hmcts.reform.bulkscanccdeventhandler.services.exception;

public class ForbiddenException extends RuntimeException {

    public ForbiddenException(String message) {
        super(message);
    }
}
