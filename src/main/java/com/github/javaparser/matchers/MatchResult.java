package com.github.javaparser.matchers;

import com.github.javaparser.ast.Node;

import java.util.*;
import java.util.stream.Collectors;

/**
 * The result of a match operation.
 * The result could represent a failure: in that case the result will be empty.
 * When representing a success it will contain all possible contexts that can be obtained.
 */
public class MatchResult<N extends Node> {

    private N currentNode;
    private List<MatchContext> matches;

    public MatchResult(N currentNode, List<MatchContext> matches) {
        if (matches == null) {
            throw new IllegalArgumentException();
        }
        for (MatchContext mc : matches) {
            if (mc == null) {
                throw new IllegalArgumentException();
            }
        }
        this.currentNode = currentNode;
        this.matches = matches;
    }

    public static <T extends Node> MatchResult<T> empty(T node) {
        return new MatchResult<T>(node, Collections.emptyList());
    }

    public static <T extends Node> MatchResult<T> of(T node, MatchContext matchContext) {
        return new MatchResult<T>(node, Collections.singletonList(matchContext));
    }

    public MatchResult<N> bind(String name) {
        return bind(name, currentNode);
    }

    public MatchResult<N> bind(String name, Object value) {
        List<MatchContext> list = matches.stream()
                .map(ctx -> ctx.bind(name, value))
                .collect(Collectors.toList());
        return new MatchResult(currentNode, list);
    }

    public boolean isEmpty() {
        return matches.isEmpty();
    }

    public boolean isNotEmpty() {
        return (!matches.isEmpty());
    }

    public <T extends Node> MatchResult<T> currentNode(T node) {
        return new MatchResult<T>(node, matches);
    }
    public List<MatchContext> getMatches() {
        return this.matches;
    }

    public N getCurrentNode() {
        return currentNode;
    }

    public MatchResult<N> combine (MatchResult<N> other) {
        if (this.isEmpty() || other.isEmpty()) {
            return MatchResult.empty((N)currentNode);
        }

        Set<MatchContext> combinations = new HashSet<>();

        for (MatchContext mc : matches) {
            for (MatchContext otherMc : other.matches) {
                combinations.add(mc.combine(otherMc));
            }
        }

        return new MatchResult(currentNode, combinations.stream().filter(Objects::nonNull)
                                                        .collect(Collectors.toList()));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MatchResult<?> that = (MatchResult<?>) o;

        if (!currentNode.equals(that.currentNode)) return false;
        return matches.equals(that.matches);
    }

    @Override
    public int hashCode() {
        int result = currentNode.hashCode();
        result = 31 * result + matches.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "MatchResult{" +
                "currentNode=" + currentNode +
                ", matches=" + matches +
                '}';
    }
}
