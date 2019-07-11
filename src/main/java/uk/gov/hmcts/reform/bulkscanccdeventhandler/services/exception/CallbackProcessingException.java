package uk.gov.hmcts.reform.bulkscanccdeventhandler.services.exception;

/**
 * Represents an error during the processing of a callback.
 */
public class CallbackProcessingException extends RuntimeException {

    public CallbackProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}
