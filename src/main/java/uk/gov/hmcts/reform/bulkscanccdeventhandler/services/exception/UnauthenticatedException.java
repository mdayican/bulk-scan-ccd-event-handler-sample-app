package uk.gov.hmcts.reform.bulkscanccdeventhandler.services.exception;

public class UnauthenticatedException extends RuntimeException {

    public UnauthenticatedException(String message) {
        super(message);
    }
}
