package com.github.javaparser.matchersj;

import com.github.javaparser.ast.Node;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by djc3 on 6/14/17.
 */
public class AllOf<N extends Node> implements Matcher<N> {

    private List<Matcher<N>> elements;

    public AllOf(Matcher<N>... elements) {
        this.elements = Arrays.asList(elements);
        if (elements.length == 0) {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public MatchResult<N> match(Node node, MatchContext matchContext) {
        List<MatchResult<N>> partialResults = elements.stream()
                                                      .map(it -> it.match(node, matchContext).currentNode(node))
                                                      .collect(Collectors.toList());
        return partialResults.get(0);
    }
}
