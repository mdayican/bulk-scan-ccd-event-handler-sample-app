package uk.gov.hmcts.reform.bulkscanccdeventhandler.validators;

import org.junit.jupiter.api.Test;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.ocrvalidation.model.in.OcrDataField;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.ocrvalidation.model.out.ValidationStatus;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.ocrvalidation.services.OcrDataValidator;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.ocrvalidation.services.OcrValidationResult;

import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
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
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.ocrvalidation.model.FormType.PERSONAL;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.ocrvalidation.model.out.ValidationStatus.ERRORS;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.ocrvalidation.model.out.ValidationStatus.SUCCESS;

class OcrDataValidatorTest {

    private final OcrDataValidator validator = new OcrDataValidator();

    @Test
    void should_return_errors_and_warnings_when_mandatory_and_optional_fields_are_missing() {
        // given
        List<OcrDataField> ocrDataFields = singletonList(new OcrDataField(FIRST_NAME, "test"));

        // when
        OcrValidationResult result = validator.validate(PERSONAL, ocrDataFields);

        // then
        assertThat(result).isNotNull();
        assertThat(result)
            .extracting("errors", "warnings", "status")
            .containsExactlyInAnyOrder(
                singletonList("last_name is missing"),
                asList(
                    "address_line_1 is missing",
                    "address_line_2 is missing",
                    "address_line_3 is missing",
                    "post_town is missing",
                    "county is missing",
                    "country is missing",
                    "contact_number is missing",
                    "post_code is missing",
                    "email is missing",
                    "date_of_birth is missing"
                ),
                ValidationStatus.ERRORS
            );
    }

    @Test
    void should_return_errors_and_warnings_when_mandatory_fields_are_empty_and_optional_fields_are_missing() {
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
                asList(
                    "address_line_1 is missing",
                    "address_line_2 is missing",
                    "address_line_3 is missing",
                    "post_town is missing",
                    "county is missing",
                    "country is missing",
                    "contact_number is missing",
                    "post_code is missing",
                    "email is missing"
                ),
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
            new OcrDataField("unrecognised_field", "xyz"),
            new OcrDataField(ADDRESS_LINE_1, "1 Street"),
            new OcrDataField(ADDRESS_LINE_2, "Victoria Street"),
            new OcrDataField(ADDRESS_LINE_3, "London"),
            new OcrDataField(POST_TOWN, "LONDON"),
            new OcrDataField(POST_CODE, "SW1 1ER"),
            new OcrDataField(COUNTY, "county"),
            new OcrDataField(COUNTRY, "UK"),
            new OcrDataField(EMAIL, "xyz@something.com"),
            new OcrDataField(CONTACT_NUMBER, "0123456789")
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
    void should_return_no_errors_when_ocr_data_contains_values_for_all_form_fields() {
        // given
        List<OcrDataField> ocrDataFields = asList(
            new OcrDataField(FIRST_NAME, "test"),
            new OcrDataField(LAST_NAME, "name"),
            new OcrDataField(DATE_OF_BIRTH, "01-01-1990"),
            new OcrDataField(ADDRESS_LINE_1, "1 Street"),
            new OcrDataField(ADDRESS_LINE_2, "Victoria Street"),
            new OcrDataField(ADDRESS_LINE_3, "London"),
            new OcrDataField(POST_TOWN, "LONDON"),
            new OcrDataField(POST_CODE, "SW1 1ER"),
            new OcrDataField(COUNTY, "county"),
            new OcrDataField(COUNTRY, "UK"),
            new OcrDataField(EMAIL, "xyz@something.com"),
            new OcrDataField(CONTACT_NUMBER, "0123456789")
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
            new OcrDataField(FIRST_NAME, "test"),
            new OcrDataField(LAST_NAME, "name"),
            new OcrDataField(DATE_OF_BIRTH, "01-01-1990"),
            new OcrDataField("unrecognised_field", "xyz"),
            new OcrDataField(ADDRESS_LINE_1, "1 Street"),
            new OcrDataField(ADDRESS_LINE_2, "Victoria Street"),
            new OcrDataField(ADDRESS_LINE_3, "London"),
            new OcrDataField(POST_TOWN, "LONDON"),
            new OcrDataField(POST_CODE, "SW1 1ER"),
            new OcrDataField(COUNTY, "county"),
            new OcrDataField(COUNTRY, "UK"),
            new OcrDataField(EMAIL, "xyz@"),
            new OcrDataField(CONTACT_NUMBER, "0123456789")
        );

        // when
        OcrValidationResult result = validator.validate(PERSONAL, ocrDataFields);

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
            new OcrDataField(FIRST_NAME, "test"),
            new OcrDataField(LAST_NAME, "name"),
            new OcrDataField(DATE_OF_BIRTH, "01-01-1990"),
            new OcrDataField("unrecognised_field", "xyz"),
            new OcrDataField(ADDRESS_LINE_1, "1 Street"),
            new OcrDataField(ADDRESS_LINE_2, "Victoria Street"),
            new OcrDataField(ADDRESS_LINE_3, "London"),
            new OcrDataField(POST_TOWN, "LONDON"),
            new OcrDataField(POST_CODE, "SW1 1ER"),
            new OcrDataField(COUNTY, "county"),
            new OcrDataField(COUNTRY, "UK"),
            new OcrDataField(EMAIL, "xyz@something.com"),
            new OcrDataField(CONTACT_NUMBER, "invalid") //invalid phone number
        );

        // when
        OcrValidationResult result = validator.validate(PERSONAL, ocrDataFields);

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
}
