package com.github.javaparser.matchersj.classes;

import com.github.javaparser.ast.Node;
import com.github.javaparser.matchersj.MatchContext;
import com.github.javaparser.matchersj.MatchResult;
import com.github.javaparser.matchersj.Matcher;

import java.util.Arrays;
import java.util.List;

/**
 * Created by cdo on 6/15/17.
 */
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
