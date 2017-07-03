package com.github.javaparser.matchers.classes;

import com.github.javaparser.ast.Node;
import com.github.javaparser.matchers.MatchContext;
import com.github.javaparser.matchers.MatchResult;
import com.github.javaparser.matchers.Matcher;

/**
 * Created by cdo on 6/15/17.
 */
public class Is<N extends Node, T extends Node> implements Matcher<N>{

    private Class<T> type;

    public Is(Class<T> type) {
        this.type = type;
    }

    @Override
    public MatchResult<N> match(N node, MatchContext matchContext) {
        return type.isInstance(node) ? MatchResult.of(node, matchContext) : MatchResult.empty(node);
    }
}
