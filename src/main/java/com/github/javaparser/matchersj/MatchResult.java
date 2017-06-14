package com.github.javaparser.matchersj;

import com.github.javaparser.ast.Node;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Created by djc3 on 6/13/17.
 */
public class MatchResult<N extends Node> {

    private Node currentNode;
    private List<MatchContext> matches;

    public MatchResult(Node currentNode, List<MatchContext> matches) {
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
        List<MatchContext> list = matches.stream()
                                         .map(ctx -> ctx.bind(name, currentNode))
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
        return new MatchResult(node, matches);
    }

    public MatchResult<N> combine (MatchResult<N> other) {
        if (this.isEmpty() || other.isEmpty()) {
            return MatchResult.<N>empty((N)currentNode);
        }

        HashSet<MatchContext> combinations = new HashSet<MatchContext>();

        for (MatchContext mc : matches) {
            for (MatchContext otherMc : other.matches) {
                combinations.add(mc.combine(otherMc));
            }
        }

        return new MatchResult(currentNode, combinations.stream()
                                                        .filter(Objects::nonNull)
                                                        .collect(Collectors.toList()));
    }

    public static void main(String[] args) {
        String str = "lala";
        String str2 = str.concat("lele");

    }
}
