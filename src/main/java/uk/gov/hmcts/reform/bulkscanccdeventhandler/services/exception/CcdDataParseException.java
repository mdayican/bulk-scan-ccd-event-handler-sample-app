package uk.gov.hmcts.reform.bulkscanccdeventhandler.services.exception;

public class CcdDataParseException extends RuntimeException {

    public CcdDataParseException(String message, Throwable cause) {
        super(message, cause);
    }
}
