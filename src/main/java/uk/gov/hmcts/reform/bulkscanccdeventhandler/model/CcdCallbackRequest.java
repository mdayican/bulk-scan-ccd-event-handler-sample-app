package uk.gov.hmcts.reform.bulkscanccdeventhandler.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import uk.gov.hmcts.reform.ccd.client.model.CaseDetails;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CcdCallbackRequest {

    @JsonProperty
    public final String eventId;

    @JsonProperty
    public final CaseDetails caseDetails;

    @JsonProperty
    public final boolean ignoreWarnings;

    public CcdCallbackRequest(
        @JsonProperty("event_id") String eventId,
        @JsonProperty("case_details") CaseDetails caseDetails,
        @JsonProperty("ignore_warning") boolean ignoreWarnings
    ) {
        this.eventId = eventId;
        this.caseDetails = caseDetails;
        this.ignoreWarnings = ignoreWarnings;
    }
}
