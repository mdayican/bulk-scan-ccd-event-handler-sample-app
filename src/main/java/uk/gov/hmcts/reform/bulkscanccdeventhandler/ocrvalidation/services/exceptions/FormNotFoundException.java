package uk.gov.hmcts.reform.bulkscanccdeventhandler.ocrvalidation.services.exceptions;

public class FormNotFoundException extends RuntimeException {

    public FormNotFoundException(String message) {
        super(message);
    }
}
