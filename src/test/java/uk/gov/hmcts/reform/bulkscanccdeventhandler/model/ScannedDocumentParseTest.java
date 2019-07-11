package uk.gov.hmcts.reform.bulkscanccdeventhandler.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.common.collect.ImmutableMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.services.CcdCollectionParser;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static java.util.Collections.singletonMap;
import static org.assertj.core.api.Assertions.assertThat;

public class ScannedDocumentParseTest {

    private static final Object RAW_DOCUMENTS =
        Arrays.asList(
            singletonMap(
                "value",
                ImmutableMap.builder()
                    .put("type", "type1")
                    .put("subtype", "subtype1")
                    .put("url", "http://example.com/doc1")
                    .put("controlNumber", "dcn1")
                    .put("fileName", "filename1")
                    .put("scannedDate", "2019-05-01T09:45:01.000")
                    .put("deliveryDate", "2019-05-03T11:45:01.000")
                    .put("exceptionRecordReference", "exceptionRef1")
                    .build()
            ),
            singletonMap(
                "value",
                ImmutableMap.builder()
                    .put("type", "type2")
                    .put("subtype", "subtype2")
                    .put("url", "http://example.com/doc2")
                    .put("controlNumber", "dcn2")
                    .put("fileName", "filename2")
                    .put("scannedDate", "2019-06-01T19:45:01.000")
                    .put("deliveryDate", "2019-06-03T21:45:01.000")
                    .put("exceptionRecordReference", "exceptionRef2")
                    .build()
            )
        );

    private static final List<CcdCollectionElement<ScannedDocument>> EXPECTED_PARSED_DOCUMENTS =
        Arrays.asList(
            new CcdCollectionElement<>(
                new ScannedDocument(
                    "type1",
                    "subtype1",
                    "http://example.com/doc1",
                    "dcn1",
                    "filename1",
                    LocalDateTime.parse("2019-05-01T09:45:01.000"),
                    LocalDateTime.parse("2019-05-03T11:45:01.000"),
                    "exceptionRef1"
                )
            ),
            new CcdCollectionElement<>(
                new ScannedDocument(
                    "type2",
                    "subtype2",
                    "http://example.com/doc2",
                    "dcn2",
                    "filename2",
                    LocalDateTime.parse("2019-06-01T19:45:01.000"),
                    LocalDateTime.parse("2019-06-03T21:45:01.000"),
                    "exceptionRef2"
                )
            )
        );


    private final ObjectMapper mapper = new ObjectMapper();

    private CcdCollectionParser ccdCollectionParser;

    @BeforeEach
    public void setUp() {
        mapper.registerModule(new JavaTimeModule());

        ccdCollectionParser = new CcdCollectionParser(mapper);
    }

    @Test
    public void should_be_able_to_parse_valid_raw_scanned_documents_structure() {
        List<CcdCollectionElement<ScannedDocument>> result =
            ccdCollectionParser.parseCcdCollection(RAW_DOCUMENTS, ScannedDocument.class);

        assertThat(result)
            .usingRecursiveFieldByFieldElementComparator()
            .isEqualTo(EXPECTED_PARSED_DOCUMENTS);
    }
}
