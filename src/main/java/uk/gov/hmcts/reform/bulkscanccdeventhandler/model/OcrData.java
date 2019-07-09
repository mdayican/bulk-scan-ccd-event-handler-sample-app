package uk.gov.hmcts.reform.bulkscanccdeventhandler.model;

import java.util.List;

public class OcrData {

    public final List<CcdCollectionElement<OcrDataField>> fields;

    public OcrData(List<CcdCollectionElement<OcrDataField>> fields) {
        this.fields = fields;
    }
}
