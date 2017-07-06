package com.github.javaparser.matchers.classes;

import com.github.javaparser.ast.Node;
import com.github.javaparser.matchers.MatchContext;
import com.github.javaparser.matchers.MatchResult;
import com.github.javaparser.matchers.Matcher;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This Matcher will be satisfied if at least one descendant of the node is matched by the descendantMatcher.
 */
public class HasDescendant<N extends Node> implements Matcher<N> {

    private Matcher<Node> descendantMatcher;

    public HasDescendant(Matcher<Node> descendantMatcher) {
        this.descendantMatcher = descendantMatcher;
    }

    @Override
    public MatchResult<N> match(N node, MatchContext matchContext) {
        MatchResult result = getDescendants(node).stream()
                .map(n -> descendantMatcher.match(n, matchContext).currentNode(node))
                .filter(it -> it.isNotEmpty())
                .findFirst().orElseGet(() -> MatchResult.empty(node));
        return result;
    }

    private List<Node> getDescendants(Node node) {
        List<Node> descendants = new LinkedList<>();
        descendants.addAll(node.getChildNodes());
        node.getChildNodes().forEach(child -> descendants.addAll(getDescendants(child)));
        return descendants;
    }
}
