package com.scottlogic.deg.generator.generation;

import com.scottlogic.deg.generator.DataGeneratorBaseTypes;
import com.scottlogic.deg.generator.fieldspecs.FieldSpec;
import com.scottlogic.deg.generator.generation.fieldvaluesources.CannedValuesFieldValueSource;
import com.scottlogic.deg.generator.generation.fieldvaluesources.FieldValueSource;
import com.scottlogic.deg.generator.restrictions.StringRestrictions;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class StringFieldValueSourceFactory implements FieldValueSourceFactory {
    public static TypeDefinition getTypeDefinition() {
        return new TypeDefinition(new StringFieldValueSourceFactory());
    }

    @Override
    public FieldValueSource createValueSource(FieldSpec fieldSpec) {
        StringRestrictions stringRestrictions = fieldSpec.getStringRestrictions();

        if (stringRestrictions == null) {
            return new CannedValuesFieldValueSource(Collections.emptyList());
        }

        Set<Object> blacklist = getBlacklist(fieldSpec);

        StringGenerator generator = stringRestrictions.createGenerator();
        if (blacklist.size() > 0) {
            RegexStringGenerator blacklistGenerator = RegexStringGenerator.createFromBlacklist(blacklist);

            generator = generator.intersect(blacklistGenerator);
        }

        return generator.asFieldValueSource();
    }

    private static Set<Object> getBlacklist(FieldSpec fieldSpec) {
        if (fieldSpec.getSetRestrictions() == null)
            return Collections.emptySet();

        return new HashSet<>(fieldSpec.getSetRestrictions().getBlacklist());
    }

    @Override
    public DataGeneratorBaseTypes getUnderlyingDataType() {
        return DataGeneratorBaseTypes.STRING;
    }

    @Override
    public boolean isValid(Object value, FieldSpec fieldSpec) {
        StringRestrictions stringRestrictions = fieldSpec.getStringRestrictions();
        return value instanceof String && (stringRestrictions == null || stringRestrictions.match(value));
    }
}

