package com.github.javaparser.matchersj.classes;

import com.github.javaparser.ast.Node;
import com.github.javaparser.matchersj.MatchContext;
import com.github.javaparser.matchersj.MatchResult;
import com.github.javaparser.matchersj.Matcher;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by cdo on 6/15/17.
 */
public class HasDescendant<N extends Node> implements Matcher<N> {

    Matcher<Node> descendantMatcher;

    public HasDescendant(Matcher<Node> descendantMatcher) {
        this.descendantMatcher = descendantMatcher;
    }

    @Override
    public MatchResult<N> match(N node, MatchContext matchContext) {
        return getDescendants(node).stream()
                                   .map(n -> descendantMatcher.match(n, matchContext).currentNode(node))
                                   .findFirst().orElseGet(() -> MatchResult.empty(node));
    }

    private List<Node> getDescendants(Node node) {
        List<Node> descendants = new LinkedList<Node>();
        descendants.addAll(node.getChildNodes());
        node.getChildNodes().stream()
                            .forEach(child -> descendants.addAll(getDescendants(child)));
        return descendants;
    }
}
