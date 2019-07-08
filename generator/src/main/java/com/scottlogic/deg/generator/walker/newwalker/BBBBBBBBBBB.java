package com.scottlogic.deg.generator.walker.newwalker;

import com.scottlogic.deg.common.profile.ProfileFields;
import com.scottlogic.deg.generator.decisiontree.ConstraintNode;
import com.scottlogic.deg.generator.decisiontree.DecisionTree;
import com.scottlogic.deg.generator.fieldspecs.RowSpec;
import com.scottlogic.deg.generator.reducer.ConstraintReducer;
import com.scottlogic.deg.generator.walker.RowSpecGenerator;

import javax.inject.Inject;
import java.util.stream.Stream;

public class BBBBBBBBBBB implements RowSpecGenerator {
    private final EEEE eeee;
    private final ConstraintReducer constraintReducer;

    @Inject
    public BBBBBBBBBBB(EEEE eeee, ConstraintReducer constraintReducer) {
        this.eeee = eeee;
        this.constraintReducer = constraintReducer;
    }

    @Override
    public Stream<RowSpec> generateRowSpecs(DecisionTree tree) {
        ConstraintNode constraintNode = eeee.reduceToRowNode(tree.rootNode);

        return Stream.of(getRowspecs(tree.fields, constraintNode));
    }

    public RowSpec getRowspecs(ProfileFields fields, ConstraintNode rootNode){
       return constraintReducer
           .reduceConstraintsToRowSpec(fields, rootNode.getAtomicConstraints())
           .get();//todo
    }

}
