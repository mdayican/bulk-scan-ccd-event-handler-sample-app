package uk.gov.hmcts.reform.bulkscanccdeventhandler.common.auth;

public class UnauthenticatedException extends RuntimeException {

    public UnauthenticatedException(String message) {
        super(message);
    }
}
