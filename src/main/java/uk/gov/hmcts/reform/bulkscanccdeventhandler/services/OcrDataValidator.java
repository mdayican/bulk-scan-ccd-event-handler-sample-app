package uk.gov.hmcts.reform.bulkscanccdeventhandler.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.model.FormType;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.model.OcrDataField;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.model.OcrFieldNames;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.model.out.OcrValidationResult;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.model.out.ValidationStatus;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.util.OcrFormValidationHelper;

import java.util.Arrays;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.model.FormType.CONTACT;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.model.FormType.PERSONAL;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.model.OcrFieldNames.ADDRESS_LINE_1;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.model.OcrFieldNames.CONTACT_NUMBER;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.model.OcrFieldNames.COUNTRY;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.model.OcrFieldNames.EMAIL;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.model.OcrFieldNames.FIRST_NAME;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.model.OcrFieldNames.LAST_NAME;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.model.OcrFieldNames.POST_CODE;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.model.out.ValidationStatus.ERRORS;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.model.out.ValidationStatus.SUCCESS;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.model.out.ValidationStatus.WARNINGS;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.util.OcrFormValidationHelper.getOcrFieldNames;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.util.OcrFormValidationHelper.isValidEmailAddress;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.util.OcrFormValidationHelper.isValidPhoneNumber;

@Service
public class OcrDataValidator {

    private static final Logger log = LoggerFactory.getLogger(OcrDataValidator.class);

    public OcrValidationResult validate(FormType formType, List<OcrDataField> ocrData) {
        List<String> duplicateOcrKeys = OcrFormValidationHelper.findDuplicateOcrKeys(ocrData);

        if (duplicateOcrKeys.isEmpty()) {
            List<String> errors = validateMandatoryFields(formType, ocrData);
            List<String> warnings = validateOptionalFields(formType, ocrData);

            return new OcrValidationResult(
                warnings, errors, getValidationStatus(!errors.isEmpty(), !warnings.isEmpty())
            );
        } else {
            String duplicateKeys = String.join(",", duplicateOcrKeys);
            log.info("Found duplicate keys in OCR data. {}", duplicateKeys);

            String errorMessage = String.format("Invalid OCR data. Duplicate keys exist: %s", duplicateKeys);
            return new OcrValidationResult(emptyList(), singletonList(errorMessage), ERRORS);
        }
    }

    private List<String> validateMandatoryFields(FormType formType, List<OcrDataField> ocrData) {
        List<String> mandatoryFields = getMandatoryFieldsForForm(formType);
        List<String> missingFields = OcrFormValidationHelper.findBlankFields(mandatoryFields, ocrData);
        List<String> errors = OcrFormValidationHelper.getErrorMessagesForMissingFields(missingFields);

        if (formType.equals(CONTACT)) {
            String email = OcrFormValidationHelper.findOcrFormFieldValue(EMAIL, ocrData);
            String phone = OcrFormValidationHelper.findOcrFormFieldValue(CONTACT_NUMBER, ocrData);

            if (!errors.contains(EMAIL) && !isValidEmailAddress(email)) {
                errors.add("Invalid email address");
            }
            if (!errors.contains(CONTACT_NUMBER) && !isValidPhoneNumber(phone)) {
                errors.add("Invalid phone number");
            }
        }

        return errors;
    }

    private List<String> validateOptionalFields(FormType formType, List<OcrDataField> ocrData) {
        List<String> optionalFields = getOptionalFieldsForForm(formType);
        List<String> ocrInputFields = getOcrFieldNames(ocrData);

        List<String> missingFields = OcrFormValidationHelper.findMissingFields(optionalFields, ocrInputFields);
        return OcrFormValidationHelper.getErrorMessagesForMissingFields(missingFields);
    }

    private List<String> getMandatoryFieldsForForm(FormType formType) {
        if (formType.equals(CONTACT)) {
            return asList(
                ADDRESS_LINE_1,
                EMAIL,
                POST_CODE,
                COUNTRY,
                CONTACT_NUMBER
            );

        } else if (formType.equals(PERSONAL)) {
            return Arrays.asList(
                FIRST_NAME,
                LAST_NAME
            );
        }
        //TODO: Change ocr validation controller to have formType as a path parameter
        // or throw exception for invalid form type

        log.info("Invalid Form type {}", formType);
        return emptyList();
    }

    private List<String> getOptionalFieldsForForm(FormType formType) {
        if (formType.equals(CONTACT)) {
            return asList(
                OcrFieldNames.ADDRESS_LINE_1,
                OcrFieldNames.EMAIL,
                OcrFieldNames.POST_CODE,
                OcrFieldNames.COUNTRY,
                OcrFieldNames.CONTACT_NUMBER
            );
        } else if (formType.equals(PERSONAL)) {
            return singletonList(OcrFieldNames.DATE_OF_BIRTH);
        }
        return emptyList();
    }

    private ValidationStatus getValidationStatus(boolean errorsExist, boolean warningsExist) {
        if (errorsExist) {
            return ERRORS;
        }

        if (warningsExist) {
            return WARNINGS;
        }
        return SUCCESS;
    }

}
