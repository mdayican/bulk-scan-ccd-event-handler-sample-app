package uk.gov.hmcts.reform.bulkscanccdeventhandler.services;

import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.model.OcrData;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.model.OcrDataField;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.model.ResultOrErrors;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.services.exception.CallbackProcessingException;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.services.exception.CcdDataParseException;

import java.util.Map;

import static uk.gov.hmcts.reform.bulkscanccdeventhandler.model.ExceptionRecordFieldNames.SCAN_OCR_DATA;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.model.ResultOrErrors.errors;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.model.ResultOrErrors.result;

/**
 * Converts raw exception record data representing OCR data fields
 * (in the form of a list of maps of objects) into a strongly typed object.
 */
@Service
public class OcrDataParser {

    private final CcdCollectionParser ccdCollectionParser;

    public OcrDataParser(CcdCollectionParser ccdCollectionParser) {
        this.ccdCollectionParser = ccdCollectionParser;
    }

    public ResultOrErrors<OcrData> parseOcrData(
        Map<String, Object> exceptionRecordData
    ) {
        Object rawOcrData = exceptionRecordData.get(SCAN_OCR_DATA);

        if (rawOcrData == null) {
            return errors("Form OCR data is missing");
        } else {
            try {
                return result(
                    new OcrData(
                        ccdCollectionParser.parseCcdCollection(rawOcrData, OcrDataField.class)
                    )
                );
            } catch (CcdDataParseException e) {
                throw new CallbackProcessingException("Failed to parse OCR data from exception record", e);
            }
        }
    }
}
