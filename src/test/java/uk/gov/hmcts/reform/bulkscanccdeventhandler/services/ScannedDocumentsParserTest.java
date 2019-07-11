package uk.gov.hmcts.reform.bulkscanccdeventhandler.services;

import com.google.common.collect.ImmutableMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.model.CcdCollectionElement;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.model.ResultOrErrors;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.model.ScannedDocument;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.services.exception.CallbackProcessingException;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.services.exception.CcdDataParseException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
public class ScannedDocumentsParserTest {

    @Mock
    private CcdCollectionParser ccdCollectionParser;

    private ScannedDocumentsParser scannedDocumentsParser;

    @BeforeEach
    public void setUp() {
        this.scannedDocumentsParser = new ScannedDocumentsParser(ccdCollectionParser);
    }

    @Test
    public void should_return_scanned_documents_when_valid() {
        // given
        List<CcdCollectionElement<ScannedDocument>> expectedResult =
            Arrays.asList(
                new CcdCollectionElement<>(
                    mock(ScannedDocument.class)
                )
            );

        given(ccdCollectionParser.parseCcdCollection(any(), eq(ScannedDocument.class)))
            .willReturn(expectedResult);

        Object rawData = new Object();

        Map<String, Object> exceptionRecordData = ImmutableMap.of(
            "someProperty", "someValue",
            "scannedDocuments", rawData
        );

        // when
        ResultOrErrors<List<CcdCollectionElement<ScannedDocument>>> result =
            scannedDocumentsParser.parseScannedDocuments(exceptionRecordData);

        // then
        assertThat(result.isSuccessful()).isTrue();
        assertThat(result.get()).isSameAs(expectedResult);

        verify(ccdCollectionParser).parseCcdCollection(rawData, ScannedDocument.class);
    }

    @Test
    public void should_throw_exception_when_format_is_invalid() {
        // given
        CcdDataParseException exceptionToThrow = new CcdDataParseException("Test exception", null);
        willThrow(exceptionToThrow).given(ccdCollectionParser).parseCcdCollection(any(), any());

        Map<String, Object> exceptionRecordData = ImmutableMap.of("scannedDocuments", new Object());

        assertThatThrownBy(() ->
            scannedDocumentsParser.parseScannedDocuments(exceptionRecordData)
        )
            .isInstanceOf(CallbackProcessingException.class)
            .hasMessage("Failed to parse scanned documents from exception record");
    }

    @Test
    public void should_return_empty_list_when_scanned_documents_field_is_null() {
        // given
        Map<String, Object> exceptionRecordData = new HashMap<>();
        exceptionRecordData.put("scannedDocuments", null);

        // when
        ResultOrErrors<List<CcdCollectionElement<ScannedDocument>>> result =
            scannedDocumentsParser.parseScannedDocuments(exceptionRecordData);

        // then
        assertThat(result.isSuccessful()).isTrue();
        assertThat(result.get()).isEqualTo(emptyList());
        verifyNoMoreInteractions(ccdCollectionParser);
    }

    @Test
    public void should_return_empty_list_when_scanned_documents_field_is_absent() {
        // given
        Map<String, Object> exceptionRecordData = ImmutableMap.of("notScannedDocuments", "some value");

        // when
        ResultOrErrors<List<CcdCollectionElement<ScannedDocument>>> result =
            scannedDocumentsParser.parseScannedDocuments(exceptionRecordData);

        // then
        assertThat(result.isSuccessful()).isTrue();
        assertThat(result.get()).isEqualTo(emptyList());
        verifyNoMoreInteractions(ccdCollectionParser);
    }
}
