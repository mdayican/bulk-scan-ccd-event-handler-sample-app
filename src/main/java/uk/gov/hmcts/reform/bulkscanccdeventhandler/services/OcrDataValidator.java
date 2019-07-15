package uk.gov.hmcts.reform.bulkscanccdeventhandler.services;

import uk.gov.hmcts.reform.bulkscanccdeventhandler.model.FormType;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.model.OcrDataField;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.model.out.OcrValidationResult;

import java.util.List;

import static java.util.Collections.emptyList;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.model.out.ValidationStatus.SUCCESS;

public final class OcrDataValidator {

    // prevent instantiation
    private OcrDataValidator() {
    }

    public static OcrValidationResult validate(FormType formType, List<OcrDataField> ocrDataFields) {
        //TODO validate OCr data
        return new OcrValidationResult(emptyList(), emptyList(), SUCCESS);
    }

}
