package com.github.javaparser.matchersj.classes;

import com.github.javaparser.ast.Node;
import com.github.javaparser.matchersj.MatchContext;
import com.github.javaparser.matchersj.MatchResult;
import com.github.javaparser.matchersj.Matcher;

/**
 * Created by cdo on 6/15/17.
 */
public class HasChild<N extends Node> implements Matcher<N> {

    Matcher<Node> childMatcher;

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
