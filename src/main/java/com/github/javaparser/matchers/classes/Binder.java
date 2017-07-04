package com.github.javaparser.matchers.classes;

import com.github.javaparser.ast.Node;
import com.github.javaparser.matchers.MatchContext;
import com.github.javaparser.matchers.MatchResult;
import com.github.javaparser.matchers.Matcher;
import com.google.common.collect.Collections2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class Binder<N extends Node> implements Matcher<N> {

    private String name;
    private Matcher<N> matcher;

    public Binder(String name, Matcher<N> matcher) {
        this.name = name;
        this.matcher = matcher;
    }

    @Override
    public MatchResult<N> match(N node, MatchContext matchContext) {
        MatchResult<N> unboundResult = matcher.match(node, matchContext);
        return unboundResult.bind(name);
    }
}
