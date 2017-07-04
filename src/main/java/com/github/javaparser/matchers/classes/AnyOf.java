package com.github.javaparser.matchers.classes;

import com.github.javaparser.ast.Node;
import com.github.javaparser.matchers.MatchContext;
import com.github.javaparser.matchers.MatchResult;
import com.github.javaparser.matchers.Matcher;

import java.util.Arrays;
import java.util.List;

public class AnyOf<N extends Node> implements Matcher<N> {

    List<Matcher<N>> elements;

    public AnyOf(Matcher<N>... elements) {
        this.elements = Arrays.asList(elements);
    }

    @Override
    public MatchResult<N> match(N node, MatchContext matchContext) {
        MatchResult<N> result;
        return elements.stream()
                       .map(m -> m.match(node, matchContext).currentNode(node))
                       .filter(MatchResult::isNotEmpty)
                       .findFirst().orElseGet(() -> MatchResult.empty(node));
    }
}
