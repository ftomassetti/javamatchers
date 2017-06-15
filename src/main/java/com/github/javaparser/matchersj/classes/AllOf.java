package com.github.javaparser.matchersj.classes;

import com.github.javaparser.ast.Node;
import com.github.javaparser.matchersj.MatchContext;
import com.github.javaparser.matchersj.MatchResult;
import com.github.javaparser.matchersj.Matcher;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by djc3 on 6/14/17.
 */
public class AllOf<N extends Node> implements Matcher<N> {

    private List<Matcher<N>> elements = new ArrayList<>();

    public AllOf(Matcher<N>... elements) {
        this.elements = Arrays.asList(elements);
        if (elements.length == 0) {
            throw new IllegalArgumentException();
        }
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
