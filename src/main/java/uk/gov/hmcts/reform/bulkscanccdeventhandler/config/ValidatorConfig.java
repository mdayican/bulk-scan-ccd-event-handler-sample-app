package uk.gov.hmcts.reform.bulkscanccdeventhandler.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.model.OcrData;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.services.OcrFieldRetriever;

import java.util.function.Function;

@Configuration
public class ValidatorConfig {

    @Bean
    public Function<OcrData, OcrFieldRetriever> getOcrDataValidatorSupplier(ObjectMapper mapper) {
        return ocrData -> new OcrFieldRetriever(ocrData, mapper);
    }
}
