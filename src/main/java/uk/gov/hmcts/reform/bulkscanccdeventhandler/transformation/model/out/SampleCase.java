package uk.gov.hmcts.reform.bulkscanccdeventhandler.transformation.model.out;

import java.util.List;

public class SampleCase {

    public final String legacyId;
    public final String firstName;
    public final String lastName;
    public final String dateOfBirth;
    public final String contactNumber;
    public final String email;
    public final Address address;
    public final List<Item<ScannedDocument>> scannedDocuments;

    public SampleCase(
        String legacyId,
        String firstName,
        String lastName,
        String dateOfBirth,
        String contactNumber,
        String email,
        Address address,
        List<Item<ScannedDocument>> scannedDocuments
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
