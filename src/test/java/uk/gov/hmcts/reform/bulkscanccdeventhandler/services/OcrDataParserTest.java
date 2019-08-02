package uk.gov.hmcts.reform.bulkscanccdeventhandler.services;

import com.google.common.collect.ImmutableMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.model.CcdCollectionElement;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.model.OcrData;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.model.ResultOrErrors;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.ocrvalidation.model.in.OcrDataField;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.services.exception.CallbackProcessingException;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.services.exception.CcdDataParseException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
public class OcrDataParserTest {

    @Mock
    private CcdCollectionParser ccdCollectionParser;

    private OcrDataParser ocrDataParser;

    @BeforeEach
    public void setUp() {
        this.ocrDataParser = new OcrDataParser(ccdCollectionParser);
    }

    @Test
    public void should_return_ocr_data_when_valid() {
        // given
        List<CcdCollectionElement<OcrDataField>> expectedOcrFields = sampleOcrFieldList();
        given(ccdCollectionParser.parseCcdCollection(any(), eq(OcrDataField.class)))
            .willReturn(expectedOcrFields);

        Object rawOcrData = new Object();

        Map<String, Object> exceptionRecordData = ImmutableMap.of(
            "someProperty", "someValue",
            "scanOCRData", rawOcrData
        );

        // when
        ResultOrErrors<OcrData> result = ocrDataParser.parseOcrData(exceptionRecordData);

        // then
        assertTrue(result.isSuccessful());

        OcrData parsedData = result.get();
        assertThat(parsedData.fields).isSameAs(expectedOcrFields);

        verify(ccdCollectionParser).parseCcdCollection(rawOcrData, OcrDataField.class);
    }

    @Test
    public void should_throw_exception_when_format_is_invalid() {
        // given
        CcdDataParseException exceptionToThrow = new CcdDataParseException("Test exception", null);
        willThrow(exceptionToThrow).given(ccdCollectionParser).parseCcdCollection(any(), any());

        Map<String, Object> exceptionRecordData = ImmutableMap.of("scanOCRData", new Object());

        assertThatThrownBy(() ->
                               ocrDataParser.parseOcrData(exceptionRecordData)
        )
            .isInstanceOf(CallbackProcessingException.class)
            .hasMessage("Failed to parse OCR data from exception record");
    }

    @Test
    public void should_return_error_when_null() {
        // given
        Map<String, Object> exceptionRecordData = new HashMap<>();
        exceptionRecordData.put("scanOCRData", null);

        // when
        ResultOrErrors<OcrData> result = ocrDataParser.parseOcrData(exceptionRecordData);

        // then
        assertFalse(result.isSuccessful());
        assertThat(result.errors).isEqualTo(Arrays.asList("Form OCR data is missing"));
        verifyNoMoreInteractions(ccdCollectionParser);
    }

    @Test
    public void should_return_error_when_absent() {
        // given
        Map<String, Object> exceptionRecordData = ImmutableMap.of("notOcrData", "some value");

        // when
        ResultOrErrors<OcrData> result = ocrDataParser.parseOcrData(exceptionRecordData);

        // then
        assertFalse(result.isSuccessful());
        assertThat(result.errors).isEqualTo(Arrays.asList("Form OCR data is missing"));
        verifyNoMoreInteractions(ccdCollectionParser);
    }

    private List<CcdCollectionElement<OcrDataField>> sampleOcrFieldList() {
        return createCcdElementList(
            Arrays.asList(
                new OcrDataField("field_1", "value 1"),
                new OcrDataField("field_2", "value 2")
            )
        );
    }

    private List<CcdCollectionElement<OcrDataField>> createCcdElementList(List<OcrDataField> ocrFields) {
        return ocrFields
            .stream()
            .map(field -> new CcdCollectionElement<>(field))
            .collect(toList());
    }
}
