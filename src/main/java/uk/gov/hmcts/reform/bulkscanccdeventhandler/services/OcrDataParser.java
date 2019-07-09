package uk.gov.hmcts.reform.bulkscanccdeventhandler.services;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.model.CcdCollectionElement;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.model.OcrData;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.model.OcrDataField;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.model.ResultOrErrors;

import java.util.List;
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

    private final ObjectMapper mapper;

    public OcrDataParser(ObjectMapper mapper) {
        this.mapper = mapper;
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
                    new OcrData(convertToOcrFieldList(rawOcrData))
                );
            } catch (IllegalArgumentException ex) {
                return errors("Form OCR data has invalid format");
            }
        }
    }

    private List<CcdCollectionElement<OcrDataField>> convertToOcrFieldList(Object rawOcrData) {
        JavaType ccdCollectionElementType =
            mapper.getTypeFactory().constructParametricType(
                CcdCollectionElement.class,
                OcrDataField.class
            );

        JavaType type = mapper.getTypeFactory().constructParametricType(
            List.class,
            ccdCollectionElementType
        );

        return mapper.convertValue(rawOcrData, type);
    }
}
