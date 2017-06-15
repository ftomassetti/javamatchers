package com.github.javaparser.matchersj.classes;

import com.github.javaparser.ast.Node;
import com.github.javaparser.matchersj.MatchContext;
import com.github.javaparser.matchersj.MatchResult;
import com.github.javaparser.matchersj.Matcher;

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
