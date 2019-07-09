package uk.gov.hmcts.reform.bulkscanccdeventhandler.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.model.CcdCollectionElement;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.model.OcrData;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.model.OcrDataField;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.model.ResultOrErrors;

import java.util.List;

import static com.google.common.base.Strings.isNullOrEmpty;
import static java.util.stream.Collectors.toList;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.model.ResultOrErrors.errors;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.model.ResultOrErrors.result;

public class OcrFieldRetriever {

    private final OcrData ocrData;
    private final ObjectMapper mapper;

    public OcrFieldRetriever(OcrData ocrData, ObjectMapper mapper) {
        this.ocrData = ocrData;
        this.mapper = mapper;
    }

    public <T> ResultOrErrors<T> getFieldValue(
        String fieldName,
        boolean isRequired,
        Class<T> fieldClass
    ) {
        List<CcdCollectionElement<OcrDataField>> fieldValues = findValuesForField(fieldName);

        if (fieldValues.isEmpty()) {
            return isRequired
                ? errors(String.format("OCR field %s is missing", fieldName))
                : result(null);
        } else if (fieldValues.size() == 1) {
            String stringValue = fieldValues.get(0).value.value;

            if (isNullOrEmpty(stringValue)) {
                return isRequired
                    ? errors(String.format("OCR field %s must have a value", fieldName))
                    : result(null);
            } else {
                return convertFieldValue(fieldName, stringValue, fieldClass);
            }
        } else {
            return errors(String.format("There must be only one OCR field named %s", fieldName));
        }
    }

    private <T> ResultOrErrors<T> convertFieldValue(
        String fieldName,
        String stringValue,
        Class<T> fieldClass
    ) {
        try {
            return result(mapper.convertValue(stringValue, fieldClass));
        } catch (IllegalArgumentException e) {
            return errors(String.format("OCR field %s has invalid format", fieldName));
        }
    }

    private List<CcdCollectionElement<OcrDataField>> findValuesForField(String fieldName) {
        return ocrData
            .fields
            .stream()
            .filter(e -> fieldName.equals(e.value.key))
            .collect(toList());
    }
}
