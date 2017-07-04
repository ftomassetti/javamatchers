package com.github.javaparser.matchers.classes;

import com.github.javaparser.ast.Node;
import com.github.javaparser.matchers.MatchContext;
import com.github.javaparser.matchers.MatchResult;
import com.github.javaparser.matchers.Matcher;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class AnyChild<N extends Node> implements Matcher<N> {

    private Matcher<Node> childMatcher;

    public AnyChild(Matcher<Node> childMatcher) {
        this.childMatcher = childMatcher;
    }

    @Override
    public MatchResult<N> match(N node, MatchContext matchContext) {
        List<MatchContext> partial = new LinkedList<>();
        node.getChildNodes().stream()
                                   .map(n -> childMatcher.match(n, matchContext).currentNode(node))
                                   .filter(MatchResult::isNotEmpty)
                                   .map(mr -> mr.getMatches())
                                   .forEach(mcl -> mcl.forEach(mc -> {if (!partial.contains(mc)) {
                                       partial.add(mc);
                                    }}));
        return new MatchResult<N>(node, partial);
    }
}
