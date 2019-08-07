package uk.gov.hmcts.reform.bulkscanccdeventhandler.transformation;

import org.junit.jupiter.api.Test;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.ocrvalidation.model.in.OcrDataField;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.transformation.model.in.ExceptionRecord;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.transformation.model.in.JourneyClassification;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.transformation.model.in.ScannedDocument;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.transformation.model.out.CaseCreationDetails;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.transformation.model.out.SuccessfulTransformationResponse;

import java.time.LocalDateTime;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * TODO: REMOVE
 * This test only exists to add coverage to not yet used models.
 * Once models are used this class should be deleted.
 */
public class ModelTest {

    @Test
    public void requestModelTest() {
        ExceptionRecord exceptionRecord =
            new ExceptionRecord(
                "er-id",
                "caseType",
                "poBox",
                "jurisdiction",
                JourneyClassification.EXCEPTION,
                LocalDateTime.now(),
                LocalDateTime.now(),
                singletonList(
                    new ScannedDocument(
                        "type",
                        "subtype",
                        "http://localhost/123",
                        "dcn",
                        "hello.pdf",
                        LocalDateTime.now(),
                        LocalDateTime.now()
                    )
                ),
                asList(
                    new OcrDataField("key1", "value1"),
                    new OcrDataField("key2", "value2")
                )
            );

        assertThat(exceptionRecord).isNotNull();
    }

    @Test
    public void response_model_test() {
        SuccessfulTransformationResponse resp =
            new SuccessfulTransformationResponse(
                new CaseCreationDetails(
                    "caseTypeId",
                    "eventId",
                    null
                ),
                emptyList()
            );
        assertThat(resp).isNotNull();
    }
}
