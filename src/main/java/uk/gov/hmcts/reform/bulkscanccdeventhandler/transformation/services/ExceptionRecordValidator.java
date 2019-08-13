package uk.gov.hmcts.reform.bulkscanccdeventhandler.transformation.services;

import com.google.common.collect.Sets;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.common.OcrFieldNames;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.transformation.model.in.ExceptionRecord;

import java.util.Set;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

@Component
public class ExceptionRecordValidator {

    public void assertIsValid(ExceptionRecord exceptionRecord) {

        Set<String> missingFields =
            Sets.difference(
                OcrFieldNames.getRequiredFields(),
                exceptionRecord.ocrDataFields.stream().map(it -> it.name).collect(toSet())
            );

        if (!missingFields.isEmpty()) {
            throw new InvalidExceptionRecordException(
                missingFields.stream().map(it -> "'" + it + "' is required").collect(toList())
            );
        }
    }
}
