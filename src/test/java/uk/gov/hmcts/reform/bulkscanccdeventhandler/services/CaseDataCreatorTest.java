package uk.gov.hmcts.reform.bulkscanccdeventhandler.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.model.BulkScanCase;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.model.OcrData;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.model.ResultOrErrors;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.services.exception.CallbackProcessingException;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.model.ResultOrErrors.errors;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.model.ResultOrErrors.result;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.services.data.SampleCaseConversionData.PARSED_OCR_DATA;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.services.data.SampleCaseConversionData.PARSED_SCANNED_DOCUMENTS;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.services.data.SampleCaseConversionData.RAW_EXCEPTION_RECORD_DATA;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.services.data.SampleCaseConversionData.TRANSFORMED_CCD_CASE;

@ExtendWith(MockitoExtension.class)
public class CaseDataCreatorTest {

    private static final List<String> ALL_OCR_FIELD_NAMES = Arrays.asList(
        "legacy_id",
        "first_name",
        "last_name",
        "date_of_birth",
        "contact_number",
        "email",
        "address_line_1",
        "address_line_2",
        "address_line_3",
        "post_code",
        "post_town",
        "county",
        "country"
    );

    @Mock
    private OcrDataParser ocrDataParser;

    @Mock
    private ScannedDocumentsParser scannedDocumentsParser;

    @Mock
    private OcrFieldRetriever ocrFieldRetriever;

    @Mock
    private Function<OcrData, OcrFieldRetriever> ocrFieldRetrieverSupplier;

    private final ObjectMapper mapper = new ObjectMapper();

    private CaseDataCreator caseDataCreator;

    @BeforeEach
    public void setUp() {
        mapper.registerModule(new JavaTimeModule());

        this.caseDataCreator = new CaseDataCreator(
            ocrDataParser,
            scannedDocumentsParser,
            ocrFieldRetrieverSupplier
        );
    }

    @Test
    public void should_return_case_data_when_exception_record_data_is_valid() {
        // given
        setupSuccessfulOcrDataParsing();
        setupSuccessfulScannedDocumentParsing();
        setupSuccessfulOcrFieldRetrieval();

        Map<String, Object> inputData = RAW_EXCEPTION_RECORD_DATA;

        // when
        ResultOrErrors<BulkScanCase> result = caseDataCreator.createCaseData(inputData);

        // then
        assertThat(result.isSuccessful()).isTrue();
        assertThat(result.get())
            .isEqualToComparingFieldByFieldRecursively(TRANSFORMED_CCD_CASE);

        verify(ocrDataParser).parseOcrData(inputData);
        verify(scannedDocumentsParser).parseScannedDocuments(inputData);

        verify(ocrFieldRetriever).getFieldValue("legacy_id", false, String.class);
        verify(ocrFieldRetriever).getFieldValue("first_name", true, String.class);
        verify(ocrFieldRetriever).getFieldValue("last_name", true, String.class);
        verify(ocrFieldRetriever).getFieldValue("date_of_birth", true, LocalDate.class);
        verify(ocrFieldRetriever).getFieldValue("contact_number", true, String.class);
        verify(ocrFieldRetriever).getFieldValue("email", true, String.class);
        verify(ocrFieldRetriever).getFieldValue("address_line_1", true, String.class);
        verify(ocrFieldRetriever).getFieldValue("address_line_2", false, String.class);
        verify(ocrFieldRetriever).getFieldValue("address_line_3", false, String.class);
        verify(ocrFieldRetriever).getFieldValue("post_code", true, String.class);
        verify(ocrFieldRetriever).getFieldValue("post_town", true, String.class);
        verify(ocrFieldRetriever).getFieldValue("county", true, String.class);
        verify(ocrFieldRetriever).getFieldValue("country", true, String.class);
    }

    @Test
    public void should_return_errors_when_ocr_data_parser_returns_errors() {
        Exception exceptionToThrow = new CallbackProcessingException("test exception", null);
        willThrow(exceptionToThrow).given(ocrDataParser).parseOcrData(any());

        assertThatThrownBy(
            () -> caseDataCreator.createCaseData(RAW_EXCEPTION_RECORD_DATA)
        )
            .isSameAs(exceptionToThrow);
    }

    @Test
    public void should_return_errors_when_documents_parser_returns_errors() {
        setupSuccessfulOcrDataParsing();
        setupSuccessfulOcrFieldRetrieval();

        Exception exceptionToThrow = new CallbackProcessingException("test exception", null);
        willThrow(exceptionToThrow).given(scannedDocumentsParser).parseScannedDocuments(any());

        assertThatThrownBy(() -> caseDataCreator.createCaseData(RAW_EXCEPTION_RECORD_DATA))
            .isSameAs(exceptionToThrow);
    }

    @Test
    public void should_return_errors_for_each_ocr_field_validation_failure() {
        // given
        setupSuccessfulOcrDataParsing();
        setupSuccessfulScannedDocumentParsing();

        // make every field invalid
        setupFailingOcrFieldRetrieval(field -> String.format("Field %s is invalid", field));

        // when
        ResultOrErrors<BulkScanCase> result = caseDataCreator.createCaseData(RAW_EXCEPTION_RECORD_DATA);

        // then
        assertThat(result.isSuccessful()).isFalse();

        List<String> expectedErrors =
            ALL_OCR_FIELD_NAMES
                .stream()
                .map(field -> String.format("Field %s is invalid", field))
                .collect(toList());

        assertThat(result.errors).containsExactlyInAnyOrderElementsOf(expectedErrors);
    }

    private void setupSuccessfulOcrDataParsing() {
        given(ocrDataParser.parseOcrData(any())).willReturn(result(PARSED_OCR_DATA));
    }

    private void setupSuccessfulScannedDocumentParsing() {
        given(scannedDocumentsParser.parseScannedDocuments(any()))
            .willReturn(result(PARSED_SCANNED_DOCUMENTS));
    }

    private void setupSuccessfulOcrFieldRetrieval() {
        ocrFieldRetriever = spy(new OcrFieldRetriever(PARSED_OCR_DATA, mapper));
        given(ocrFieldRetrieverSupplier.apply(any())).willReturn(ocrFieldRetriever);
    }

    private void setupFailingOcrFieldRetrieval(Function<String, String> fieldToErrorFunction) {
        ocrFieldRetriever = mock(OcrFieldRetriever.class);
        given(ocrFieldRetriever.getFieldValue(any(), anyBoolean(), any())).willAnswer(
            invocation -> errors(
                fieldToErrorFunction.apply(invocation.getArgument(0).toString())
            )
        );

        given(ocrFieldRetrieverSupplier.apply(any())).willReturn(ocrFieldRetriever);
    }
}
