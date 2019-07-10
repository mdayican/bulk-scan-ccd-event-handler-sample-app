package uk.gov.hmcts.reform.bulkscanccdeventhandler.services;

import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.handler.model.ExceptionRecord;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.model.CcdCase;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.model.ResultOrErrors;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.transformer.ExceptionRecordToCaseTransformer;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.transformer.model.TransformationResult;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.util.Collections.emptyList;

@Service
public class CaseTransformer implements ExceptionRecordToCaseTransformer {

    private static final String caseCreationEventId = "createCase";
    private static final String caseTypeId = "Bulk_Scanned";

    private final CaseDataCreator caseDataCreator;

    public CaseTransformer(CaseDataCreator caseDataCreator) {
        this.caseDataCreator = caseDataCreator;
    }

    @Override
    public TransformationResult transform(ExceptionRecord exceptionRecord) {

        ResultOrErrors<CcdCase> caseDataCreationResult =
            caseDataCreator.tryCreateCaseData(exceptionRecord.data);

        if (caseDataCreationResult.isSuccessful()) {
            CcdCase caseData = caseDataCreationResult.get();

            return new TransformationResult(
                getWarnings(caseData),
                emptyList(),
                caseData,
                exceptionRecord.jurisdiction,
                caseTypeId,
                caseCreationEventId
            );
        } else {
            return new TransformationResult(
                emptyList(),
                caseDataCreationResult.errors,
                null,
                null,
                null,
                null
            );
        }
    }

    private List<String> getWarnings(CcdCase caseData) {
        List<String> warnings = new ArrayList<>();

        if (caseData.dateOfBirth.isBefore(LocalDate.of(1900, 1, 1))) {
            warnings.add("Data of birth is from before year 1900 - most likely invalid");
        }

        if (caseData.scannedDocuments.isEmpty()) {
            return Arrays.asList("There are no scanned documents");
        } else {
            return emptyList();
        }
    }
}
