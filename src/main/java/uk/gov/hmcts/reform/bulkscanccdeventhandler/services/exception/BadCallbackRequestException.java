package uk.gov.hmcts.reform.bulkscanccdeventhandler.services.exception;

// TODO: map to 400 response when the exception handler is added
public class BadCallbackRequestException extends RuntimeException {

    public BadCallbackRequestException(String message) {
        super(message);
    }
}
