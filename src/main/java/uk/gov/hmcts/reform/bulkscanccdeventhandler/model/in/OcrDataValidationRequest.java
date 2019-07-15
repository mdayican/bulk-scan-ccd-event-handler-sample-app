package uk.gov.hmcts.reform.bulkscanccdeventhandler.model.in;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.model.FormType;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.model.OcrDataField;

import java.util.List;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class OcrDataValidationRequest {

    @ApiModelProperty(value = "List of ocr data fields to be validated.", required = true)
    @NotEmpty
    public final List<OcrDataField> ocrDataFields;

    @ApiModelProperty(value = "Type of the paper based form.", required = true)
    @NotNull
    public final FormType formType;

    public OcrDataValidationRequest(
        @JsonProperty("ocr_data_fields") List<OcrDataField> ocrDataFields,
        @JsonProperty("form_type") FormType formType
    ) {
        this.ocrDataFields = ocrDataFields;
        this.formType = formType;
    }

    public List<OcrDataField> getOcrDataFields() {
        return ocrDataFields;
    }

    public FormType getFormType() {
        return formType;
    }
}
