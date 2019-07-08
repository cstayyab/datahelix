package com.scottlogic.deg.generator.walker.newwalker;

import com.scottlogic.deg.common.profile.Field;
import com.scottlogic.deg.common.profile.constraints.atomic.AtomicConstraint;
import com.scottlogic.deg.generator.decisiontree.ConstraintNode;
import com.scottlogic.deg.generator.decisiontree.DecisionNode;
import com.scottlogic.deg.generator.fieldspecs.FieldSpec;
import com.scottlogic.deg.generator.utils.JavaUtilRandomNumberGenerator;
import com.scottlogic.deg.generator.walker.reductive.Merged;
import com.scottlogic.deg.generator.walker.reductive.ReductiveTreePruner;

import javax.inject.Inject;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class EEEE {

    private final ReductiveTreePruner reductiveTreePruner;
    private final JavaUtilRandomNumberGenerator randomNumberGenerator;

    @Inject
    public EEEE(ReductiveTreePruner reductiveTreePruner, JavaUtilRandomNumberGenerator randomNumberGenerator) {
        this.reductiveTreePruner = reductiveTreePruner;
        this.randomNumberGenerator = randomNumberGenerator;
    }

    ConstraintNode reduceToRowNode(ConstraintNode rootNode){
        if (rootNode.getDecisions().isEmpty()){
            return rootNode;
        }

        DecisionNode decision = getDecision(rootNode);

        Set<DecisionNode> newDecisions = removeDecision(rootNode, decision);

        ConstraintNode newRoot = getOption(decision); //todo add backtracking or streamability

        Map<Field, FieldSpec> pulledUpFields = getFields(newRoot);

        newRoot = newRoot
            .addDecisions(newDecisions)
            .addAtomicConstraints(rootNode.getAtomicConstraints());

        Merged<ConstraintNode> prunedRoot = reductiveTreePruner.pruneConstraintNode(newRoot, pulledUpFields);

        if (prunedRoot.isContradictory()){
            //todo
        }

        return reduceToRowNode(prunedRoot.get());
    }

    private Map<Field, FieldSpec> getFields(ConstraintNode option) {
        return option.getAtomicConstraints().stream()
            .map(AtomicConstraint::getField)
            .distinct()
            .collect(Collectors.toMap(
                Function.identity(),
                field-> FieldSpec.Empty));
    }

    private ConstraintNode getOption(DecisionNode decision) {
        return getRandom(decision.getOptions());
    }

    private Set<DecisionNode> removeDecision(ConstraintNode rootNode, DecisionNode decision) {
        return rootNode.getDecisions().stream()
            .filter(decisionNode -> decisionNode != decision)
            .collect(Collectors.toSet());
    }

    private DecisionNode getDecision(ConstraintNode rootNode) {
        return getRandom(rootNode.getDecisions());
    }

    private <T> T getRandom(Collection<T> options) {
        int i = randomNumberGenerator.nextInt(options.size());
        return options.stream()
            .skip(i)
            .findFirst().get();
    }
}
