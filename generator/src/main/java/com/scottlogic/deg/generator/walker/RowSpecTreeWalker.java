package com.scottlogic.deg.generator.walker;

import com.google.inject.Inject;
import com.scottlogic.deg.common.util.FlatMappingSpliterator;
import com.scottlogic.deg.generator.decisiontree.DecisionTree;
import com.scottlogic.deg.generator.generation.databags.DataBag;
import com.scottlogic.deg.generator.generation.databags.RowSpecDataBagGenerator;

import java.util.stream.Stream;

public class RowSpecTreeWalker implements DecisionTreeWalker {
    private final RowSpecDataBagGenerator rowSpecDataBagGenerator;
    private final RowSpecGenerator rowSpecGenerator;

    @Inject
    public RowSpecTreeWalker(RowSpecDataBagGenerator rowSpecDataBagGenerator, RowSpecGenerator rowSpecGenerator) {
        this.rowSpecDataBagGenerator = rowSpecDataBagGenerator;
        this.rowSpecGenerator = rowSpecGenerator;
    }

    @Override
    public Stream<DataBag> walk(DecisionTree tree) {
        return FlatMappingSpliterator.flatMap(
            rowSpecGenerator.generateRowSpecs(tree),
            rowSpecDataBagGenerator::createDataBags);
    }
}
