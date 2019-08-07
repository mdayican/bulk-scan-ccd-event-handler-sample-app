package uk.gov.hmcts.reform.bulkscanccdeventhandler.transformation.services.utils;

import uk.gov.hmcts.reform.bulkscanccdeventhandler.ocrvalidation.model.in.OcrDataField;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.transformation.model.in.ExceptionRecord;

import java.util.List;

public final class OcrFieldExtractor {

    public static String get(ExceptionRecord er, String name) {
        return get(er.ocrDataFields, name);
    }

    public static String get(List<OcrDataField> ocrFields, String name) {
        return ocrFields
            .stream()
            .filter(it -> it.name.equals(name))
            .map(it -> it.value)
            .findFirst()
            .orElse(null);
    }

    private OcrFieldExtractor() {
        // util class
    }
}
