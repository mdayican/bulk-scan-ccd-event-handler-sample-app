package uk.gov.hmcts.reform.bulkscanccdeventhandler;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.transformation.services.ExceptionRecordToCaseTransformer;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;

@ExtendWith(SpringExtension.class)
@TestPropertySource("classpath:application.conf")
public class TransformationTest {

    private final TestHelper testHelper = new TestHelper();

    @SuppressWarnings("unchecked")
    @Test
    public void should_transform_exception_record_successfully_without_any_warnings() {
        Response response = testHelper.postWithBody(
            "/transform-exception-record",
            TestHelper.fileContentAsBytes("exception-records/valid.json")
        );

        assertThat(response.getStatusCode()).isEqualTo(OK.value());

        JsonPath transformationResponse = response.getBody().jsonPath();

        assertSoftly(softly -> {
            softly.assertThat(transformationResponse.getList("warnings")).isEmpty();
            softly.assertThat(transformationResponse.getMap("case_creation_details").get("case_type_id"))
                .isEqualTo(ExceptionRecordToCaseTransformer.CASE_TYPE_ID);
            softly.assertThat(transformationResponse.getMap("case_creation_details").get("event_id"))
                .isEqualTo(ExceptionRecordToCaseTransformer.EVENT_ID);

            Map<String, Object> caseData = (Map<String, Object>) transformationResponse
                .getMap("case_creation_details")
                .get("case_data");

            softly.assertThat(caseData.get("email")).isEqualTo("non-empty-email");

            softly.assertAll();
        });
    }

    @Test
    public void should_transform_exception_record_successfully_with_warning() {
        Response response = testHelper.postWithBody(
            "/transform-exception-record",
            TestHelper.fileContentAsBytes("exception-records/valid-missing-email.json")
        );

        assertThat(response.getStatusCode()).isEqualTo(OK.value());

        JsonPath transformationResponse = response.getBody().jsonPath();

        assertThat(transformationResponse.getList("warnings"))
            .hasSize(1)
            .containsOnly("'email' is empty");

        assertThat(transformationResponse.getMap("case_creation_details").get("email")).isNull();
    }

    @Test
    public void should_not_transform_exception_record_and_respond_with_422() {
        Response response = testHelper.postWithBody(
            "/transform-exception-record",
            TestHelper.fileContentAsBytes("exception-records/invalid-missing-last-name.json")
        );

        assertThat(response.getStatusCode()).isEqualTo(UNPROCESSABLE_ENTITY.value());

        JsonPath errorResponse = response.getBody().jsonPath();

        assertThat(errorResponse.getList("errors"))
            .hasSize(1)
            .containsOnly("'last_name' is required");
        assertThat(errorResponse.getList("warnings")).isEmpty();
        assertThat(errorResponse.getMap("")).containsOnlyKeys("errors", "warnings");
    }
}
