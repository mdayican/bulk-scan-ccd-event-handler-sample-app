package uk.gov.hmcts.reform.bulkscanccdeventhandler.controllers;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OcrValidationController.class)
public class OcrValidationControllerTest {

    @Autowired
    private transient MockMvc mockMvc;

    @Test
    void should_return_success_message_when_ocr_data_is_valid() throws Exception {
        String requestBody = readResource("ocr-data/valid-ocr-data.json");

        mockMvc
            .perform(
                post("/validate-ocr-data")
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header("ServiceAuthorization", "testServiceAuthHeader")
                    .content(requestBody)
            )
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().json(readResource("ocr-data/valid-ocr-response.json")));
    }

    @Test
    void should_return_bad_request_when_form_type_is_missing() throws Exception {
        mockMvc
            .perform(
                post("/validate-ocr-data")
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header("ServiceAuthorization", "testServiceAuthHeader")
                    .content(readResource("ocr-data/ocr-data-without-form-type.json"))
            )
            .andExpect(status().isBadRequest());
    }

    @Test
    void should_return_bad_request_with_ocr_fields_are_missing() throws Exception {
        mockMvc
            .perform(
                post("/validate-ocr-data")
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header("ServiceAuthorization", "testServiceAuthHeader")
                    .content(readResource("ocr-data/ocr-data-without-ocr-fields.json"))
            )
            .andExpect(status().isBadRequest());
    }

    @Test
    void should_return_bad_request_when_ocr_fields_list_is_empty() throws Exception {
        mockMvc
            .perform(
                post("/validate-ocr-data")
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header("ServiceAuthorization", "testServiceAuthHeader")
                    .content(readResource("ocr-data/ocr-data-empty-fields.json"))
            )
            .andExpect(status().isBadRequest());
    }

    @Test
    void should_return_bad_request_when_form_type_is_invalid() throws Exception {
        mockMvc
            .perform(
                post("/validate-ocr-data")
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header("ServiceAuthorization", "testServiceAuthHeader")
                    .content(readResource("ocr-data/ocr-data-invalid-form-type.json"))
            )
            .andExpect(status().isBadRequest());
    }

    private String readResource(final String fileName) throws IOException {
        return Resources.toString(Resources.getResource(fileName), Charsets.UTF_8);
    }
}
