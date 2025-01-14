package uk.gov.hmcts.reform.bulkscanccdeventhandler.ocrvalidation.controllers;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.commons.lang3.EnumUtils;
import org.slf4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.common.auth.AuthService;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.ocrvalidation.model.FormType;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.ocrvalidation.model.in.OcrDataValidationRequest;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.ocrvalidation.model.out.OcrValidationResponse;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.ocrvalidation.services.OcrDataValidator;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.ocrvalidation.services.OcrValidationResult;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.ocrvalidation.services.exceptions.FormNotFoundException;

import javax.validation.Valid;

import static org.slf4j.LoggerFactory.getLogger;
import static org.springframework.http.ResponseEntity.ok;

@RestController
public class OcrValidationController {
    private static final Logger logger = getLogger(OcrValidationController.class);

    private final OcrDataValidator ocrDataValidator;
    private final AuthService authService;

    public OcrValidationController(
        OcrDataValidator ocrDataValidator,
        AuthService authService
    ) {
        this.ocrDataValidator = ocrDataValidator;
        this.authService = authService;
    }

    @PostMapping(
        path = "/forms/{form-type}/validate-ocr",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ApiOperation("Validates OCR form data based on form type")
    @ApiResponses({
        @ApiResponse(
            code = 200, response = OcrValidationResponse.class, message = "Validation executed successfully"
        ),
        @ApiResponse(code = 401, message = "Provided S2S token is missing or invalid"),
        @ApiResponse(code = 403, message = "S2S token is not authorized to use the service"),
        @ApiResponse(code = 404, message = "Form type not found")
    })
    public ResponseEntity<OcrValidationResponse> validateOcrData(
        @RequestHeader(name = "ServiceAuthorization", required = false) String serviceAuthHeader,
        @PathVariable(name = "form-type", required = false) String formType,
        @Valid @RequestBody OcrDataValidationRequest request
    ) {
        if (!EnumUtils.isValidEnum(FormType.class, formType)) {
            throw new FormNotFoundException("Form type '" + formType + "' not found");
        }

        String serviceName = authService.authenticate(serviceAuthHeader);
        logger.info("Request received to validate ocr data from service {}", serviceName);

        authService.assertIsAllowedService(serviceName);

        OcrValidationResult result = ocrDataValidator.validate(FormType.valueOf(formType), request.getOcrDataFields());

        return ok().body(new OcrValidationResponse(result.warnings, result.errors, result.status));
    }

}
