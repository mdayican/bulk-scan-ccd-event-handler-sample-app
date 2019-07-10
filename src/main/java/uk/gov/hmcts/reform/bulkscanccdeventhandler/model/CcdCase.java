package uk.gov.hmcts.reform.bulkscanccdeventhandler.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.util.List;

public class CcdCase {

    @JsonProperty
    public final String legacyId;

    @JsonProperty
    public final String firstName;

    @JsonProperty
    public final String lastName;

    @JsonProperty
    public final LocalDate dateOfBirth;

    @JsonProperty
    public final String contactNumber;

    @JsonProperty
    public final String email;

    @JsonProperty
    public final Address address;

    @JsonProperty
    public final List<CcdCollectionElement<ScannedDocument>> scannedDocuments;

    public CcdCase(
        String legacyId,
        String firstName,
        String lastName,
        LocalDate dateOfBirth,
        String contactNumber,
        String email,
        Address address,
        List<CcdCollectionElement<ScannedDocument>> scannedDocuments
    ) {
        this.legacyId = legacyId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.contactNumber = contactNumber;
        this.email = email;
        this.address = address;
        this.scannedDocuments = scannedDocuments;
    }
}
