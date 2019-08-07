package uk.gov.hmcts.reform.bulkscanccdeventhandler.transformation.services;

import org.junit.jupiter.api.Test;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.model.Address;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.ocrvalidation.model.in.OcrDataField;

import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.common.OcrFieldNames.ADDRESS_LINE_1;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.common.OcrFieldNames.ADDRESS_LINE_2;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.common.OcrFieldNames.ADDRESS_LINE_3;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.common.OcrFieldNames.COUNTRY;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.common.OcrFieldNames.COUNTY;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.common.OcrFieldNames.POST_CODE;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.common.OcrFieldNames.POST_TOWN;

public class AddressExtractorTest {

    private final AddressExtractor service = new AddressExtractor();

    @Test
    public void should_extract_address_model_from_exception_record_ocr_data() {
        // given
        List<OcrDataField> ocrData =
            asList(
                new OcrDataField(ADDRESS_LINE_1, "line1"),
                new OcrDataField(ADDRESS_LINE_2, "line2"),
                new OcrDataField(ADDRESS_LINE_3, "line3"),
                new OcrDataField(POST_CODE, "post code"),
                new OcrDataField(POST_TOWN, "post town"),
                new OcrDataField(COUNTY, "county"),
                new OcrDataField(COUNTRY, "country")
            );

        // when
        Address result = service.extractFrom(ocrData);

        // then
        assertSoftly(softly -> {
            softly.assertThat(result.addressLine1).isEqualTo("line1");
            softly.assertThat(result.addressLine2).isEqualTo("line2");
            softly.assertThat(result.addressLine3).isEqualTo("line3");
            softly.assertThat(result.postCode).isEqualTo("post code");
            softly.assertThat(result.postTown).isEqualTo("post town");
            softly.assertThat(result.county).isEqualTo("county");
            softly.assertThat(result.country).isEqualTo("country");
        });
    }

    @Test
    public void should_fill_missing_fields_with_nulls() {
        // given
        List<OcrDataField> ocrData =
            asList(
                new OcrDataField(ADDRESS_LINE_1, "line1"),
                new OcrDataField(ADDRESS_LINE_2, "line2"),
                new OcrDataField(ADDRESS_LINE_3, "line3")
            );

        // when
        Address result = service.extractFrom(ocrData);

        // then
        assertSoftly(softly -> {
            softly.assertThat(result.addressLine1).isEqualTo("line1");
            softly.assertThat(result.addressLine2).isEqualTo("line2");
            softly.assertThat(result.addressLine3).isEqualTo("line3");
            softly.assertThat(result.postCode).isNull();
            softly.assertThat(result.postTown).isNull();
            softly.assertThat(result.county).isNull();
            softly.assertThat(result.country).isNull();
        });
    }

    @Test
    public void should_return_empty_object_when_all_fields_are_missing() {
        // given
        List<OcrDataField> ocrData = Collections.emptyList();

        // when
        Address result = service.extractFrom(ocrData);

        // then

        assertSoftly(softly -> {
            softly.assertThat(result.addressLine1).isNull();
            softly.assertThat(result.addressLine2).isNull();
            softly.assertThat(result.addressLine3).isNull();
            softly.assertThat(result.postCode).isNull();
            softly.assertThat(result.postTown).isNull();
            softly.assertThat(result.county).isNull();
            softly.assertThat(result.country).isNull();
        });
    }
}
