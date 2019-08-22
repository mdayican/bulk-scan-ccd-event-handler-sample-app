package uk.gov.hmcts.reform.bulkscanccdeventhandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import uk.gov.hmcts.reform.authorisation.exceptions.InvalidTokenException;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.common.auth.ForbiddenException;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.common.auth.UnauthenticatedException;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.ocrvalidation.services.exceptions.FormNotFoundException;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.transformation.model.out.ApiError;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.transformation.model.out.ErrorResponse;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.transformation.services.InvalidExceptionRecordException;

import static java.util.Collections.emptyList;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;
import static org.springframework.http.ResponseEntity.status;

@ControllerAdvice
public class ResponseExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(ResponseExceptionHandler.class);
    private static final ApiError API_ERROR_FORBIDDEN = new ApiError("S2S token is not authorized to use the service");

    @ExceptionHandler(InvalidTokenException.class)
    protected ResponseEntity<ApiError> handleInvalidTokenException(InvalidTokenException exc) {
        log.warn(exc.getMessage(), exc);
        return status(UNAUTHORIZED).body(new ApiError(exc.getMessage()));
    }

    @ExceptionHandler(UnauthenticatedException.class)
    protected ResponseEntity<ApiError> handleUnauthenticatedException(UnauthenticatedException exc) {
        log.warn(exc.getMessage(), exc);
        return status(UNAUTHORIZED).body(new ApiError(exc.getMessage()));
    }

    @ExceptionHandler(FormNotFoundException.class)
    protected ResponseEntity<ApiError> handleFormNotFoundException(FormNotFoundException exc) {
        log.warn(exc.getMessage(), exc);
        return status(NOT_FOUND).body(new ApiError(exc.getMessage()));
    }

    @ExceptionHandler(ForbiddenException.class)
    protected ResponseEntity<ApiError> handleForbiddenException(ForbiddenException exc) {
        log.warn(exc.getMessage(), exc);
        return status(FORBIDDEN).body(API_ERROR_FORBIDDEN);
    }

    @ExceptionHandler(InvalidExceptionRecordException.class)
    protected ResponseEntity<ErrorResponse> handleInvalidExceptionRecord(InvalidExceptionRecordException exc) {
        return status(UNPROCESSABLE_ENTITY).body(new ErrorResponse(exc.getErrors(), emptyList()));
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<Void> handleInternalException(Exception exc) {
        log.error(exc.getMessage(), exc);
        return status(INTERNAL_SERVER_ERROR).build();
    }
}
