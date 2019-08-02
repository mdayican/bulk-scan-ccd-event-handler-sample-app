package uk.gov.hmcts.reform.bulkscanccdeventhandler.ocrvalidation.services;

import org.apache.commons.validator.routines.EmailValidator;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.ocrvalidation.model.in.OcrDataField;

import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang.StringUtils.isBlank;

public final class OcrFormValidationHelper {

    private OcrFormValidationHelper() {
        // util class
    }

    public static List<String> getOcrFieldNames(List<OcrDataField> ocrData) {
        return ocrData
            .stream()
            .map(field -> field.name)
            .collect(toList());
    }

    public static List<String> findBlankFields(List<String> fieldNames, List<OcrDataField> ocrData) {
        return fieldNames
            .stream()
            .filter(field -> isBlank(OcrFormValidationHelper.findOcrFormFieldValue(field, ocrData)))
            .collect(toList());
    }

    /* returns missing expected fields in the input collection */
    public static List<String> findMissingFields(List<String> expectedFields, List<String> inputFields) {
        return expectedFields.stream()
            .filter(item -> !inputFields.contains(item))
            .collect(toList());
    }

    public static String findOcrFormFieldValue(String fieldName, List<OcrDataField> ocrData) {
        return ocrData
            .stream()
            .filter(e -> fieldName.equals(e.name))
            .findFirst()
            .map(v -> v.value)
            .orElse(null);
    }

    public static boolean isValidEmailAddress(String email) {
        return EmailValidator.getInstance().isValid(email);
    }

    public static boolean isValidPhoneNumber(String phone) {
        return Pattern.compile("\\d{10}").matcher(phone).matches();
    }

    public static List<String> findDuplicateOcrFields(List<OcrDataField> ocrFields) {
        return ocrFields
            .stream()
            .collect(groupingBy(it -> it.name, counting()))
            .entrySet()
            .stream()
            .filter(entry -> entry.getValue() > 1)
            .map(Entry::getKey)
            .collect(toList());
    }

    public static List<String> getErrorMessagesForMissingFields(Collection<String> missingFields) {
        return missingFields
            .stream()
            .map(field -> String.format("%s is missing", field))
            .collect(toList());
    }
}
