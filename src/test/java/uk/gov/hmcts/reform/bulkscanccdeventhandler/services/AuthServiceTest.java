package uk.gov.hmcts.reform.bulkscanccdeventhandler.services;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.reform.authorisation.exceptions.InvalidTokenException;
import uk.gov.hmcts.reform.authorisation.validators.AuthTokenValidator;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.services.exception.ForbiddenException;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.services.exception.UnauthenticatedException;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    private static final String SERVICE_HEADER = "some-header";

    @Mock
    private AuthTokenValidator validator;

    private AuthService service;

    @BeforeEach
    void setUp() {
        service = new AuthService(validator, asList("allowed_service1", "allowed_service2"));
    }

    @AfterEach
    void tearDown() {
        reset(validator);
    }

    @Test
    void should_throw_unauthenticated_exception_when_auth_header_is_null() {
        // when
        Throwable exception = catchThrowable(() -> service.authenticate(null));

        // then
        assertThat(exception)
            .isInstanceOf(UnauthenticatedException.class)
            .hasMessage("Provided S2S token is missing or invalid");

        // and
        verify(validator, never()).getServiceName(anyString());
    }

    @Test
    void should_throw_unauthenticated_exception_when_auth_header_is_empty() {
        // when
        Throwable exception = catchThrowable(() -> service.authenticate("  "));

        // then
        assertThat(exception)
            .isInstanceOf(UnauthenticatedException.class)
            .hasMessage("Provided S2S token is missing or invalid");

        // and
        verify(validator, never()).getServiceName(anyString());
    }

    @Test
    void should_throw_invalid_token_exception_when_invalid_token_received() {
        // given
        willThrow(InvalidTokenException.class).given(validator).getServiceName(anyString());

        // when
        Throwable exception = catchThrowable(() -> service.authenticate(SERVICE_HEADER));

        // then
        assertThat(exception).isInstanceOf(InvalidTokenException.class);
    }

    @Test
    void should_return_the_service_name_when_valid_token_is_received() {
        // given
        given(validator.getServiceName(SERVICE_HEADER)).willReturn("service1");

        // when
        String serviceName = service.authenticate(SERVICE_HEADER);

        // then
        assertThat(serviceName).isEqualTo("service1");
    }

    @Test
    void should_throw_forbidden_exception_when_service_not_in_allowed_services() {
        // when
        Throwable exception = catchThrowable(() -> service.assertIsAllowedService("service_not_allowed"));

        // then
        assertThat(exception).isInstanceOf(ForbiddenException.class);
    }

    @Test
    void should_not_throw_exception_when_the_requested_service_is_allowed() {
        // when
        Throwable exception = catchThrowable(() -> service.assertIsAllowedService("allowed_service1"));

        // then
        assertThat(exception).isNull();
    }
}
