package com.github.javaparser.matchers.classes;

import com.github.javaparser.ast.Node;
import com.github.javaparser.matchers.MatchContext;
import com.github.javaparser.matchers.MatchResult;
import com.github.javaparser.matchers.Matcher;

/**
 * This Matcher will be satisfied if at least one child of the node satisfy the childMatcher.
 */
public class HasChild<N extends Node> implements Matcher<N> {

    private Matcher<Node> childMatcher;

    public HasChild(Matcher<Node> childMatcher) {
        this.childMatcher = childMatcher;
    }

    @Override
    public MatchResult<N> match(N node, MatchContext matchContext) {
        return node.getChildNodes().stream()
                                   .map(n -> childMatcher.match(n, matchContext).currentNode(node))
                                   .filter(MatchResult::isNotEmpty)
                                   .findFirst().orElseGet(() -> MatchResult.empty(node));
    }
}
