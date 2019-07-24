package uk.gov.hmcts.reform.bulkscanccdeventhandler.validators;

import org.junit.jupiter.api.Test;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.model.OcrDataField;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.model.out.OcrValidationResult;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.model.out.ValidationStatus;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.services.OcrDataValidator;

import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.model.FormType.CONTACT;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.model.FormType.PERSONAL;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.model.OcrFieldNames.ADDRESS_LINE_1;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.model.OcrFieldNames.CONTACT_NUMBER;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.model.OcrFieldNames.COUNTRY;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.model.OcrFieldNames.DATE_OF_BIRTH;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.model.OcrFieldNames.EMAIL;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.model.OcrFieldNames.FIRST_NAME;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.model.OcrFieldNames.LAST_NAME;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.model.OcrFieldNames.POST_CODE;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.model.out.ValidationStatus.ERRORS;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.model.out.ValidationStatus.SUCCESS;

class OcrDataValidatorTest {

    private final OcrDataValidator validator = new OcrDataValidator();

    @Test
    void should_return_errors_when_mandatory_fields_are_missing() {
        // given
        List<OcrDataField> ocrDataFields = singletonList(new OcrDataField(FIRST_NAME, "test"));

        // when
        OcrValidationResult result = validator.validate(PERSONAL, ocrDataFields);

        // then
        assertThat(result).isNotNull();
        assertThat(result)
            .extracting("errors", "warnings", "status")
            .containsExactly(
                singletonList("last_name is missing"),
                singletonList("date_of_birth is missing"),
                ValidationStatus.ERRORS
            );
    }

    @Test
    void should_return_errors_when_ocr_data_contains_duplicate_field_names() {
        // given
        List<OcrDataField> ocrDataFields = asList(
            new OcrDataField(FIRST_NAME, "name"),
            new OcrDataField(LAST_NAME, "xyz"), //duplicate field name
            new OcrDataField(LAST_NAME, "abc"), //duplicate field name
            new OcrDataField(DATE_OF_BIRTH, "01-01-1990")
        );

        // when
        OcrValidationResult result = validator.validate(PERSONAL, ocrDataFields);

        // then
        assertThat(result).isNotNull();
        assertThat(result)
            .extracting("errors", "warnings", "status")
            .containsExactly(
                singletonList("Invalid OCR data. Duplicate fields exist: last_name"),
                emptyList(),
                ValidationStatus.ERRORS
            );
    }

    @Test
    void should_ignore_unrecognised_form_fields_and_return_success() {
        // given
        List<OcrDataField> ocrDataFields = asList(
            new OcrDataField(FIRST_NAME, "test"),
            new OcrDataField(LAST_NAME, "name"),
            new OcrDataField(DATE_OF_BIRTH, "01-01-1990"),
            new OcrDataField("unrecognised_field", "xyz")
        );

        // when
        OcrValidationResult result = validator.validate(PERSONAL, ocrDataFields);

        // then
        assertThat(result).isNotNull();
        assertThat(result)
            .extracting("errors", "warnings", "status")
            .containsExactly(emptyList(), emptyList(), SUCCESS);
    }

    @Test
    void should_return_errors_when_mandatory_fields_are_empty() {
        // given
        List<OcrDataField> ocrDataFields = asList(
            new OcrDataField(FIRST_NAME, "test"),
            new OcrDataField(LAST_NAME, ""),
            new OcrDataField(DATE_OF_BIRTH, "01-01-1990")
        );

        // when
        OcrValidationResult result = validator.validate(PERSONAL, ocrDataFields);

        // then
        assertThat(result).isNotNull();
        assertThat(result)
            .extracting("errors", "warnings", "status")
            .containsExactly(
                singletonList("last_name is missing"),
                emptyList(),
                ValidationStatus.ERRORS
            );
    }

    @Test
    void should_return_warnings_when_optional_fields_are_empty() {
        // given
        List<OcrDataField> ocrDataFields = asList(
            new OcrDataField(FIRST_NAME, "test"),
            new OcrDataField(LAST_NAME, "test")
        );

        // when
        OcrValidationResult result = validator.validate(PERSONAL, ocrDataFields);

        // then
        assertThat(result).isNotNull();
        assertThat(result)
            .extracting("errors", "warnings", "status")
            .containsExactly(
                emptyList(),
                singletonList("date_of_birth is missing"),
                ValidationStatus.WARNINGS
            );
    }

    @Test
    void should_return_no_errors_when_ocr_data_contains_values_for_all_form_fields() {
        // given
        List<OcrDataField> ocrDataFields = asList(
            new OcrDataField(FIRST_NAME, "test"),
            new OcrDataField(LAST_NAME, "name"),
            new OcrDataField(DATE_OF_BIRTH, "01-01-1990")
        );

        // when
        OcrValidationResult result = validator.validate(PERSONAL, ocrDataFields);

        // then
        assertThat(result).isNotNull();
        assertThat(result)
            .extracting("errors", "warnings", "status")
            .containsExactly(emptyList(), emptyList(), SUCCESS);
    }

    @Test
    void should_validate_email_format_and_return_errors_for_invalid_value() {
        // given
        List<OcrDataField> ocrDataFields = asList(
            new OcrDataField(ADDRESS_LINE_1, "1 Street"),
            new OcrDataField(POST_CODE, "SW1 1ER"),
            new OcrDataField(COUNTRY, "UK"),
            new OcrDataField(EMAIL, "xyz"), //invalid email address
            new OcrDataField(CONTACT_NUMBER, "0123456789")
        );

        // when
        OcrValidationResult result = validator.validate(CONTACT, ocrDataFields);

        // then
        assertThat(result).isNotNull();
        assertThat(result)
            .extracting("errors", "warnings", "status")
            .containsExactly(
                singletonList("Invalid email address"),
                emptyList(),
                ERRORS
            );
    }

    @Test
    void should_validate_phone_number_and_return_errors_for_invalid_value() {
        // given
        List<OcrDataField> ocrDataFields = asList(
            new OcrDataField(ADDRESS_LINE_1, "1 Street"),
            new OcrDataField(POST_CODE, "SW1 1ER"),
            new OcrDataField(COUNTRY, "UK"),
            new OcrDataField(EMAIL, "test@test.com"),
            new OcrDataField(CONTACT_NUMBER, "invalid") //invalid phone number
        );

        // when
        OcrValidationResult result = validator.validate(CONTACT, ocrDataFields);

        // then
        assertThat(result).isNotNull();
        assertThat(result)
            .extracting("errors", "warnings", "status")
            .containsExactly(
                singletonList("Invalid phone number"),
                emptyList(),
                ERRORS
            );
    }

    @Test
    void should_return_success_when_contact_form_fields_format_is_valid() {
        // given
        List<OcrDataField> ocrDataFields = asList(
            new OcrDataField(ADDRESS_LINE_1, "1 Street"),
            new OcrDataField(POST_CODE, "SW1 1ER"),
            new OcrDataField(COUNTRY, "UK"),
            new OcrDataField(EMAIL, "test@test.com"), //valid email format
            new OcrDataField(CONTACT_NUMBER, "0123456789") //valid phone number format
        );

        // when
        OcrValidationResult result = validator.validate(CONTACT, ocrDataFields);

        // then
        assertThat(result).isNotNull();
        assertThat(result)
            .extracting("errors", "warnings", "status")
            .containsExactly(emptyList(), emptyList(), SUCCESS);
    }

}
