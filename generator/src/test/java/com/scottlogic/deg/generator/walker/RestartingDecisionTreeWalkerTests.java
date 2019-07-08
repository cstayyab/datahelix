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

package com.scottlogic.deg.generator.walker;

import com.scottlogic.deg.common.profile.Field;
import com.scottlogic.deg.common.profile.ProfileFields;
import com.scottlogic.deg.generator.decisiontree.DecisionTree;
import com.scottlogic.deg.generator.decisiontree.TreeConstraintNode;
import com.scottlogic.deg.generator.generation.databags.DataBag;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.*;

class RestartingDecisionTreeWalkerTests {
    private DecisionTree tree;
    private RestartingDecisionTreeWalker walker;
    private ReductiveDecisionTreeWalker underlyingWalker;

    @BeforeEach
    public void beforeEach(){
        tree = new DecisionTree(
            new TreeConstraintNode(),
            new ProfileFields(Arrays.asList(new Field("field1"), new Field("field2")))
        );

        underlyingWalker = mock(ReductiveDecisionTreeWalker.class);
        walker = new RestartingDecisionTreeWalker(underlyingWalker);
    }

    /**
     * If RANDOM mode is enabled there should be two rows of data where field1 & field2 have (synthetically) random
     * values for each row
     */
    @Test
    public void shouldProduceTwoRowsOfRandomDataOneRowSpecFromEachIteration() {
        when(underlyingWalker.walk(tree)).thenReturn(
            Stream.of(rowSpec("first-iteration-first-random-row"), rowSpec("first-iteration-second-random-row")),
            Stream.of(rowSpec("second-iteration-first-random-row"), rowSpec("second-iteration-second-random-row"))
        );

        List<DataBag> result = walker.walk(tree).limit(2).collect(Collectors.toList());

        verify(underlyingWalker, times(2)).walk(tree);
        Assert.assertThat(
            result.stream().map(DataBag::toString).collect(Collectors.toList()),
            hasItems("first-iteration-first-random-row", "second-iteration-first-random-row"));
    }

    @Test
    public void shouldProduceNoData() {
        when(underlyingWalker.walk(tree)).thenReturn(
            Stream.of(rowSpec("first-iteration-first-random-row"), rowSpec("first-iteration-second-random-row")),
            Stream.empty(),
            Stream.of(rowSpec("third-iteration-first-random-row"), rowSpec("third-iteration-second-random-row"))
        );

        List<DataBag> result = walker.walk(tree).limit(2).collect(Collectors.toList());

        verify(underlyingWalker, times(3)).walk(tree);
        Assert.assertThat(
            result.stream().map(DataBag::toString).collect(Collectors.toList()),
            hasItems("first-iteration-first-random-row", "third-iteration-first-random-row"));
    }

    @Test
    public void shouldAccommodateNoDataInSubsequentIteration() {
        when(underlyingWalker.walk(tree)).thenReturn(
            Stream.empty()
        );

        List<DataBag> result = walker.walk(tree).limit(2).collect(Collectors.toList());

        verify(underlyingWalker, times(1)).walk(tree);
        Assert.assertThat(
            result.stream().iterator().hasNext(),
            is(false));
    }

    private static DataBag rowSpec(String detail) {
        return mock(DataBag.class, detail);
    }
}