package com.scottlogic.deg.generator.walker;

import com.scottlogic.deg.generator.decisiontree.ConstraintNode;
import com.scottlogic.deg.generator.decisiontree.DecisionTree;
import com.scottlogic.deg.generator.fieldspecs.RowSpec;

import java.util.stream.Stream;

public interface RowSpecGenerator {
    Stream<RowSpec> generateRowSpecs(DecisionTree tree);
}
