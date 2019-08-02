package uk.gov.hmcts.reform.bulkscanccdeventhandler.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.model.CcdCollectionElement;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.model.OcrData;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.model.ResultOrErrors;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.ocrvalidation.model.in.OcrDataField;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

public class OcrFieldRetrieverTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    public void setUp() {
        mapper.registerModule(new JavaTimeModule());
    }

    @Test
    public void should_return_error_when_field_required_but_not_found() {
        OcrData ocrData = ocrData(new OcrDataField("field_1", "value"));

        ResultOrErrors<String> result = ocrFieldRetriever(ocrData).getFieldValue(
            "non-existing-field",
            true,
            String.class
        );

        assertThat(result.isSuccessful()).isFalse();
        assertThat(result.errors).isEqualTo(Arrays.asList("OCR field non-existing-field is missing"));
    }

    @Test
    public void should_return_null_when_field_not_found_but_not_required() {
        OcrData ocrData = ocrData(new OcrDataField("field_1", "value"));

        ResultOrErrors<String> result = ocrFieldRetriever(ocrData).getFieldValue(
            "non-existing-field",
            false,
            String.class
        );

        assertThat(result.isSuccessful()).isTrue();
        assertThat(result.get()).isNull();
    }

    @Test
    public void should_return_error_when_field_is_required_but_value_is_null() {
        String fieldName = "existing_field";
        OcrData ocrData = ocrData(new OcrDataField(fieldName, null));

        ResultOrErrors<String> result =
            ocrFieldRetriever(ocrData).getFieldValue(fieldName, true, String.class);

        assertThat(result.isSuccessful()).isFalse();
        assertThat(result.errors).isEqualTo(Arrays.asList("OCR field existing_field must have a value"));
    }

    @Test
    public void should_return_error_when_field_is_required_but_value_is_empty() {
        String fieldName = "existing_field";
        OcrData ocrData = ocrData(new OcrDataField(fieldName, ""));

        ResultOrErrors<String> result =
            ocrFieldRetriever(ocrData).getFieldValue(fieldName, true, String.class);

        assertThat(result.isSuccessful()).isFalse();
        assertThat(result.errors).isEqualTo(Arrays.asList("OCR field existing_field must have a value"));
    }

    @Test
    public void should_return_null_when_value_is_null_but_field_not_required() {
        String fieldName = "existing_field";
        OcrData ocrData = ocrData(new OcrDataField(fieldName, null));

        ResultOrErrors<String> result =
            ocrFieldRetriever(ocrData).getFieldValue(fieldName, false, String.class);

        assertThat(result.isSuccessful()).isTrue();
        assertThat(result.get()).isNull();
    }

    @Test
    public void should_return_null_when_value_is_empty_but_field_not_required() {
        String fieldName = "existing_field";
        OcrData ocrData = ocrData(new OcrDataField(fieldName, ""));

        ResultOrErrors<String> result =
            ocrFieldRetriever(ocrData).getFieldValue(fieldName, false, String.class);

        assertThat(result.isSuccessful()).isTrue();
        assertThat(result.get()).isNull();
    }

    @Test
    public void should_return_error_when_multiple_instances_of_fields_found() {
        String fieldName = "existing_field";
        OcrData ocrData = ocrData(
            new OcrDataField(fieldName, "value 1"),
            new OcrDataField(fieldName, "value 2")
        );

        ResultOrErrors<String> result =
            ocrFieldRetriever(ocrData).getFieldValue(fieldName, false, String.class);

        assertThat(result.isSuccessful()).isFalse();
        assertThat(result.errors).isEqualTo(
            Arrays.asList("There must be only one OCR field named existing_field")
        );
    }

    @Test
    public void should_return_error_when_field_deserialisation_fails() {
        String fieldName = "date_field";
        OcrData ocrData = ocrData(new OcrDataField(fieldName, "not-a-date"));

        ResultOrErrors<LocalDate> result =
            ocrFieldRetriever(ocrData).getFieldValue(fieldName, false, LocalDate.class);

        assertThat(result.isSuccessful()).isFalse();
        assertThat(result.errors).isEqualTo(Arrays.asList("OCR field date_field has invalid format"));
    }

    @Test
    public void should_return_deserialisation_result_when_successful() {
        String dateFieldName = "date_field";
        String intFieldName = "int_field";
        String boolFieldName = "bool_field";
        String stringFieldName = "string_field";

        OcrFieldRetriever fieldRetriever = ocrFieldRetriever(
            ocrData(
                new OcrDataField(dateFieldName, "2019-07-08"),
                new OcrDataField(intFieldName, "3241"),
                new OcrDataField(boolFieldName, "true"),
                new OcrDataField(stringFieldName, "some string")
            ));

        assertSuccessfulResultWithValue(
            fieldRetriever.getFieldValue(dateFieldName, false, LocalDate.class),
            LocalDate.of(2019, 7, 8)
        );

        assertSuccessfulResultWithValue(
            fieldRetriever.getFieldValue(intFieldName, false, Integer.class),
            3241
        );

        assertSuccessfulResultWithValue(
            fieldRetriever.getFieldValue(boolFieldName, false, Boolean.class),
            true
        );

        assertSuccessfulResultWithValue(
            fieldRetriever.getFieldValue(stringFieldName, false, String.class),
            "some string"
        );
    }

    private <T> void assertSuccessfulResultWithValue(ResultOrErrors<T> result, T expectedValue) {
        assertThat(result.isSuccessful()).isTrue();
        assertThat(result.get()).isEqualTo(expectedValue);
    }

    private OcrFieldRetriever ocrFieldRetriever(OcrData ocrData) {
        return new OcrFieldRetriever(ocrData, mapper);
    }

    private OcrData ocrData(OcrDataField... fields) {
        return new OcrData(
            Stream.of(fields)
                .map(field -> new CcdCollectionElement<>(field))
                .collect(toList())
        );

    }
}
