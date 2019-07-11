package uk.gov.hmcts.reform.bulkscanccdeventhandler.services.exception;

/**
 * Represents an error related with a request having invalid format.
 */
public class MalformedRequestException extends RuntimeException {

    public MalformedRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}
