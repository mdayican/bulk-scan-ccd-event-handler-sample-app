package uk.gov.hmcts.reform.bulkscanccdeventhandler.services.exception;

public class FormNotFoundException extends RuntimeException {

    public FormNotFoundException(String message) {
        super(message);
    }
}
