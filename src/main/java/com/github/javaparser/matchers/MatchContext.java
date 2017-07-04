package com.github.javaparser.matchers;

import java.util.*;

/**
 * This class will contained the matched node and possibly bound values.
 */
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

    public Object getBoundValue(String name) {
        if (!boundValues.containsKey(name)) {
            throw new IllegalArgumentException();
        }
        return boundValues.get(name);
    }

    public MatchContext bind(String name, Object value) {
        Map<String, Object> temp = new HashMap<>();
        temp.putAll(this.boundValues);
        temp.put(name, value);
        return new MatchContext(temp);
    }

    public MatchContext combine(MatchContext otherMc) {
        Set<String> tempSet = new HashSet<>(this.boundValues.keySet());
        tempSet.retainAll(otherMc.boundValues.keySet());
        if (tempSet.stream()
                   .anyMatch(m -> !this.boundValues.get(m).equals(otherMc.boundValues.get(m)))) {
            return null;
        }

        Map<String, Object> tempMap = new HashMap<>(this.boundValues);
        tempMap.putAll(otherMc.boundValues);
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

    @Override
    public int hashCode() {
        return boundValues.hashCode();
    }

    @Override
    public String toString() {
        return "MatchContext{" +
                "boundValues=" + boundValues +
                '}';
    }
}
