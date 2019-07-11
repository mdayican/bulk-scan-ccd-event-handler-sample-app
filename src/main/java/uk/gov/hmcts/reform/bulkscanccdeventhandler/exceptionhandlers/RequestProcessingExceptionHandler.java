package uk.gov.hmcts.reform.bulkscanccdeventhandler.exceptionhandlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.model.response.errors.ErrorResponse;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.services.exception.MalformedRequestException;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.ResponseEntity.status;

@ControllerAdvice
public class RequestProcessingExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(RequestProcessingExceptionHandler.class);

    @ExceptionHandler(MalformedRequestException.class)
    protected ResponseEntity<ErrorResponse> handleMalformedRequestException(
        MalformedRequestException exception
    ) {
        // This is logged as an error, because there may be a problem somewhere between services
        // and we should be notified
        log.error("Service received a malformed callback request", exception);

        return status(BAD_REQUEST)
            .body(
                new ErrorResponse(
                    String.format("Invalid request: %s", exception.getMessage())
                )
            );
    }
}
