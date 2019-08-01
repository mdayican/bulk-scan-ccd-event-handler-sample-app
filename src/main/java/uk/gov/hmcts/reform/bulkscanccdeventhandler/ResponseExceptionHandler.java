package uk.gov.hmcts.reform.bulkscanccdeventhandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import uk.gov.hmcts.reform.authorisation.exceptions.InvalidTokenException;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.services.exception.ForbiddenException;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.services.exception.FormNotFoundException;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.services.exception.UnauthenticatedException;

import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.http.ResponseEntity.status;

@ControllerAdvice
public class ResponseExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(ResponseExceptionHandler.class);

    @ExceptionHandler(InvalidTokenException.class)
    protected ResponseEntity<String> handleInvalidTokenException(InvalidTokenException exc) {
        log.warn(exc.getMessage(), exc);
        return status(UNAUTHORIZED).body(exc.getMessage());
    }

    @ExceptionHandler(UnauthenticatedException.class)
    protected ResponseEntity<String> handleUnauthenticatedException(UnauthenticatedException exc) {
        log.warn(exc.getMessage(), exc);
        return status(UNAUTHORIZED).body(exc.getMessage());
    }

    @ExceptionHandler(FormNotFoundException.class)
    protected ResponseEntity<String> handleFormNotFoundException(FormNotFoundException exc) {
        log.warn(exc.getMessage(), exc);
        return status(NOT_FOUND).body(exc.getMessage());
    }

    @ExceptionHandler(ForbiddenException.class)
    protected ResponseEntity<String> handleForbiddenException(ForbiddenException exc) {
        log.warn(exc.getMessage(), exc);
        return status(FORBIDDEN).body("S2S token is not authorized to use the service");
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<Void> handleInternalException(Exception exc) {
        log.error(exc.getMessage(), exc);
        return status(INTERNAL_SERVER_ERROR).build();
    }
}
