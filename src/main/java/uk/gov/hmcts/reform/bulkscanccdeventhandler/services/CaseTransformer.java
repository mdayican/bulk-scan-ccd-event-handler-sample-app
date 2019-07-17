package uk.gov.hmcts.reform.bulkscanccdeventhandler.services;

import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.handler.model.ExceptionRecord;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.model.BulkScanCase;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.model.ResultOrErrors;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.transformer.ExceptionRecordToCaseTransformer;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.transformer.model.ErrorTransformationResult;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.transformer.model.OkTransformationResult;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.transformer.model.TransformationResult;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.emptyList;

@Service
public class CaseTransformer implements ExceptionRecordToCaseTransformer {

    private static final String CASE_CREATION_EVENT_ID = "createCase";
    private static final String CASE_TYPE_ID = "Bulk_Scanned";
    private static final String JURISDICTION = "BULKSCAN";

    private final CaseDataCreator caseDataCreator;

    public CaseTransformer(CaseDataCreator caseDataCreator) {
        this.caseDataCreator = caseDataCreator;
    }

    @Override
    public TransformationResult transform(ExceptionRecord exceptionRecord) {

        ResultOrErrors<BulkScanCase> caseDataCreationResult =
            caseDataCreator.createCaseData(exceptionRecord.data);

        if (caseDataCreationResult.isSuccessful()) {
            BulkScanCase caseData = caseDataCreationResult.get();

            return new OkTransformationResult(
                getWarnings(caseData),
                caseData,
                JURISDICTION,
                CASE_TYPE_ID,
                CASE_CREATION_EVENT_ID
            );
        } else {
            return new ErrorTransformationResult(
                emptyList(),
                caseDataCreationResult.errors
            );
        }
    }

    private List<String> getWarnings(BulkScanCase caseData) {
        List<String> warnings = new ArrayList<>();

        if (caseData.dateOfBirth.isBefore(LocalDate.of(1900, 1, 1))) {
            warnings.add("Date of birth is from before year 1900");
        }

        if (caseData.scannedDocuments.isEmpty()) {
            warnings.add("There are no scanned documents");
        }

        return warnings;
    }
}
