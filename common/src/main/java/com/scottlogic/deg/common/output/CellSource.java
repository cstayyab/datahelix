package com.scottlogic.deg.common.output;

import com.scottlogic.deg.common.profile.Field;
import com.scottlogic.deg.common.profile.constraints.atomic.AtomicConstraint;
import com.scottlogic.deg.common.profile.RuleInformation;
import com.scottlogic.deg.common.util.FlatMappingSpliterator;

import java.util.Set;
import java.util.stream.Collectors;

public class CellSource {
    public final Field field;
    private final DataBagValueSource source;

    public CellSource(Field field, DataBagValueSource value) {
        this.field = field;
        this.source = value != null ? value : DataBagValueSource.Empty;
    }

    public Set<AtomicConstraint> getConstraints(){
        return this.source.getConstraints();
    }

    public boolean isViolated(AtomicConstraint constraint){
        return this.source.getViolatedConstraints().contains(constraint);
    }

    public boolean isViolated(RuleInformation rule){
        return FlatMappingSpliterator.flatMap(source.getViolatedConstraints()
            .stream(), c -> c.getRules().stream())
            .anyMatch(r -> r.equals(rule));
    }

    public Set<RuleInformation> getRules(){
        return FlatMappingSpliterator.flatMap(source.getConstraints()
            .stream(), c -> c.getRules().stream())
            .collect(Collectors.toSet());
    }
}