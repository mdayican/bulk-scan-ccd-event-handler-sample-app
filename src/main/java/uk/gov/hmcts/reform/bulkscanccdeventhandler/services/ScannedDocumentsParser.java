package uk.gov.hmcts.reform.bulkscanccdeventhandler.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.model.CcdCollectionElement;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.model.ResultOrErrors;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.model.ScannedDocument;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.services.exception.CallbackProcessingException;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.services.exception.CcdDataParseException;

import java.util.List;
import java.util.Map;

import static java.util.Collections.emptyList;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.model.ExceptionRecordFieldNames.SCANNED_DOCUMENTS;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.model.ResultOrErrors.result;

/**
 * Converts raw exception record data representing scanned documents
 * (in the form of a list of maps of objects) into a strongly typed collection.
 */
@Service
public class ScannedDocumentsParser {

    private static final Logger log = LoggerFactory.getLogger(OcrDataParser.class);

    private final CcdCollectionParser ccdCollectionParser;

    public ScannedDocumentsParser(CcdCollectionParser ccdCollectionParser) {
        this.ccdCollectionParser = ccdCollectionParser;
    }

    public ResultOrErrors<List<CcdCollectionElement<ScannedDocument>>> parseScannedDocuments(
        Map<String, Object> exceptionRecordData
    ) {
        Object scannedDocumentsElement = exceptionRecordData.get(SCANNED_DOCUMENTS);

        if (scannedDocumentsElement == null) {
            return result(emptyList());
        } else {
            try {
                List<CcdCollectionElement<ScannedDocument>> scannedDocuments =
                    ccdCollectionParser.parseCcdCollection(scannedDocumentsElement, ScannedDocument.class);

                return result(scannedDocuments);
            } catch (CcdDataParseException e) {
                throw new CallbackProcessingException(
                    "Failed to parse scanned documents from exception record",
                    e
                );
            }
        }
    }
}
