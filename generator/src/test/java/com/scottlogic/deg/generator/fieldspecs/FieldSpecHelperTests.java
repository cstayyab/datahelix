package com.scottlogic.deg.generator.fieldspecs;

import com.scottlogic.deg.common.profile.Field;
import com.scottlogic.deg.generator.restrictions.NullRestrictions;
import com.scottlogic.deg.common.profile.constraintdetail.Nullness;
import com.scottlogic.deg.generator.restrictions.set.SetRestrictions;
import com.scottlogic.deg.generator.walker.reductive.fieldselectionstrategy.FieldValue;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static com.shazam.shazamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;

class FieldSpecHelperTests {

    FieldSpecHelper fieldSpecHelper = new FieldSpecHelper();
    private final Field field = new Field("field");

    @Test
    void getFieldSpecForValue() {
        FieldValue input = new FieldValue(field, "value", FieldSpec.Empty);

        FieldSpec actual = fieldSpecHelper.getFieldSpecForValue(input);

        FieldSpec expected = FieldSpec.Empty
            .withSetRestrictions(SetRestrictions.fromWhitelist(Collections.singleton("value")), FieldSpecSource.Empty)
            .withNullRestrictions(new NullRestrictions(Nullness.MUST_NOT_BE_NULL), FieldSpecSource.Empty);

        assertEquals(actual, expected);
    }

    @Test
    void getFieldSpecForNullValue() {
        FieldValue input = new FieldValue(field, null, FieldSpec.Empty);

        FieldSpec actual = fieldSpecHelper.getFieldSpecForValue(input);

        FieldSpec expected = FieldSpec.Empty
            .withNullRestrictions(new NullRestrictions(Nullness.MUST_BE_NULL), FieldSpecSource.Empty);

        assertEquals(actual, expected);
    }
}