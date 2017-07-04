package com.github.javaparser.matchers;

import com.github.javaparser.ast.Node;

public interface Matcher<N extends Node> {

    MatchResult<N> match(N node, MatchContext matchContext);

}
