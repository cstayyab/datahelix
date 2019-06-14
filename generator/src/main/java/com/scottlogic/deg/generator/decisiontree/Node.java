package com.scottlogic.deg.generator.decisiontree;

public interface Node {
    boolean hasMarking(NodeMarking detail);
    Node getFirstChild();
    Node getSecondChild();
}