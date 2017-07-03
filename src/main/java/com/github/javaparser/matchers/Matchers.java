package com.github.javaparser.matchers;

import com.github.javaparser.ast.Node;
import com.github.javaparser.matchers.classes.*;
import com.github.javaparser.symbolsolver.javaparser.Navigator;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Most static methods in this class are a bare call to its classes
 * This was written in order to get better code elegance when writing matchers
 */
public class Matchers {

    public static <N extends Node, T extends Node> Matcher<N> is(Class<T> type) {
        return new Is<N, T>(type);
    }

    public static <N extends Node> Matcher<N> parent(Matcher<Node> parentMatcher) {
        return new Parent(parentMatcher);
    }

    public static <N extends Node> Matcher<N> anyOf(Matcher<Node>... elements) {
        return new AnyOf(elements);
    }

    public static <N extends Node> Matcher<N> allOf(Matcher<Node>... elements) {
        return new AllOf(elements);
    }

    public static <N extends Node> Matcher<N> hasDescendant(Matcher<Node> descendantMatcher) {
        return new HasDescendant(descendantMatcher);
    }

    public static <N extends Node> List<MatchResult<N>> match(Node ast, Class<N> nodeClass, Matcher<N> matcher)  {
        return Navigator.findAllNodesOfGivenClass(ast, nodeClass).stream()
                                                                 .map(n -> matcher.match(n, MatchContext.empty()))
                                                                 .filter(MatchResult::isNotEmpty)
                                                                 .collect(Collectors.toList());
    }

    public static List<MatchResult<Node>> match(Node ast, Matcher<Node> matcher) {
        return match(ast, Node.class, matcher);
    }
}
