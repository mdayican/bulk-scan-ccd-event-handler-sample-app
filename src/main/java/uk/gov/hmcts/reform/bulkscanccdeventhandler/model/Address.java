package uk.gov.hmcts.reform.bulkscanccdeventhandler.model;

public class Address {

    public final String addressLine1;
    public final String addressLine2;
    public final String addressLine3;
    public final String postCode;
    public final String postTown;
    public final String county;
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
