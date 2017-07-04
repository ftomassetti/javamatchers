package com.github.javaparser.matchers;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class MatchContext {
    private Map<String, Object> boundValues;

    public MatchContext() {
        this(Collections.emptyMap());
    }

    public MatchContext(Map<String, Object> boundValues) {
        this.boundValues = boundValues;
    }

    public static MatchContext empty() {
        return new MatchContext();
    }

    public MatchContext bind(String name, Object value) {
        Map<String, Object> temp = Collections.emptyMap();
        temp.putAll(this.boundValues);
        temp.put(name, value);
        return new MatchContext(temp);
    }

    public MatchContext combine(MatchContext otherMc) {
        Set<?> tempSet = this.boundValues.keySet();
        tempSet.retainAll(otherMc.boundValues.keySet());
        if (tempSet.stream()
                   .anyMatch(m -> this.boundValues.get(m) != otherMc.boundValues.get(m)))
        {
            return null;
        }

        Map<String, Object> tempMap = this.boundValues;

        for(String s : otherMc.boundValues.keySet()
                                          .stream()
                                          .filter(k -> !otherMc.boundValues.keySet().contains(k))
                                          .collect(Collectors.toList())) {
            tempMap.put(s, otherMc.boundValues.get(s));
        }

        return new MatchContext(tempMap);
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof MatchContext)) {
            return false;
        }

        MatchContext other = (MatchContext)obj;
        Set<String> thisKeys = this.boundValues.keySet(),
                    otherKeys = other.boundValues.keySet();

        if(thisKeys.size() != otherKeys.size() || !thisKeys.containsAll(otherKeys)) {
            return false;
        }

        return thisKeys.stream().allMatch(m -> this.boundValues.get(m).equals(other.boundValues.get(m)));
    }
}
