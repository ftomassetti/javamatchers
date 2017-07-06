package com.github.javaparser.matchers;

import com.github.javaparser.ast.Node;

/**
 * A matcher will verify if a certain pattern can be found in a node, considering a given context.
 */
public interface Matcher<N extends Node> {

    MatchResult<N> match(N node, MatchContext matchContext);

}
