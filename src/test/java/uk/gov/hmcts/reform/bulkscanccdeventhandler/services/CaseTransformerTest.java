package uk.gov.hmcts.reform.bulkscanccdeventhandler.services;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.handler.model.ExceptionRecord;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.model.Address;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.model.BulkScanCase;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.services.exception.CallbackProcessingException;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.transformer.model.ErrorTransformationResult;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.transformer.model.OkTransformationResult;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.transformer.model.TransformationResult;

import java.time.LocalDate;
import java.util.List;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.assertj.core.api.Java6Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.verify;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.model.ResultOrErrors.errors;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.model.ResultOrErrors.result;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.services.data.SampleCaseConversionData.TRANSFORMED_CCD_CASE;

@ExtendWith(MockitoExtension.class)
public class CaseTransformerTest {

    @Mock
    private CaseDataCreator caseDataCreator;

    private CaseTransformer caseTransformer;

    @BeforeEach
    public void setUp() {
        caseTransformer = new CaseTransformer(caseDataCreator);
    }

    @Test
    public void should_return_successful_transformation_result_when_case_data_creation_is_successful() {
        ExceptionRecord exceptionRecord = sampleExceptionRecord();
        BulkScanCase expectedCaseData = TRANSFORMED_CCD_CASE;

        given(caseDataCreator.createCaseData(exceptionRecord.data)).willReturn(
            result(expectedCaseData)
        );

        TransformationResult result = caseTransformer.transform(exceptionRecord);
        assertSuccessfulResult(result, expectedCaseData, emptyList());

        verify(caseDataCreator).createCaseData(exceptionRecord.data);
    }

    @Test
    public void should_return_failed_transformation_result_when_case_data_creation_fails() {
        ExceptionRecord exceptionRecord = sampleExceptionRecord();
        List<String> caseCreationErrors = ImmutableList.of("error 1", "error 2");

        given(caseDataCreator.createCaseData(exceptionRecord.data)).willReturn(
            errors(caseCreationErrors)
        );

        TransformationResult result = caseTransformer.transform(exceptionRecord);

        assertThat(result).isNotNull();
        assertThat(result).isInstanceOf(ErrorTransformationResult.class);

        ErrorTransformationResult errorResult = (ErrorTransformationResult)result;
        assertThat(errorResult.errors).isEqualTo(caseCreationErrors);
        assertThat(errorResult.warnings).isEmpty();
    }

    @Test
    public void should_return_successful_transformation_result_when_there_are_warnings_but_no_errors() {
        ExceptionRecord exceptionRecord = sampleExceptionRecord();
        BulkScanCase expectedCaseData = caseCausingWarnings();

        given(caseDataCreator.createCaseData(exceptionRecord.data)).willReturn(
            result(expectedCaseData)
        );

        TransformationResult result = caseTransformer.transform(exceptionRecord);

        List<String> expectedWarnings = ImmutableList.of(
            "There are no scanned documents",
            "Date of birth is from before year 1900"
        );

        assertSuccessfulResult(result, expectedCaseData, expectedWarnings);
    }

    @Test
    public void should_throw_exception_when_case_data_creator_throws_exception() {
        Exception exceptionToThrow = new CallbackProcessingException("test exception", null);

        willThrow(exceptionToThrow).given(caseDataCreator).createCaseData(any());

        assertThatThrownBy(() -> caseTransformer.transform(sampleExceptionRecord()))
            .isSameAs(exceptionToThrow);
    }

    private void assertSuccessfulResult(
        TransformationResult result,
        BulkScanCase expectedCaseData,
        List<String> expectedWarnings
    ) {
        assertThat(result).isNotNull();
        assertThat(result).isInstanceOf(OkTransformationResult.class);

        OkTransformationResult successfulResult = (OkTransformationResult)result;
        assertThat(successfulResult.warnings).containsExactlyInAnyOrderElementsOf(expectedWarnings);
        assertThat(successfulResult.caseTypeId).isEqualTo("Bulk_Scanned");
        assertThat(successfulResult.eventId).isEqualTo("createCase");
        assertThat(successfulResult.jurisdiction).isEqualTo("BULKSCAN");
        assertThat(successfulResult.data).isEqualTo(expectedCaseData);
    }

    private ExceptionRecord sampleExceptionRecord() {
        return new ExceptionRecord(
            "id123",
            "jurisdiction1",
            "state1",
            "caseTypeId1",
            ImmutableMap.of("key1", "value1")
        );
    }

    private BulkScanCase caseCausingWarnings() {
        return new BulkScanCase(
            "legacyId1",
            "John",
            "Smith",
            LocalDate.parse("1899-12-31"), // warning: too old
            "0123456789",
            "jsmith@example.com",
            new Address(
                "line 1",
                "line 2",
                "line 3",
                "SW1H 9AJ",
                "postTown1",
                "county1",
                "country1"
            ),
            emptyList() // warning: no scanned documents
        );
    }
}
