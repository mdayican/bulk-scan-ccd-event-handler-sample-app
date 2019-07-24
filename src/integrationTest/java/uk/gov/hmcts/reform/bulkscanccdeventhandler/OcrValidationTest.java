package uk.gov.hmcts.reform.bulkscanccdeventhandler;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@ComponentScan(basePackages = "...", lazyInit = true)
@ContextConfiguration
@SpringBootTest
@TestPropertySource("classpath:application.conf")
class OcrValidationTest {

    @Autowired
    private MockMvc mvc;

    @Test
    void should_return_200_when_ocr_form_validation_request_data_is_valid() throws Throwable {
        String content = readResource("ocr-data/valid/valid-ocr-data.json");

        mvc.perform(
            post("/validate-ocr-data")
                .header("ServiceAuthorization", "auth-header-value")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("SUCCESS"))
            .andExpect(jsonPath("$.warnings", hasSize(0)))
            .andExpect(jsonPath("$.errors", hasSize(0)));
    }

    @Test
    void should_return_errors_when_ocr_data_contains_duplicate_fields() throws Throwable {
        String content = readResource("ocr-data/invalid/duplicate-fields.json");

        mvc.perform(
            post("/validate-ocr-data")
                .header("ServiceAuthorization", "auth-header-value")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("ERRORS"))
            .andExpect(jsonPath("$.warnings", hasSize(0)))
            .andExpect(jsonPath("$.errors", hasSize(1)))
            .andExpect(jsonPath("$.errors[0]").value("Invalid OCR data. Duplicate fields exist: last_name"));
    }

    @Test
    void should_return_errors_when_ocr_form_data_has_missing_mandatory_fields() throws Throwable {
        String content = readResource("ocr-data/invalid/missing-mandatory-fields.json");

        mvc.perform(
            post("/validate-ocr-data")
                .header("ServiceAuthorization", "auth-header-value")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("ERRORS"))
            .andExpect(jsonPath("$.errors", hasSize(1)))
            .andExpect(jsonPath("$.warnings", hasSize(0)))
            .andExpect(jsonPath("$.errors[0]").value("last_name is missing"));
    }

    @Test
    void should_return_warnings_when_ocr_form_data_has_missing_optional_fields() throws Throwable {
        String content = readResource("ocr-data/invalid/missing-optional-fields.json");

        mvc.perform(
            post("/validate-ocr-data")
                .header("ServiceAuthorization", "auth-header-value")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("WARNINGS"))
            .andExpect(jsonPath("$.errors", hasSize(0)))
            .andExpect(jsonPath("$.warnings", hasSize(1)))
            .andExpect(jsonPath("$.warnings[0]").value("date_of_birth is missing"));
    }

    @Test
    void should_return_401_when_service_auth_header_is_missing() throws Throwable {
        String content = readResource("ocr-data/invalid/missing-optional-fields.json");

        mvc.perform(
            post("/validate-ocr-data")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content))
            .andExpect(status().isUnauthorized());
    }

    private String readResource(final String fileName) throws IOException {
        return Resources.toString(Resources.getResource(fileName), Charsets.UTF_8);
    }
}