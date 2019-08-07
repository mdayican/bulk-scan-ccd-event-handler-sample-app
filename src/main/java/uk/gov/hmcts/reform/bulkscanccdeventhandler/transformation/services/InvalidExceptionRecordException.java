package uk.gov.hmcts.reform.bulkscanccdeventhandler.transformation.services;

public class InvalidExceptionRecordException extends RuntimeException {
    public InvalidExceptionRecordException(String message) {
        super(message);
    }
}
