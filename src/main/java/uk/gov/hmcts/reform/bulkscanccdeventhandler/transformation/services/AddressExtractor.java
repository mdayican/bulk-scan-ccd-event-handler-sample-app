package uk.gov.hmcts.reform.bulkscanccdeventhandler.transformation.services;

import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.model.Address;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.ocrvalidation.model.in.OcrDataField;

import java.util.List;

import static uk.gov.hmcts.reform.bulkscanccdeventhandler.common.OcrFieldNames.ADDRESS_LINE_1;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.common.OcrFieldNames.ADDRESS_LINE_2;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.common.OcrFieldNames.ADDRESS_LINE_3;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.common.OcrFieldNames.COUNTRY;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.common.OcrFieldNames.COUNTY;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.common.OcrFieldNames.POST_CODE;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.common.OcrFieldNames.POST_TOWN;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.transformation.services.utils.OcrFieldExtractor.get;

@Component
public class AddressExtractor {

    /**
     * Extracts address data from OCR data.
     */
    public Address extractFrom(List<OcrDataField> ocrFields) {
        return new Address(
            get(ocrFields, ADDRESS_LINE_1),
            get(ocrFields, ADDRESS_LINE_2),
            get(ocrFields, ADDRESS_LINE_3),
            get(ocrFields, POST_CODE),
            get(ocrFields, POST_TOWN),
            get(ocrFields, COUNTY),
            get(ocrFields, COUNTRY)
        );
    }
}
