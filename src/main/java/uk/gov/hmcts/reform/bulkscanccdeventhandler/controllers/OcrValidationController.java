package uk.gov.hmcts.reform.bulkscanccdeventhandler.controllers;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.model.in.OcrDataValidationRequest;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.model.out.OcrValidationResponse;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.model.out.OcrValidationResult;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.services.OcrDataValidator;

import javax.validation.Valid;

import static org.springframework.http.ResponseEntity.ok;

@RestController
public class OcrValidationController {

    @PostMapping(
        path = "/validate-ocr-data",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ApiOperation("Validates OCR form data based on form type")
    @ApiResponses({
        @ApiResponse(code = 200, response = OcrValidationResponse.class, message = "Success"),
        @ApiResponse(code = 401, message = "Provided S2S token is missing or invalid")
    })
    public ResponseEntity<OcrValidationResponse> validateOcrData(
        @RequestHeader(name = "ServiceAuthorization", required = false) String serviceAuthHeader,
        @Valid @RequestBody OcrDataValidationRequest request
    ) {
        //TODO: validate s2s token
        OcrValidationResult result = OcrDataValidator.validate(request.getFormType(), request.getOcrDataFields());

        return ok().body(new OcrValidationResponse(result.warnings, result.errors, result.status));
    }
}
