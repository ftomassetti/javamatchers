package com.github.javaparser.matchers.classes;

import com.github.javaparser.ast.Node;
import com.github.javaparser.matchers.MatchContext;
import com.github.javaparser.matchers.MatchResult;
import com.github.javaparser.matchers.Matcher;
import com.google.common.base.Predicates;

import java.util.function.Predicate;

public class Is<N extends Node, T extends Node> implements Matcher<N>{

    private Class<T> type;
    private Predicate<T> condition;

    public Is(Class<T> type) {
        this.type = type;
        this.condition = Predicates.alwaysTrue();
    }

    public Is(Class<T> type, Predicate<T> condition) {
        this.type = type;
        this.condition = condition;
    }

    @Override
    public MatchResult<N> match(N node, MatchContext matchContext) {
        return type.isInstance(node) && condition.test(type.cast(node)) ? MatchResult.of(node, matchContext) : MatchResult.empty(node);
    }
}
