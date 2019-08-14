package uk.gov.hmcts.reform.bulkscanccdeventhandler.transformation.services;

import com.google.common.base.Strings;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.transformation.model.out.SampleCase;

import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

@Component
public class CaseValidator {

    public List<String> getWarnings(SampleCase theCase) {
        return Strings.isNullOrEmpty(theCase.email)
            ? singletonList("'email' is empty")
            : emptyList();
    }
}
