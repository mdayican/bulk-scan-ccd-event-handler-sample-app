package uk.gov.hmcts.reform.bulkscanccdeventhandler.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.model.OcrData;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.model.OcrDataField;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.model.ResultOrErrors;
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

    private static final Logger log = LoggerFactory.getLogger(OcrDataParser.class);

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
                log.warn("Failed to parse OCR data", e);
                return errors("Form OCR data has invalid format");
            }
        }
    }
}
