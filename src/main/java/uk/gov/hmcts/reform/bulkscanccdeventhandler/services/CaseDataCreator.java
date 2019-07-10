package uk.gov.hmcts.reform.bulkscanccdeventhandler.services;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.model.Address;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.model.CcdCase;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.model.CcdCollectionElement;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.model.OcrData;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.model.ResultOrErrors;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.model.ScannedDocument;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.model.ExceptionRecordFieldNames.SCANNED_DOCUMENTS;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.model.OcrFieldNames.ADDRESS_LINE_1;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.model.OcrFieldNames.ADDRESS_LINE_2;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.model.OcrFieldNames.ADDRESS_LINE_3;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.model.OcrFieldNames.CONTACT_NUMBER;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.model.OcrFieldNames.COUNTRY;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.model.OcrFieldNames.COUNTY;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.model.OcrFieldNames.DATE_OF_BIRTH;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.model.OcrFieldNames.EMAIL;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.model.OcrFieldNames.FIRST_NAME;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.model.OcrFieldNames.LAST_NAME;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.model.OcrFieldNames.LEGACY_ID;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.model.OcrFieldNames.POST_CODE;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.model.OcrFieldNames.POST_TOWN;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.model.ResultOrErrors.errors;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.model.ResultOrErrors.result;

@Service
public class CaseDataCreator {

    private final OcrDataParser ocrDataParser;
    private final Function<OcrData, OcrFieldRetriever> ocrDataValidatorSupplier;
    private final ObjectMapper mapper;

    public CaseDataCreator(
        OcrDataParser ocrDataParser,
        Function<OcrData, OcrFieldRetriever> ocrDataValidatorSupplier,
        ObjectMapper mapper
    ) {
        this.ocrDataParser = ocrDataParser;
        this.ocrDataValidatorSupplier = ocrDataValidatorSupplier;
        this.mapper = mapper;
    }

    public ResultOrErrors<CcdCase> tryCreateCaseData(Map<String, Object> exceptionRecordData) {
        ResultOrErrors<OcrData> ocrDataParsingResult = ocrDataParser.parseOcrData(exceptionRecordData);

        if (ocrDataParsingResult.isSuccessful()) {
            OcrFieldRetriever ocrFieldRetriever = ocrDataValidatorSupplier.apply(ocrDataParsingResult.get());
            return getCcdCase(exceptionRecordData, ocrFieldRetriever);
        } else {
            List<String> parseErrors = Stream.concat(
                ocrDataParsingResult.errors.stream(),
                getScannedDocuments(exceptionRecordData).errors.stream()
            ).collect(toList());

            return errors(parseErrors);
        }
    }

    private ResultOrErrors<CcdCase> getCcdCase(
        Map<String, Object> exceptionRecordData,
        OcrFieldRetriever ocrFieldRetriever
    ) {
        ResultOrErrors<String> legacyId = ocrFieldRetriever.getFieldValue(LEGACY_ID, false, String.class);
        ResultOrErrors<String> firstName = ocrFieldRetriever.getFieldValue(FIRST_NAME, true, String.class);
        ResultOrErrors<String> lastName = ocrFieldRetriever.getFieldValue(LAST_NAME, true, String.class);
        ResultOrErrors<LocalDate> dateOfBirth = ocrFieldRetriever.getFieldValue(DATE_OF_BIRTH, true, LocalDate.class);
        ResultOrErrors<String> contactNumber = ocrFieldRetriever.getFieldValue(CONTACT_NUMBER, true, String.class);
        ResultOrErrors<String> email = ocrFieldRetriever.getFieldValue(EMAIL, true, String.class);
        ResultOrErrors<Address> address = getAddress(ocrFieldRetriever);
        ResultOrErrors<List<CcdCollectionElement<ScannedDocument>>> scannedDocuments =
            getScannedDocuments(exceptionRecordData);

        List<String> fieldValidationErrors = Stream.of(
            legacyId, firstName, lastName, dateOfBirth, contactNumber, email, address, scannedDocuments
        )
            .flatMap(result -> result.errors.stream())
            .collect(toList());

        if (fieldValidationErrors.isEmpty()) {
            return result(
                new CcdCase(
                    legacyId.get(),
                    firstName.get(),
                    lastName.get(),
                    dateOfBirth.get(),
                    contactNumber.get(),
                    email.get(),
                    address.get(),
                    scannedDocuments.get()
                )
            );
        } else {
            return errors(fieldValidationErrors);
        }
    }

    private ResultOrErrors<Address> getAddress(OcrFieldRetriever ocrValidator) {
        ResultOrErrors<String> addressLine1 = ocrValidator.getFieldValue(ADDRESS_LINE_1, true, String.class);
        ResultOrErrors<String> addressLine2 = ocrValidator.getFieldValue(ADDRESS_LINE_2, false, String.class);
        ResultOrErrors<String> addressLine3 = ocrValidator.getFieldValue(ADDRESS_LINE_3, false, String.class);
        ResultOrErrors<String> postCode = ocrValidator.getFieldValue(POST_CODE, true, String.class);
        ResultOrErrors<String> postTown = ocrValidator.getFieldValue(POST_TOWN, true, String.class);
        ResultOrErrors<String> county = ocrValidator.getFieldValue(COUNTY, true, String.class);
        ResultOrErrors<String> country = ocrValidator.getFieldValue(COUNTRY, true, String.class);


        List<String> fieldErrors = Stream.of(
            addressLine1, addressLine2, addressLine3, postCode, postTown, county, country
        )
            .flatMap(result -> result.errors.stream())
            .collect(toList());

        if (fieldErrors.isEmpty()) {
            return result(
                new Address(
                    addressLine1.get(),
                    addressLine2.get(),
                    addressLine3.get(),
                    postCode.get(),
                    postTown.get(),
                    county.get(),
                    country.get())
            );
        } else {
            return errors(fieldErrors);
        }
    }

    private ResultOrErrors<List<CcdCollectionElement<ScannedDocument>>> getScannedDocuments(
        Map<String, Object> exceptionRecordData
    ) {
        Object scannedDocumentsElement = exceptionRecordData.get(SCANNED_DOCUMENTS);

        if (scannedDocumentsElement == null) {
            return result(emptyList());
        } else {
            JavaType ccdCollectionElementType =
                mapper.getTypeFactory().constructParametricType(
                    CcdCollectionElement.class,
                    ScannedDocument.class
                );

            JavaType documentListType = mapper.getTypeFactory().constructParametricType(
                List.class,
                ccdCollectionElementType
            );

            try {
                List<CcdCollectionElement<ScannedDocument>> scannedDocuments =
                    mapper.convertValue(scannedDocumentsElement, documentListType);

                return result(scannedDocuments);
            } catch (IllegalArgumentException ex) {
                return errors("Scanned documents collection has invalid format");
            }
        }
    }
}
