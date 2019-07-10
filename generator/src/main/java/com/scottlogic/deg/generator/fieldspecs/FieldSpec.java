/*
 * Copyright 2019 Scott Logic Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.scottlogic.deg.generator.fieldspecs;

import com.scottlogic.deg.common.profile.constraints.atomic.IsOfTypeConstraint;
import com.scottlogic.deg.common.profile.constraints.atomic.IsOfTypeConstraint.Types;

import com.scottlogic.deg.generator.fieldspecs.whitelist.FrequencyWhitelist;
import com.scottlogic.deg.generator.fieldspecs.whitelist.Whitelist;
import com.scottlogic.deg.generator.restrictions.*;
import com.scottlogic.deg.common.util.HeterogeneousTypeContainer;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Details a column's atomic constraints
 * A fieldSpec can either be a whitelist of allowed values, or a set of restrictions.
 * if a fieldSpec can not be a type, it will not have any restrictions for that type
 * This is enforced during merging.
 */
public class FieldSpec {
    public static final FieldSpec Empty =
        new FieldSpec(null, new HeterogeneousTypeContainer<>(), true, null);

    private final boolean nullable;
    private final String formatting;
    private final Whitelist<Object> whitelist;
    private final HeterogeneousTypeContainer<Restrictions> restrictions;

    private FieldSpec(
        Whitelist<Object> whitelist,
        HeterogeneousTypeContainer<Restrictions> restrictions,
        boolean nullable,
        String formatting
    ) {
        this.whitelist = whitelist;
        this.restrictions = restrictions;
        this.nullable = nullable;
        this.formatting = formatting;
    }

    public boolean isNullable() {
        return nullable;
    }

    public Whitelist<Object> getWhitelist() {
        return whitelist;
    }

    public BlacklistRestrictions getBlacklistRestrictions() {
        return restrictions.get(BlacklistRestrictions.class).orElse(null);
    }

    public NumericRestrictions getNumericRestrictions() {
        return restrictions.get(NumericRestrictions.class).orElse(null);
    }

    public StringRestrictions getStringRestrictions() {
        return restrictions.get(StringRestrictions.class).orElse(null);
    }

    public TypeRestrictions getTypeRestrictions() {
        return restrictions.get(TypeRestrictions.class).orElse(null);
    }

    public DateTimeRestrictions getDateTimeRestrictions() {
        return restrictions.get(DateTimeRestrictions.class).orElse(null);
    }

    public String getFormatting() {
        return formatting;
    }

    public FieldSpec withWhitelist(Whitelist<Object> whitelist) {
        return new FieldSpec(whitelist, new HeterogeneousTypeContainer<>(), nullable, formatting);
    }

    public FieldSpec withNumericRestrictions(NumericRestrictions numericRestrictions) {
        return withConstraint(NumericRestrictions.class, numericRestrictions);
    }

    public FieldSpec withBlacklistRestrictions(BlacklistRestrictions blacklistRestrictions) {
        return withConstraint(BlacklistRestrictions.class, blacklistRestrictions);
    }

    public FieldSpec withTypeRestrictions(TypeRestrictions typeRestrictions) {
        return withConstraint(TypeRestrictions.class, typeRestrictions);
    }

    public FieldSpec withStringRestrictions(StringRestrictions stringRestrictions) {
        return withConstraint(StringRestrictions.class, stringRestrictions);
    }

    public FieldSpec withNotNull() {
        return new FieldSpec(whitelist, restrictions, false, formatting);
    }

    public static FieldSpec mustBeNull() {
        return FieldSpec.Empty.withWhitelist(FrequencyWhitelist.empty());
    }

    public FieldSpec withDateTimeRestrictions(DateTimeRestrictions dateTimeRestrictions) {
        return withConstraint(DateTimeRestrictions.class, dateTimeRestrictions);
    }

    public FieldSpec withFormatting(String formatting) {
        return new FieldSpec(whitelist, restrictions, nullable, formatting);
    }

    public FieldSpec withoutType(IsOfTypeConstraint.Types type){
        TypeRestrictions typeRestrictions = getTypeRestrictions();
        if (typeRestrictions == null){
            typeRestrictions = DataTypeRestrictions.ALL_TYPES_PERMITTED;
        }
        typeRestrictions = typeRestrictions.except(type);

        if (typeRestrictions.getAllowedTypes().isEmpty()){
            return mustBeNull();
        }

        return withTypeRestrictions(typeRestrictions);
    }

    private <T extends Restrictions> FieldSpec withConstraint(Class<T> type, T restriction) {
        if (restriction == null){
            return this;
        }
        return new FieldSpec(null, restrictions.put(type, restriction), nullable, formatting);
    }

    public boolean isTypeAllowed(IsOfTypeConstraint.Types type){
        return getTypeRestrictions() == null || getTypeRestrictions().isTypeAllowed(type);
    }

    @Override
    public String toString() {
        if (whitelist != null) {
            if (whitelist.set().isEmpty()) {
                return "Null only";
            }
            return (nullable ? "" : "Not Null") + String.format("IN %s", whitelist);
        }

        List<String> propertyStrings = restrictions.values()
                .stream()
                .filter(Objects::nonNull)
                .map(Object::toString)
                .collect(Collectors.toList());

        if (propertyStrings.isEmpty()) {
            return "<all values>";
        }

        if (!nullable){
            propertyStrings.add(0, "Not Null");
        }

        return String.join(" & ", propertyStrings);
    }

    /**
     * Create a predicate that returns TRUE for all (and only) values permitted by this FieldSpec
     */
    public boolean permits(Object value) {
        TypeRestrictions typeRestrictions = getTypeRestrictions();
        if (typeRestrictions != null) {
            for (Types type : Types.values()) {
                if (!typeRestrictions.isTypeAllowed(type) && type.isInstanceOf(value)) {
                    return false;
                }
            }
        }

        Set<Class<? extends Restrictions>> keys = new HashSet<>();
        keys.add(NumericRestrictions.class);
        keys.add(DateTimeRestrictions.class);
        keys.add(StringRestrictions.class);
        keys.add(BlacklistRestrictions.class);

        Set<TypedRestrictions> toCheckForMatch = restrictions.getMultiple(keys)
            .stream()
            .map(r -> (TypedRestrictions) r)
            .collect(Collectors.toSet());
        for (TypedRestrictions restriction : toCheckForMatch) {
            if (restriction != null && restriction.isInstanceOf(value) && !restriction.match(value)) {
                return false;
            }
        }

        return true;
    }

    public int hashCode() {
        return Objects.hash(nullable, whitelist, restrictions, formatting);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }

        FieldSpec other = (FieldSpec) obj;
        return Objects.equals(nullable, other.nullable)
            && Objects.equals(whitelist, other.whitelist)
            && Objects.equals(restrictions, other.restrictions)
            && Objects.equals(formatting, other.formatting);
    }
}
