package uk.gov.hmcts.reform.bulkscanccdeventhandler;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import io.restassured.RestAssured;
import io.restassured.mapper.ObjectMapperType;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.ocrvalidation.model.out.OcrValidationResponse;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.ocrvalidation.model.out.ValidationStatus;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;

@ExtendWith(SpringExtension.class)
@TestPropertySource("classpath:application.conf")
public class OcrFormValidationTest {

    private static final String FORM_TYPE_PERSONAL = "PERSONAL";

    private String testUrl;

    private String s2sUrl;

    private String s2sName;

    private String s2sSecret;

    private final TestHelper testHelper = new TestHelper();

    @BeforeEach
    public void setUp() {
        Config config = ConfigFactory.load();
        this.testUrl = config.getString("test-url");
        this.s2sUrl = config.getString("test-s2s-url");
        this.s2sName = config.getString("test-s2s-name");
        this.s2sSecret = config.getString("test-s2s-secret");
    }

    @Test
    public void should_validate_ocr_data_and_return_success() {
        Response response = sendOcrFormValidationRequest(FORM_TYPE_PERSONAL, "valid-ocr-form-data.json");

        assertThat(response.getStatusCode()).isEqualTo(200);

        OcrValidationResponse validationResponse = response.getBody()
            .as(OcrValidationResponse.class, ObjectMapperType.JACKSON_2);

        assertThat(validationResponse.status).isEqualTo(ValidationStatus.SUCCESS);
        assertThat(validationResponse.errors).isEmpty();
        assertThat(validationResponse.warnings).isEmpty();
    }

    @Test
    public void should_return_errors_when_mandatory_fields_are_missing() {
        Response response = sendOcrFormValidationRequest(FORM_TYPE_PERSONAL, "missing-mandatory-fields.json");

        assertThat(response.getStatusCode()).isEqualTo(200);

        OcrValidationResponse validationResponse = response.getBody()
            .as(OcrValidationResponse.class, ObjectMapperType.JACKSON_2);

        assertThat(validationResponse.status).isEqualTo(ValidationStatus.ERRORS);
        assertThat(validationResponse.errors).containsExactly("last_name is missing");
        assertThat(validationResponse.warnings).isEmpty();
    }

    @Test
    public void should_return_warnings_when_optional_fields_are_missing() {
        Response response = sendOcrFormValidationRequest(FORM_TYPE_PERSONAL, "missing-optional-fields.json");

        assertThat(response.getStatusCode()).isEqualTo(200);

        OcrValidationResponse validationResponse = response.getBody()
            .as(OcrValidationResponse.class, ObjectMapperType.JACKSON_2);

        assertThat(validationResponse.status).isEqualTo(ValidationStatus.WARNINGS);
        assertThat(validationResponse.warnings).containsExactly("post_town is missing", "county is missing");
        assertThat(validationResponse.errors).isEmpty();
    }

    @Test
    public void should_return_errors_when_additional_validations_are_failing() {
        Response response = sendOcrFormValidationRequest(FORM_TYPE_PERSONAL, "invalid-form-data.json");

        assertThat(response.getStatusCode()).isEqualTo(200);

        OcrValidationResponse validationResponse = response.getBody()
            .as(OcrValidationResponse.class, ObjectMapperType.JACKSON_2);

        assertThat(validationResponse.status).isEqualTo(ValidationStatus.ERRORS);
        assertThat(validationResponse.errors).containsExactly("Invalid email address", "Invalid phone number");
        assertThat(validationResponse.warnings).isEmpty();
    }

    private Response sendOcrFormValidationRequest(String formType, String fileName) {
        return RestAssured
            .given()
            .relaxedHTTPSValidation()
            .header("ServiceAuthorization", "Bearer " + testHelper.s2sSignIn(s2sName, s2sSecret, s2sUrl))
            .header(CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .baseUri(testUrl)
            .body(testHelper.fileContentAsBytes(fileName))
            .when()
            .post("/forms/" + formType + "/validate-ocr")
            .andReturn();
    }
}
