package uk.gov.hmcts.reform.bulkscanccdeventhandler.transformation.services;

import org.junit.jupiter.api.Test;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.transformation.model.out.Address;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.transformation.model.out.SampleCase;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

public class CaseValidatorTest {

    private final CaseValidator validator = new CaseValidator();

    @Test
    public void should_return_warning_when_email_is_not_provided() {
        assertSoftly(softly -> {
            softly.assertThat(validator.getWarnings(caseWithEmail(""))).containsExactly("'email' is empty");
            softly.assertThat(validator.getWarnings(caseWithEmail(null))).containsExactly("'email' is empty");
        });
    }

    @Test
    void should_return_empty_array_if_case_is_valid() {
        assertThat(validator.getWarnings(caseWithEmail("hello@test.com"))).isEmpty();
    }

    private SampleCase caseWithEmail(String email) {
        return new SampleCase(
            "legacy-id",
            "first-name",
            "last-name",
            "dob",
            "contact-number",
            email,
            new Address("line-1", "line-2", "line-3", "post-code", "post-town", "county", "country"),
            emptyList()
        );
    }
}
