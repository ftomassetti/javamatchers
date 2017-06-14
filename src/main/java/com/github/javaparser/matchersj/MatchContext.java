package com.github.javaparser.matchersj;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by djc3 on 6/13/17.
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
}
