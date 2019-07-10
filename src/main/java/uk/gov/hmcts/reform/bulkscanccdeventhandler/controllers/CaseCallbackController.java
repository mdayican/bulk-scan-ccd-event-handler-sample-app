package uk.gov.hmcts.reform.bulkscanccdeventhandler.controllers;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.handler.model.CaseCreationResult;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.model.CcdCallbackRequest;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.services.CaseCreationService;
import uk.gov.hmcts.reform.ccd.client.model.AboutToStartOrSubmitCallbackResponse;
import uk.gov.hmcts.reform.ccd.client.model.CallbackResponse;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(path = "/callback", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
public class CaseCallbackController {

    public static final String USER_ID = "user-id";

    private final CaseCreationService caseCreationService;

    public CaseCallbackController(CaseCreationService caseCreationService) {
        this.caseCreationService = caseCreationService;
    }

    @PostMapping(path = "/new-application")
    public CallbackResponse createNewApplication(
        @RequestBody CcdCallbackRequest callback,
        @RequestHeader(AUTHORIZATION) String idamToken,
        @RequestHeader(USER_ID) String userId
    ) {
        CaseCreationResult caseCreationResult = caseCreationService.handleCaseCreationCallback(
            callback.caseDetails,
            idamToken,
            userId,
            callback.eventId,
            callback.ignoreWarnings
        );

        return AboutToStartOrSubmitCallbackResponse
            .builder()
            .errors(caseCreationResult.errors)
            .warnings(caseCreationResult.warnings)
            .build();
    }
}
