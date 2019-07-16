package uk.gov.hmcts.reform.bulkscanccdeventhandler.services.data;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.model.Address;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.model.BulkScanCase;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.model.CcdCollectionElement;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.model.OcrData;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.model.OcrDataField;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.model.ScannedDocument;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

/**
 * Contains sample raw exception record data and corresponding case.
 */
public final class SampleCaseConversionData {

    public static final String LEGACY_ID = "legacy-id-123";
    public static final String FIRST_NAME = "John";
    public static final String LAST_NAME = "Smith";
    public static final String DATE_OF_BIRTH = "1980-03-04";
    public static final String CONTACT_NUMBER = "0123456789";
    public static final String EMAIL = "email1@example.com";
    public static final String ADDRESS_LINE_1 = "Flat 1 Golden House";
    public static final String ADDRESS_LINE_2 = "123 Some Street";
    public static final String ADDRESS_LINE_3 = "City of Westminster";
    public static final String POST_CODE = "SW1H 9AJ";
    public static final String POST_TOWN = "London";
    public static final String COUNTY = "City of Westminster";
    public static final String COUNTRY = "United Kingdom";
    public static final String DOCUMENT_TYPE = "Other";
    public static final String DOCUMENT_SUBTYPE = "subtype1";
    public static final String DOCUMENT_URL = "http://example.com/url1";
    public static final String DOCUMENT_CONTROL_NUMBER = "controlNumber1";
    public static final String DOCUMENT_FILENAME = "filename1";
    public static final String DOCUMENT_SCANNED_DATE = "2019-07-01T12:34:56.123";
    public static final String DOCUMENT_DELIVERY_DATE = "2019-07-02T01:23:45.234";
    public static final String DOCUMENT_EXCEPTION_RECORD_REFERENCE = "exceptionRef1";

    /**
     * OCR data as represented in exception record.
     */
    public static List<Object> RAW_OCR_DATA = ImmutableList
        .builder()
        .add(rawOcrField("legacy_id", LEGACY_ID))
        .add(rawOcrField("first_name", FIRST_NAME))
        .add(rawOcrField("last_name", LAST_NAME))
        .add(rawOcrField("date_of_birth", DATE_OF_BIRTH))
        .add(rawOcrField("contact_number", CONTACT_NUMBER))
        .add(rawOcrField("email", EMAIL))
        .add(rawOcrField("address_line_1", ADDRESS_LINE_1))
        .add(rawOcrField("address_line_2", ADDRESS_LINE_2))
        .add(rawOcrField("address_line_3", ADDRESS_LINE_3))
        .add(rawOcrField("post_code", POST_CODE))
        .add(rawOcrField("post_town", POST_TOWN))
        .add(rawOcrField("county", COUNTY))
        .add(rawOcrField("country", COUNTRY))
        .build();

    /**
     * Scanned documents as represented in exception record.
     */
    public static final List<Object> RAW_SCANNED_DOCUMENTS =
        ImmutableList.of(
            rawScannedDocument(
                DOCUMENT_TYPE,
                DOCUMENT_SUBTYPE,
                DOCUMENT_URL,
                DOCUMENT_CONTROL_NUMBER,
                DOCUMENT_FILENAME,
                DOCUMENT_SCANNED_DATE,
                DOCUMENT_DELIVERY_DATE,
                DOCUMENT_EXCEPTION_RECORD_REFERENCE
            )
        );

    public static final Map<String, Object> RAW_EXCEPTION_RECORD_DATA =
        ImmutableMap.of(
            "scanOCRData", RAW_OCR_DATA,
            "scannedDocuments", RAW_SCANNED_DOCUMENTS
        );

    /**
     * OCR data expected after parsing from the raw version.
     */
    public static final OcrData PARSED_OCR_DATA = new OcrData(
        ImmutableList
            .<OcrDataField>builder()
            .add(new OcrDataField("legacy_id", LEGACY_ID))
            .add(new OcrDataField("first_name", FIRST_NAME))
            .add(new OcrDataField("last_name", LAST_NAME))
            .add(new OcrDataField("date_of_birth", DATE_OF_BIRTH))
            .add(new OcrDataField("contact_number", CONTACT_NUMBER))
            .add(new OcrDataField("email", EMAIL))
            .add(new OcrDataField("address_line_1", ADDRESS_LINE_1))
            .add(new OcrDataField("address_line_2", ADDRESS_LINE_2))
            .add(new OcrDataField("address_line_3", ADDRESS_LINE_3))
            .add(new OcrDataField("post_code", POST_CODE))
            .add(new OcrDataField("post_town", POST_TOWN))
            .add(new OcrDataField("county", COUNTY))
            .add(new OcrDataField("country", COUNTRY))
            .build()
            .stream()
            .map(field -> new CcdCollectionElement<>(field))
            .collect(toList())
    );

    /**
     * Scanned documents expected after parsing from exception record.
     */
    public static final List<CcdCollectionElement<ScannedDocument>> PARSED_SCANNED_DOCUMENTS =
        ImmutableList.of(
            new CcdCollectionElement<>(
                new ScannedDocument(
                    DOCUMENT_TYPE,
                    DOCUMENT_SUBTYPE,
                    DOCUMENT_URL,
                    DOCUMENT_CONTROL_NUMBER,
                    DOCUMENT_FILENAME,
                    LocalDateTime.parse(DOCUMENT_SCANNED_DATE),
                    LocalDateTime.parse(DOCUMENT_DELIVERY_DATE),
                    DOCUMENT_EXCEPTION_RECORD_REFERENCE
                )
            )
        );

    /**
     * CCD case data expected as a result of transformation from raw exception record data.
     */
    public static final BulkScanCase TRANSFORMED_CCD_CASE =
        new BulkScanCase(
            LEGACY_ID,
            FIRST_NAME,
            LAST_NAME,
            LocalDate.parse(DATE_OF_BIRTH),
            CONTACT_NUMBER,
            EMAIL,
            new Address(
                ADDRESS_LINE_1,
                ADDRESS_LINE_2,
                ADDRESS_LINE_3,
                POST_CODE,
                POST_TOWN,
                COUNTY,
                COUNTRY
            ),
            PARSED_SCANNED_DOCUMENTS
        );

    private static Map<String, Object> rawOcrField(String key, String value) {
        return ImmutableMap.of(
            "value",
            ImmutableMap.of(
                "key", key,
                "value", value
            )
        );
    }

    private static Map<String, Object> rawScannedDocument(
        String type,
        String subtype,
        String url,
        String controlNumber,
        String fileName,
        String scannedDate,
        String deliveryDate,
        String exceptionRecordReference
    ) {
        return ImmutableMap.of(
            "value",
            ImmutableMap
                .builder()
                .put("type", type)
                .put("subtype", subtype)
                .put("url", url)
                .put("controlNumber", controlNumber)
                .put("fileName", fileName)
                .put("scannedDate", scannedDate)
                .put("deliveryDate", deliveryDate)
                .put("exceptionRecordReference", exceptionRecordReference)
                .build()
        );
    }

    private SampleCaseConversionData() {
        // utility class
    }
}
