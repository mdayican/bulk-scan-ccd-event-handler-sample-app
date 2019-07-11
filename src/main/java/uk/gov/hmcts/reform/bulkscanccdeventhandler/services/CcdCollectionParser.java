package uk.gov.hmcts.reform.bulkscanccdeventhandler.services;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.model.CcdCollectionElement;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.services.exception.CcdDataParseException;

import java.util.List;

/**
 * Converts raw CCD collection to a list of collection elements of the desired type.
 */
@Service
public class CcdCollectionParser {

    private final ObjectMapper mapper;

    public CcdCollectionParser(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public <T> List<CcdCollectionElement<T>> parseCcdCollection(
        Object rawData,
        Class<T> elementClass
    ) {
        try {
            return convertToList(rawData, elementClass);
        } catch (IllegalArgumentException e) {
            throw new CcdDataParseException(
                "Failed to parse given object to a list of CCD collection elements",
                e
            );
        }
    }

    private <T> List<CcdCollectionElement<T>> convertToList(Object rawData, Class<T> elementClass) {
        JavaType ccdCollectionElementType =
            mapper.getTypeFactory().constructParametricType(
                CcdCollectionElement.class,
                elementClass
            );

        JavaType type = mapper.getTypeFactory().constructParametricType(
            List.class,
            ccdCollectionElementType
        );

        return mapper.convertValue(rawData, type);
    }
}
