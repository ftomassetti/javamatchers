package com.github.javaparser.matchers.classes;

import com.github.javaparser.ast.Node;
import com.github.javaparser.matchers.MatchContext;
import com.github.javaparser.matchers.MatchResult;
import com.github.javaparser.matchers.Matcher;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class AllOf<N extends Node> implements Matcher<N> {

    private List<Matcher<N>> elements = new ArrayList<>();

    public AllOf(Matcher<N>... elements) {
        if (elements.length == 0) {
            throw new IllegalArgumentException();
        }
        this.elements = Arrays.asList(elements);
    }

    @Override
    public MatchResult<N> match(N node, MatchContext matchContext) {
        List<MatchResult<N>> partialResults = this.elements.stream()
                                                           .map(mr -> mr.match(node, matchContext).currentNode(node))
                                                           .collect(Collectors.toList());

        return partialResults.stream().anyMatch(MatchResult::isNotEmpty) ?
                MatchResult.empty(node): combine(partialResults);
    }


    private MatchResult<N> combine(List<MatchResult<N>> partialResults) {
        if (partialResults.size() == 1) {
            return partialResults.get(0);
        }
        return combine(partialResults.get(0), partialResults.subList(1, partialResults.size() - 1));
    }

    private MatchResult<N> combine(MatchResult<N> matchResult, List<MatchResult<N>> partialResults) {
        if (partialResults.isEmpty()) {
            return matchResult;
        } else {
            return combine(matchResult.combine(partialResults.get(0)), partialResults.subList(1, partialResults.size() - 1));
        }
    }
}
