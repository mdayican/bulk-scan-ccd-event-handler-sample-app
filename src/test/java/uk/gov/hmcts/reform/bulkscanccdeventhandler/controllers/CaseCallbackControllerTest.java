package uk.gov.hmcts.reform.bulkscanccdeventhandler.controllers;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.Resources;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.handler.model.CaseCreationResult;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.services.CaseCreationCallbackHandler;
import uk.gov.hmcts.reform.ccd.client.model.CaseDetails;
import uk.gov.hmcts.reform.ccd.client.model.Classification;

import java.io.IOException;
import java.time.LocalDateTime;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CaseCallbackController.class)
public class CaseCallbackControllerTest {

    private static final String CALLBACK_PATH = "/callback/new-application";

    @Autowired
    private transient MockMvc mockMvc;

    @MockBean
    private CaseCreationCallbackHandler callbackHandler;

    @Test
    public void should_return_result_from_callback_handler_when_request_is_well_formed() throws Exception {
        CaseCreationResult expectedResult = new CaseCreationResult(
            null,
            ImmutableList.of("error1"),
            ImmutableList.of("warning1")
        );

        given(callbackHandler.handleCaseCreationCallback(any(), any(), any(), any(), anyBoolean()))
            .willReturn(expectedResult);

        String expectedResponseBody =
            "{"
                + "\"data\":null,"
                + "\"data_classification\":null,"
                + "\"security_classification\":null,"
                + "\"errors\":[\"error1\"],"
                + "\"warnings\":[\"warning1\"]"
                + "}";

        mockMvc.perform(
            post(CALLBACK_PATH)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("Authorization", "token1")
                .header("user-id", "userId1")
                .content(wellFormedRequest())
        )
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().json(expectedResponseBody));

        verifyHandlerCalledWith(caseDetailsFromWellFormedRequest(), "createNewCase", "token1", "userId1", true);
    }

    @Test
    public void should_return_400_response_when_request_is_malformed() throws Exception {
        mockMvc
            .perform(
                post(CALLBACK_PATH)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header("Authorization", "testAuthHeader")
                    .header("user-id", "userId123")
                    .content(malformedRequest())
            )
            .andExpect(status().is(400));

        verifyNoMoreInteractions(callbackHandler);
    }

    @Test
    public void should_return_400_response_when_request_body_is_missing() throws Exception {
        mockMvc
            .perform(
                post(CALLBACK_PATH)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header("Authorization", "testAuthHeader")
                    .header("user-id", "userId123")
            )
            .andExpect(status().is(400));

        verifyNoMoreInteractions(callbackHandler);
    }

    @Test
    public void should_return_400_response_when_authorization_header_is_missing() throws Exception {
        mockMvc
            .perform(
                post(CALLBACK_PATH)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header("user-id", "userId123")
                    .content(wellFormedRequest())
            )
            .andExpect(status().is(400));

        verifyNoMoreInteractions(callbackHandler);
    }

    @Test
    public void should_return_400_response_when_user_id_header_is_missing() throws Exception {
        mockMvc
            .perform(
                post(CALLBACK_PATH)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header("Authorization", "testAuthHeader")
                    .content(wellFormedRequest())
            )
            .andExpect(status().is(400));

        verifyNoMoreInteractions(callbackHandler);
    }

    public void verifyHandlerCalledWith(
        CaseDetails caseDetails,
        String eventId,
        String idamToken,
        String idamUserId,
        boolean ignoreWarnings
    ) {
        ArgumentCaptor<CaseDetails> caseDetailsCaptor = ArgumentCaptor.forClass(CaseDetails.class);

        verify(callbackHandler).handleCaseCreationCallback(
            caseDetailsCaptor.capture(),
            eq(eventId),
            eq(idamToken),
            eq(idamUserId),
            eq(ignoreWarnings)
        );

        assertThat(caseDetailsCaptor.getValue())
            .isEqualToComparingFieldByFieldRecursively(caseDetails);
    }

    private String wellFormedRequest() throws IOException {
        return readResource("callback/request/well-formed.json");
    }

    private String malformedRequest() throws IOException {
        return readResource("callback/request/missing-case-details.json");
    }

    private String readResource(String fileName) throws IOException {
        return Resources.toString(Resources.getResource(fileName), Charsets.UTF_8);
    }

    /**
     * Case details that correspond to what's inside well-formed.json file.
     */
    private CaseDetails caseDetailsFromWellFormedRequest() {
        return CaseDetails.builder()
            .id(1539007368674134L)
            .jurisdiction("BULKSCAN")
            .state("ScannedRecordFound")
            .caseTypeId("Bulk_Scanned")
            .createdDate(LocalDateTime.parse("2018-10-08T14:02:48.609"))
            .lastModified(LocalDateTime.parse("2018-10-08T14:19:38.758"))
            .securityClassification(Classification.PUBLIC)
            .data(ImmutableMap.<String, Object>builder()
                .put("scanOCRData", emptyList())
                .put("scannedDocuments", emptyList())
                .build()
            )
            .build();
    }
}
