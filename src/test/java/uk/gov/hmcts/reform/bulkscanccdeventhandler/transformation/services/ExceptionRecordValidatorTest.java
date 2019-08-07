package uk.gov.hmcts.reform.bulkscanccdeventhandler.transformation.services;

import org.junit.jupiter.api.Test;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.ocrvalidation.model.in.OcrDataField;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.transformation.model.in.ExceptionRecord;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.transformation.model.in.JourneyClassification;

import java.util.List;

import static java.time.LocalDateTime.now;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.common.OcrFieldNames.CONTACT_NUMBER;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.common.OcrFieldNames.EMAIL;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.common.OcrFieldNames.FIRST_NAME;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.common.OcrFieldNames.LAST_NAME;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.common.OcrFieldNames.getRequiredFields;

public class ExceptionRecordValidatorTest {

    private final ExceptionRecordValidator validator = new ExceptionRecordValidator();

    @Test
    public void should_throw_exception_if_required_ocr_fields_are_missing() {
        // given
        ExceptionRecord er =
            exceptionRecordWithOcr(
                asList(
                    new OcrDataField(CONTACT_NUMBER, "555-555-555"),
                    new OcrDataField(EMAIL, "test@example.com")
                )
            );

        // when
        Throwable exc = catchThrowable(() -> validator.assertIsValid(er));

        // then
        assertThat(exc)
            .isInstanceOf(InvalidExceptionRecordException.class)
            .hasMessageContaining("Missing required fields")
            .hasMessageContaining(FIRST_NAME)
            .hasMessageContaining(LAST_NAME);
    }

    @Test
    public void should_not_throw_exception_if_exception_record_is_valid() {
        // given
        ExceptionRecord er =
            exceptionRecordWithOcr(
                getRequiredFields()
                    .stream()
                    .map(req -> new OcrDataField(req, "value"))
                    .collect(toList())
            );

        // when
        Throwable exc = catchThrowable(() -> validator.assertIsValid(er));

        // then
        assertThat(exc).isNull();
    }

    private ExceptionRecord exceptionRecordWithOcr(List<OcrDataField> ocrData) {
        return new ExceptionRecord(
            "er-id",
            "er-case-type",
            "er-pobox",
            "er-jurisdiction",
            JourneyClassification.NEW_APPLICATION,
            now(),
            now(),
            emptyList(),
            ocrData
        );
    }
}
