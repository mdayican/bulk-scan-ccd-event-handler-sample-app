package uk.gov.hmcts.reform.bulkscanccdeventhandler.transformation.model.out;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CaseCreationDetails {

    @JsonProperty("case_type_id")
    public final String caseTypeId;

    @JsonProperty("event_id")
    public final String eventId;

    @JsonProperty("case_data")
    public final Object caseData;

    // region constructor
    public CaseCreationDetails(
        String caseTypeId,
        String eventId,
        Object caseData
    ) {
        this.caseTypeId = caseTypeId;
        this.eventId = eventId;
        this.caseData = caseData;
    }
    // endregion
}
