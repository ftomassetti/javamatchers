package com.github.javaparser.matchersj.classes;

import com.github.javaparser.ast.Node;
import com.github.javaparser.matchersj.MatchContext;
import com.github.javaparser.matchersj.MatchResult;
import com.github.javaparser.matchersj.Matcher;

/**
 * Created by cdo on 6/15/17.
 */
public class Parent<N extends Node> implements Matcher<N> {

    private Matcher<N> parentMatcher;

    public Parent(Matcher<N> parentMatcher) {
        this.parentMatcher = parentMatcher;
    }

    @Override
    public MatchResult<N> match(N node, MatchContext matchContext) {
        return node.getParentNode().isPresent() ?
                parentMatcher.match((N)node.getParentNode().get(), matchContext).currentNode(node) :
                MatchResult.empty(node);
    }
}
