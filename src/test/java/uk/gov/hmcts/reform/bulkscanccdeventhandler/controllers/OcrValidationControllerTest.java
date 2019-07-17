package uk.gov.hmcts.reform.bulkscanccdeventhandler.controllers;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.model.FormType;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.model.out.OcrValidationResult;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.services.OcrDataValidator;

import java.io.IOException;

import static java.util.Collections.emptyList;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.model.out.ValidationStatus.SUCCESS;

@WebMvcTest(OcrValidationController.class)
class OcrValidationControllerTest {

    @Autowired
    private transient MockMvc mockMvc;

    @MockBean
    private OcrDataValidator ocrDataValidator;

    @Test
    void should_return_success_message_when_ocr_data_is_valid() throws Exception {
        String requestBody = readResource("ocr-data/valid/valid-ocr-data.json");

        given(ocrDataValidator.validate(eq(FormType.PERSONAL), any()))
            .willReturn(new OcrValidationResult(emptyList(), emptyList(), SUCCESS));

        mockMvc
            .perform(
                post("/validate-ocr-data")
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header("ServiceAuthorization", "testServiceAuthHeader")
                    .content(requestBody)
            )
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().json(readResource("ocr-data/response/valid-ocr-response.json")));
    }

    @Test
    void should_return_bad_request_when_form_type_is_missing() throws Exception {
        mockMvc
            .perform(
                post("/validate-ocr-data")
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header("ServiceAuthorization", "testServiceAuthHeader")
                    .content(readResource("ocr-data/invalid/missing-form-type.json"))
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
                    .content(readResource("ocr-data/invalid/missing-ocr-fields.json"))
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
                    .content(readResource("ocr-data/invalid/empty-ocr-fields.json"))
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
                    .content(readResource("ocr-data/invalid/invalid-form-type.json"))
            )
            .andExpect(status().isBadRequest());
    }

    private String readResource(final String fileName) throws IOException {
        return Resources.toString(Resources.getResource(fileName), Charsets.UTF_8);
    }
}
