package uk.gov.hmcts.reform.bulkscanccdeventhandler.ocrvalidation.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.ocrvalidation.model.FormType;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.ocrvalidation.model.in.OcrDataField;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.ocrvalidation.model.out.ValidationStatus;

import java.util.Arrays;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.common.OcrFieldNames.ADDRESS_LINE_1;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.common.OcrFieldNames.ADDRESS_LINE_2;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.common.OcrFieldNames.ADDRESS_LINE_3;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.common.OcrFieldNames.CONTACT_NUMBER;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.common.OcrFieldNames.COUNTRY;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.common.OcrFieldNames.COUNTY;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.common.OcrFieldNames.DATE_OF_BIRTH;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.common.OcrFieldNames.EMAIL;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.common.OcrFieldNames.FIRST_NAME;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.common.OcrFieldNames.LAST_NAME;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.common.OcrFieldNames.POST_CODE;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.common.OcrFieldNames.POST_TOWN;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.ocrvalidation.model.FormType.CONTACT;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.ocrvalidation.model.FormType.PERSONAL;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.ocrvalidation.model.out.ValidationStatus.ERRORS;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.ocrvalidation.model.out.ValidationStatus.SUCCESS;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.ocrvalidation.model.out.ValidationStatus.WARNINGS;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.ocrvalidation.services.OcrFormValidationHelper.getOcrFieldNames;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.ocrvalidation.services.OcrFormValidationHelper.isValidEmailAddress;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.ocrvalidation.services.OcrFormValidationHelper.isValidPhoneNumber;

@Service
public class OcrDataValidator {

    private static final Logger log = LoggerFactory.getLogger(OcrDataValidator.class);

    public OcrValidationResult validate(FormType formType, List<OcrDataField> ocrData) {
        List<String> duplicateOcrFields = OcrFormValidationHelper.findDuplicateOcrFields(ocrData);

        if (duplicateOcrFields.isEmpty()) {
            List<String> errors = validateMandatoryFields(formType, ocrData);
            List<String> warnings = validateOptionalFields(formType, ocrData);

            return new OcrValidationResult(
                warnings, errors, getValidationStatus(!errors.isEmpty(), !warnings.isEmpty())
            );
        } else {
            String duplicateFields = String.join(",", duplicateOcrFields);
            log.info("Found duplicate fields in OCR data. {}", duplicateFields);

            String errorMessage = String.format("Invalid OCR data. Duplicate fields exist: %s", duplicateFields);
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

        log.info("Invalid Form type {}", formType);
        return emptyList();
    }

    private List<String> getOptionalFieldsForForm(FormType formType) {
        if (formType.equals(CONTACT)) {
            return asList(
                ADDRESS_LINE_2,
                ADDRESS_LINE_3,
                POST_TOWN,
                COUNTY
            );
        } else if (formType.equals(PERSONAL)) {
            return singletonList(DATE_OF_BIRTH);
        }

        log.info("Invalid Form type {}", formType);
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
