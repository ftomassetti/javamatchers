package com.github.javaparser.matchersj.classes;

import com.github.javaparser.ast.Node;
import com.github.javaparser.matchersj.MatchContext;
import com.github.javaparser.matchersj.MatchResult;
import com.github.javaparser.matchersj.Matcher;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by djc3 on 6/14/17.
 */
public class AllOf<N extends Node> implements Matcher<N> {

    private List<Matcher<N>> elements = new ArrayList<>();

    public AllOf(Matcher<N>... elements) {
        this.elements = Arrays.asList(elements);
        if (elements.length == 0) {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public MatchResult<N> match(Node node, MatchContext matchContext) {
        return null;
    }
}
