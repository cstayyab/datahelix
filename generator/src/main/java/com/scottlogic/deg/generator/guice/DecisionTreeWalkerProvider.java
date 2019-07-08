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

package com.scottlogic.deg.generator.guice;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.scottlogic.deg.generator.generation.GenerationConfigSource;
import com.scottlogic.deg.generator.walker.*;

import static com.scottlogic.deg.generator.config.detail.DataGenerationType.RANDOM;

public class DecisionTreeWalkerProvider implements Provider<DecisionTreeWalker> {
    private final ReductiveDecisionTreeWalker reductiveDecisionTreeWalker;
    private final RowSpecTreeWalker rowSpecTreeWalker;
    private final GenerationConfigSource configSource;

    @Inject
    public DecisionTreeWalkerProvider(
        ReductiveDecisionTreeWalker reductiveDecisionTreeWalker,
        RowSpecTreeWalker rowSpecTreeWalker,
        GenerationConfigSource configSource) {
        this.reductiveDecisionTreeWalker = reductiveDecisionTreeWalker;
        this.rowSpecTreeWalker = rowSpecTreeWalker;
        this.configSource = configSource;
    }

    @Override
    public DecisionTreeWalker get() {
        if (configSource.getGenerationType() == RANDOM) {
            return new RestartingDecisionTreeWalker(getUnderlyingWalker());
        }

        return getUnderlyingWalker();
    }

    private DecisionTreeWalker getUnderlyingWalker() {
        switch(configSource.getWalkerType()) {
            case REDUCTIVE:

                return reductiveDecisionTreeWalker;

            default:
                return rowSpecTreeWalker;
      }
    }
}
