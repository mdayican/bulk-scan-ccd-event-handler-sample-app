package uk.gov.hmcts.reform.bulkscanccdeventhandler.transformation.model.in;

import com.fasterxml.jackson.annotation.JsonProperty;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.ocrvalidation.model.in.OcrDataField;

import java.time.LocalDateTime;
import java.util.List;

public class ExceptionRecord {

    public final String caseTypeId;
    public final String poBox;
    public final String jurisdiction;
    public final JourneyClassification journeyClassification;
    public final LocalDateTime deliveryDate;
    public final LocalDateTime openingDate;
    public final List<ScannedDocument> scannedDocuments;
    public final List<OcrDataField> ocrDataFields;

    public ExceptionRecord(
        @JsonProperty("case_type_id") String caseTypeId,
        @JsonProperty("po_box") String poBox,
        @JsonProperty("po_box_jurisdiction") String jurisdiction,
        @JsonProperty("journey_classification") JourneyClassification journeyClassification,
        @JsonProperty("delivery_date") LocalDateTime deliveryDate,
        @JsonProperty("opening_date") LocalDateTime openingDate,
        @JsonProperty("scanned_documents") List<ScannedDocument> scannedDocuments,
        @JsonProperty("ocr_data_fields") List<OcrDataField> ocrDataFields
    ) {
        this.caseTypeId = caseTypeId;
        this.poBox = poBox;
        this.jurisdiction = jurisdiction;
        this.journeyClassification = journeyClassification;
        this.deliveryDate = deliveryDate;
        this.openingDate = openingDate;
        this.scannedDocuments = scannedDocuments;
        this.ocrDataFields = ocrDataFields;
    }
}
