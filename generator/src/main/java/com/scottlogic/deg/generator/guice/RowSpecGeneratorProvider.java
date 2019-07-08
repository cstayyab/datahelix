package com.scottlogic.deg.generator.guice;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.scottlogic.deg.generator.walker.CartesianProductRowSpecGenerator;
import com.scottlogic.deg.generator.walker.RowSpecGenerator;
import com.scottlogic.deg.generator.walker.newwalker.BBBBBBBBBBB;

public class RowSpecGeneratorProvider implements Provider<RowSpecGenerator> {
    private final CartesianProductRowSpecGenerator cartesianProductRowSpecGenerator;
    private final BBBBBBBBBBB bbbbbbbbbbb;

    @Inject
    public RowSpecGeneratorProvider(CartesianProductRowSpecGenerator cartesianProductRowSpecGenerator, BBBBBBBBBBB bbbbbbbbbbb) {
        this.cartesianProductRowSpecGenerator = cartesianProductRowSpecGenerator;
        this.bbbbbbbbbbb = bbbbbbbbbbb;
    }

    @Override
    public RowSpecGenerator get() {
        return bbbbbbbbbbb;
    }
}
