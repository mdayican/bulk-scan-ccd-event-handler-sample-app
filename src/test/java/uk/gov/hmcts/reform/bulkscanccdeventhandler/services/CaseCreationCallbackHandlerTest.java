package uk.gov.hmcts.reform.bulkscanccdeventhandler.services;

import com.google.common.collect.ImmutableMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.handler.ExceptionRecordEventHandler;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.handler.model.CaseCreationRequest;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.handler.model.CaseCreationResult;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.handler.model.ExceptionRecord;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.services.exception.BadCallbackRequestException;
import uk.gov.hmcts.reform.ccd.client.model.CaseDetails;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
public class CaseCreationCallbackHandlerTest {

    @Mock
    private ExceptionRecordEventHandler exceptionRecordEventHandler;

    private CaseCreationCallbackHandler callbackHandler;

    @BeforeEach
    public void setUp() {
        callbackHandler = new CaseCreationCallbackHandler(exceptionRecordEventHandler);
    }

    @Test
    public void should_throw_exception_when_id_is_missing() {
        CaseDetails exceptionRecordDetails = CaseDetails
            .builder()
            .id(null)
            .jurisdiction("jurisdiction1")
            .build();

        assertThatThrownBy(() ->
            callbackHandler.handleCaseCreationCallback(
                exceptionRecordDetails,
                "eventId1",
                "token1",
                "user1",
                true
            )
        ).isInstanceOf(BadCallbackRequestException.class)
            .hasMessage("Exception record ID is missing");

        verifyNoMoreInteractions(exceptionRecordEventHandler);
    }

    @Test
    public void should_return_result_from_exception_record_event_handler_when_data_is_positively_verified() {
        // given
        CaseCreationResult expectedResult =
            new CaseCreationResult("case-id-123", emptyList(), singletonList("warning 1"));

        given(exceptionRecordEventHandler.handle(any())).willReturn(expectedResult);

        String eventId = "eventId1";
        String idamToken = "idamToken1";
        String idamUserId = "idamUser1";

        CaseDetails exceptionRecordDetails = sampleValidExceptionRecordDetails();

        // when
        CaseCreationResult actualResult = callbackHandler.handleCaseCreationCallback(
            exceptionRecordDetails,
            eventId,
            idamToken,
            idamUserId,
            true
        );

        // then
        assertThat(actualResult).isSameAs(expectedResult);

        verifyEventHandlerHasBeenCalledWithCorrectData(exceptionRecordDetails, eventId, idamToken, idamUserId);
    }

    @Test
    public void should_throw_exception_when_exception_record_event_handler_fails() {
        Exception exceptionToThrow = new BadCallbackRequestException("test exception");
        willThrow(exceptionToThrow).given(exceptionRecordEventHandler).handle(any());
        assertThatThrownBy(
            () -> callbackHandler.handleCaseCreationCallback(
                sampleValidExceptionRecordDetails(),
                "eventId1",
                "idamToken1",
                "idamUserId1",
                true
            )
        ).isSameAs(exceptionToThrow);
    }

    private void verifyEventHandlerHasBeenCalledWithCorrectData(
        CaseDetails exceptionRecordDetails,
        String eventId,
        String idamToken,
        String idamUserId
    ) {
        ArgumentCaptor<CaseCreationRequest> requestCaptor = ArgumentCaptor.forClass(CaseCreationRequest.class);
        verify(exceptionRecordEventHandler).handle(requestCaptor.capture());

        CaseCreationRequest actualRequest = requestCaptor.getValue();

        assertThat(actualRequest.eventId).isEqualTo(eventId);
        assertThat(actualRequest.idamToken).isEqualTo(idamToken);
        assertThat(actualRequest.idamUserId).isEqualTo(idamUserId);
        assertThat(actualRequest.ignoreWarnings).isTrue();

        ExceptionRecord expectedExceptionRecord = new ExceptionRecord(
            exceptionRecordDetails.getId().toString(),
            exceptionRecordDetails.getJurisdiction(),
            exceptionRecordDetails.getState(),
            exceptionRecordDetails.getCaseTypeId(),
            exceptionRecordDetails.getData()
        );

        assertThat(actualRequest.exceptionRecord)
            .isEqualToComparingFieldByFieldRecursively(expectedExceptionRecord);
    }

    private CaseDetails sampleValidExceptionRecordDetails() {
        return CaseDetails
            .builder()
            .id(123456L)
            .jurisdiction("jurisdiction1")
            .state("state1")
            .caseTypeId("caseTypeId1")
            .data(ImmutableMap.of("key1", "value1"))
            .build();
    }
}
