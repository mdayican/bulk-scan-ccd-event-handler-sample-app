package uk.gov.hmcts.reform.bulkscanccdeventhandler.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.handler.ExceptionRecordEventHandler;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.handler.ExceptionRecordEventHandlerFactory;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.services.CaseTransformer;

@Configuration
public class ExceptionRecordHandlerConfig {

    @Bean
    public ExceptionRecordEventHandler exceptionRecordEventHandler(
        CaseTransformer caseTransformer,
        AuthTokenGenerator authTokenGenerator,
        @Value("${ccd.url}") String ccdUrl

    ) {
        return
            ExceptionRecordEventHandlerFactory.getHandler(
                caseTransformer,
                ccdUrl,
                authTokenGenerator::generate
            );
    }

}
