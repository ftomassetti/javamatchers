package com.github.javaparser.matchers.classes;

import com.github.javaparser.ast.Node;
import com.github.javaparser.matchers.MatchContext;
import com.github.javaparser.matchers.MatchResult;
import com.github.javaparser.matchers.Matcher;

import java.util.function.Function;

/**
 * This Matcher just modify the matchContext by binding one value.
 * @param <N>
 */
public class Binder<N extends Node> implements Matcher<N> {

    private String name;
    private Matcher<N> matcher;
    private Function<N, ?> valueMapper;


    /**
     * The Matcher will bind the current node to the given name.
     * @param name
     * @param matcher
     */
    public Binder(String name, Matcher<N> matcher) {
        this.name = name;
        this.matcher = matcher;
    }

    /**
     * The Matcher will apply the valueMappter function to the current node to determine the value to bind to the given name.
     * @param name
     * @param matcher
     * @param valueMapper
     */
    public Binder(String name, Matcher<N> matcher, Function<N, ?> valueMapper) {
        this.name = name;
        this.matcher = matcher;
        this.valueMapper = valueMapper;
    }

    @Override
    public MatchResult<N> match(N node, MatchContext matchContext) {
        MatchResult<N> unboundResult = matcher.match(node, matchContext);
        if (unboundResult.isNotEmpty()) {
            Object value = valueMapper != null ? valueMapper.apply(unboundResult.getCurrentNode()) : unboundResult.getCurrentNode();
            return unboundResult.bind(name, value);
        } else {
            return unboundResult;
        }
    }
}
