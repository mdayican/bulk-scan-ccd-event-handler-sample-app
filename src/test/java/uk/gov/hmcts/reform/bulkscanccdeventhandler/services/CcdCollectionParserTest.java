package uk.gov.hmcts.reform.bulkscanccdeventhandler.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import org.junit.jupiter.api.Test;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.model.CcdCollectionElement;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.model.TestCollectionItem;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.services.exception.CcdDataParseException;

import java.util.Arrays;
import java.util.List;

import static java.util.Collections.singletonMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class CcdCollectionParserTest {

    private CcdCollectionParser ccdCollectionParser = new CcdCollectionParser(new ObjectMapper());

    @Test
    public void should_convert_valid_ccd_structure_to_list_of_items_of_desired_type() {
        List rawList = Arrays.asList(
            singletonMap(
                "value",
                ImmutableMap.of(
                    "intField", 123,
                    "stringField", "some string",
                    "boolField", true
                )
            ),
            singletonMap(
                "value",
                ImmutableMap.of(
                    "intField", 234,
                    "stringField", "other string",
                    "boolField", false
                )
            )
        );

        List<CcdCollectionElement<TestCollectionItem>> result =
            ccdCollectionParser.parseCcdCollection(rawList, TestCollectionItem.class);

        assertThat(result)
            .usingRecursiveFieldByFieldElementComparator()
            .containsExactly(
                new CcdCollectionElement<>(new TestCollectionItem(123, "some string", true)),
                new CcdCollectionElement<>(new TestCollectionItem(234, "other string", false))
            );
    }

    @Test
    public void should_throw_exception_when_ccd_structure_is_invalid() {
        List rawList = Arrays.asList(
            singletonMap(
                "value",
                singletonMap("invalid", "map")
            )
        );

        assertThatThrownBy(() ->
            ccdCollectionParser.parseCcdCollection(rawList, TestCollectionItem.class)
        )
            .isInstanceOf(CcdDataParseException.class)
            .hasMessage("Failed to parse given object to a list of CCD collection elements");
    }
}
