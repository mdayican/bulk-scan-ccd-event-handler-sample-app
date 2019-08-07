package uk.gov.hmcts.reform.bulkscanccdeventhandler.common;

import com.google.common.collect.ImmutableSet;

import java.util.Set;

public final class OcrFieldNames {

    public static final String LEGACY_ID = "legacy_id";
    public static final String FIRST_NAME = "first_name";
    public static final String LAST_NAME = "last_name";
    public static final String DATE_OF_BIRTH = "date_of_birth";
    public static final String CONTACT_NUMBER = "contact_number";
    public static final String EMAIL = "email";
    public static final String ADDRESS_LINE_1 = "address_line_1";
    public static final String ADDRESS_LINE_2 = "address_line_2";
    public static final String ADDRESS_LINE_3 = "address_line_3";
    public static final String POST_CODE = "post_code";
    public static final String POST_TOWN = "post_town";
    public static final String COUNTY = "county";
    public static final String COUNTRY = "country";

    private OcrFieldNames() {
        // utility class, not to be instantiated
    }

    public static Set<String> getRequiredFields() {
        return ImmutableSet.of(FIRST_NAME, LAST_NAME);
    }
}
