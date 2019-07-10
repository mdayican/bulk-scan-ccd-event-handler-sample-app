package uk.gov.hmcts.reform.bulkscanccdeventhandler.services;

import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.handler.ExceptionRecordEventHandler;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.handler.model.CaseCreationRequest;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.handler.model.CaseCreationResult;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.handler.model.ExceptionRecord;
import uk.gov.hmcts.reform.ccd.client.model.CaseDetails;

@Service
public class CaseCreationService {

    private final ExceptionRecordEventHandler exceptionRecordEventHandler;

    public CaseCreationService(ExceptionRecordEventHandler exceptionRecordEventHandler) {
        this.exceptionRecordEventHandler = exceptionRecordEventHandler;
    }

    public CaseCreationResult handleCaseCreationCallback(
        CaseDetails caseDetails,
        String eventId,
        String idamToken,
        String idamUserId,
        boolean ignoreWarnings
    ) {
        String exceptionRecordId = caseDetails.getId() != null
            ? caseDetails.getId().toString()
            : null;

        ExceptionRecord exceptionRecord = new ExceptionRecord(
            exceptionRecordId,
            caseDetails.getJurisdiction(),
            caseDetails.getState(),
            caseDetails.getCaseTypeId(),
            caseDetails.getData()
        );

        CaseCreationRequest caseCreationRequest = new CaseCreationRequest(
            exceptionRecord,
            eventId,
            idamToken,
            idamUserId,
            ignoreWarnings
        );

        return exceptionRecordEventHandler.handle(caseCreationRequest);
    }
}
