package com.github.javaparser.matchers.classes;

import com.github.javaparser.ast.Node;
import com.github.javaparser.matchers.MatchContext;
import com.github.javaparser.matchers.MatchResult;
import com.github.javaparser.matchers.Matcher;

import java.util.Arrays;
import java.util.List;

public class AnyOf<N extends Node> implements Matcher<N> {

    private List<Matcher<N>> elements;

    public AnyOf(Matcher<N>... elements) {
        if (elements.length == 0) {
            throw new IllegalArgumentException("AnyOf should contain at least one element matcher");
        }
        this.elements = Arrays.asList(elements);
    }

    @Override
    public MatchResult<N> match(N node, MatchContext matchContext) {
        return elements.stream()
                       .map(m -> m.match(node, matchContext).currentNode(node))
                       .filter(MatchResult::isNotEmpty)
                       .findFirst().orElseGet(() -> MatchResult.empty(node));
    }
}
