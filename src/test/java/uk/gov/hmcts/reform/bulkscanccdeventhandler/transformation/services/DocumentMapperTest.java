package uk.gov.hmcts.reform.bulkscanccdeventhandler.transformation.services;

import org.junit.jupiter.api.Test;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.transformation.model.in.InputScannedDoc;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.transformation.model.out.CcdCollectionElement;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.transformation.model.out.ScannedDocument;

import static java.time.LocalDateTime.now;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

public class DocumentMapperTest {

    private final DocumentMapper mapper = new DocumentMapper();

    @Test
    public void should_map_models() {
        // given
        InputScannedDoc input = new InputScannedDoc("type", "subtype", "url", "dcn", "hello.pdf", now(), now());
        String refId = "ref-id";

        // when
        CcdCollectionElement<ScannedDocument> output = mapper.toCaseDoc(input, refId);

        // then
        assertSoftly(softly -> {
            softly.assertThat(output.value.type).isEqualTo(input.type);
            softly.assertThat(output.value.subtype).isEqualTo(input.subtype);
            softly.assertThat(output.value.url).isEqualTo(input.url);
            softly.assertThat(output.value.controlNumber).isEqualTo(input.controlNumber);
            softly.assertThat(output.value.fileName).isEqualTo(input.fileName);
            softly.assertThat(output.value.deliveryDate).isEqualTo(input.deliveryDate);
            softly.assertThat(output.value.scannedDate).isEqualTo(input.scannedDate);
            softly.assertThat(output.value.exceptionRecordReference).isEqualTo(refId);
        });
    }

    @Test
    public void should_map_null_to_null() {
        // given
        InputScannedDoc input = null;
        String refId = "ref-id";

        // when
        CcdCollectionElement<ScannedDocument> output = mapper.toCaseDoc(input, refId);

        // then
        assertThat(output).isNull();
    }
}
