package uk.gov.hmcts.reform.bulkscanccdeventhandler.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Address {

    @JsonProperty
    public final String addressLine1;

    @JsonProperty
    public final String addressLine2;

    @JsonProperty
    public final String addressLine3;

    @JsonProperty
    public final String postCode;

    @JsonProperty
    public final String postTown;

    @JsonProperty
    public final String county;

    @JsonProperty
    public final String country;

    public Address(
        String addressLine1,
        String addressLine2,
        String addressLine3,
        String postCode,
        String postTown,
        String county,
        String country
    ) {
        this.addressLine1 = addressLine1;
        this.addressLine2 = addressLine2;
        this.addressLine3 = addressLine3;
        this.postCode = postCode;
        this.postTown = postTown;
        this.county = county;
        this.country = country;
    }
}
