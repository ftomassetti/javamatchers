package com.github.javaparser.matchersj;

import com.github.javaparser.ast.Node;
import com.github.javaparser.matchersj.classes.Is;
import com.github.javaparser.symbolsolver.javaparser.Navigator;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by cdo on 6/15/17.
 */
public class Matchers {

    public static <N extends Node, T extends Node> Matcher<N> Is(Class<T> type) {
        return new Is<N, T>(type);
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
