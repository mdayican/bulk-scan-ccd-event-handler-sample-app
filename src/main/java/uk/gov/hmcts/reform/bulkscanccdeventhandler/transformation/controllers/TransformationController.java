package uk.gov.hmcts.reform.bulkscanccdeventhandler.transformation.controllers;

import org.slf4j.Logger;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.common.auth.AuthService;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.transformation.model.in.ExceptionRecord;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.transformation.model.out.SuccessfulTransformationResponse;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.transformation.services.ExceptionRecordToCaseTransformer;

import javax.validation.Valid;

import static org.slf4j.LoggerFactory.getLogger;

@RestController
public class TransformationController {

    private static final Logger LOGGER = getLogger(TransformationController.class);

    private final AuthService authService;
    private final ExceptionRecordToCaseTransformer transformer;

    public TransformationController(
        AuthService authService,
        ExceptionRecordToCaseTransformer transformer
    ) {
        this.authService = authService;
        this.transformer = transformer;
    }

    @PostMapping("/transform-exception-record")
    public SuccessfulTransformationResponse transform(
        @RequestHeader(name = "ServiceAuthorization", required = false) String serviceAuthHeader,
        @Valid @RequestBody ExceptionRecord exceptionRecord
    ) {
        String serviceName = authService.authenticate(serviceAuthHeader);
        LOGGER.info("Request received to transform from service {}", serviceName);

        authService.assertIsAllowedService(serviceName);

        return transformer.toCase(exceptionRecord);
    }
}
