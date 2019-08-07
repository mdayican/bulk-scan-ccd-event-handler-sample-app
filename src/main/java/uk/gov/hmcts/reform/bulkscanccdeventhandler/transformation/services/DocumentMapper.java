package uk.gov.hmcts.reform.bulkscanccdeventhandler.transformation.services;

import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.model.CcdCollectionElement;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.model.ScannedDocument;

@Component
public class DocumentMapper {

    /**
     * Converts document in Exception Record model to document in Case model.
     */
    public CcdCollectionElement<ScannedDocument> toCaseDoc(
        uk.gov.hmcts.reform.bulkscanccdeventhandler.transformation.model.in.ScannedDocument exceptionRecordDoc,
        String exceptionRecordReference
    ) {
        return new CcdCollectionElement<>(
            new ScannedDocument(
                exceptionRecordDoc.type,
                exceptionRecordDoc.subtype,
                exceptionRecordDoc.url,
                exceptionRecordDoc.controlNumber,
                exceptionRecordDoc.fileName,
                exceptionRecordDoc.scannedDate,
                exceptionRecordDoc.deliveryDate,
                exceptionRecordReference
            )
        );
    }
}
