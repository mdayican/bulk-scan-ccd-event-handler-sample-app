package uk.gov.hmcts.reform.bulkscanccdeventhandler.transformation.services;

import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.transformation.model.in.InputScannedDoc;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.transformation.model.out.Item;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.transformation.model.out.ScannedDocument;

@Component
public class DocumentMapper {

    /**
     * Converts document in Exception Record model to document in Case model.
     */
    public Item<ScannedDocument> toCaseDoc(
        InputScannedDoc exceptionRecordDoc,
        String exceptionRecordReference
    ) {
        if (exceptionRecordDoc == null) {
            return null;
        } else {
            return new Item<>(new ScannedDocument(
                exceptionRecordDoc.type,
                exceptionRecordDoc.subtype,
                exceptionRecordDoc.url,
                exceptionRecordDoc.controlNumber,
                exceptionRecordDoc.fileName,
                exceptionRecordDoc.scannedDate,
                exceptionRecordDoc.deliveryDate,
                exceptionRecordReference
            ));
        }
    }
}
