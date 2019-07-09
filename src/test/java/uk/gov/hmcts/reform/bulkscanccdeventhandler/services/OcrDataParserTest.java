package uk.gov.hmcts.reform.bulkscanccdeventhandler.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import org.junit.jupiter.api.Test;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.model.CcdCollectionElement;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.model.OcrData;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.model.OcrDataField;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.model.ResultOrErrors;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class OcrDataParserTest {

    private final ObjectMapper mapper = new ObjectMapper();
    private final OcrDataParser ocrDataParser = new OcrDataParser(mapper);

    @Test
    public void should_return_ocr_data_when_valid() {
        List<Object> rawOcrData = createRawOcrData(
            ImmutableMap.of("field_1", "value 1", "field_2", "value 2")
        );

        Map<String, Object> exceptionRecordData = ImmutableMap.of(
            "someProperty", "someValue",
            "scanOCRData", rawOcrData
        );

        ResultOrErrors<OcrData> result = ocrDataParser.parseOcrData(exceptionRecordData);

        assertTrue(result.isSuccessful());

        OcrData parsedData = result.get();
        assertThat(parsedData).isNotNull();

        assertThat(parsedData.fields).isEqualToComparingFieldByFieldRecursively(
            Arrays.asList(
                new CcdCollectionElement<>(new OcrDataField("field_1", "value 1")),
                new CcdCollectionElement<>(new OcrDataField("field_2", "value 2"))
            )
        );
    }

    @Test
    public void should_return_error_when_format_is_invalid() {
        List<Object> invalidOcrFieldList = Arrays.asList("field1", "field2");

        Map<String, Object> exceptionRecordData = ImmutableMap.of(
            "scanOCRData", invalidOcrFieldList
        );

        ResultOrErrors<OcrData> result = ocrDataParser.parseOcrData(exceptionRecordData);

        assertFalse(result.isSuccessful());
        assertThat(result.errors).isEqualTo(Arrays.asList("Form OCR data has invalid format"));
    }

    @Test
    public void should_return_error_when_null() {
        Map<String, Object> exceptionRecordData = new HashMap<>();
        exceptionRecordData.put("scanOCRData", null);

        ResultOrErrors<OcrData> result = ocrDataParser.parseOcrData(exceptionRecordData);

        assertFalse(result.isSuccessful());
        assertThat(result.errors).isEqualTo(Arrays.asList("Form OCR data is missing"));
    }

    @Test
    public void should_return_error_when_absent() {
        Map<String, Object> exceptionRecordData = ImmutableMap.of("notOcrData", "some value");

        ResultOrErrors<OcrData> result = ocrDataParser.parseOcrData(exceptionRecordData);

        assertFalse(result.isSuccessful());
        assertThat(result.errors).isEqualTo(Arrays.asList("Form OCR data is missing"));
    }

    private List<Object> createRawOcrData(Map<String, String> fields) {
        return fields
            .entrySet()
            .stream()
            .map(
                entry -> {
                    Map<String, Object> keyValue = ImmutableMap.of(
                        "key", entry.getKey(),
                        "value", entry.getValue()
                    );

                    return ImmutableMap.of("value", keyValue);
                }
            )
            .collect(toList());
    }
}
