# JavaMatchers

[![Build Status](https://travis-ci.org/ftomassetti/javamatchers.svg?branch=master)](https://travis-ci.org/ftomassetti/javamatchers)

A library to identify patterns in Java code. Based on [JavaParser](https://javaparser.org) and [JavaSymbolSolver](https://github.com/javaparser/javasymbolsolver).

For example, this code cand be used to find all bean properties, i.e., all sets of a field, a getter and a setter with corresponding names and type:

```java
List<MatchResult<Node>> matches = match(bean1,
        allOf(
                isClass(),
                anyChild(new Binder<>("type",
                                new Binder<>("name",
                                        is(FieldDeclaration.class,
                                                f -> f.isPrivate()
                                                && !f.isStatic()
                                                && f.getVariables().size() == 1),
                                        f -> ((FieldDeclaration)f).getVariables().get(0).getName().getIdentifier()),
                                f -> ((FieldDeclaration)f).getVariables().get(0).getType())),
                anyChild(new Binder<>("type",
                                new Binder<>("name",
                                        is(MethodDeclaration.class, m -> m.isPublic() && !m.isStatic() && m.getParameters().isEmpty()),
                                        getterNameToPropertyName),
                                m -> ((MethodDeclaration)m).getType())),
                anyChild(new Binder<>("type",
                                new Binder<>("name",
                                        is(MethodDeclaration.class, m -> m.isPublic() && !m.isStatic() && m.getParameters().size() == 1 && m.getType() instanceof VoidType),
                                        setterNameToPropertyName),
                                m -> ((MethodDeclaration)m).getParameter(0).getType()))
        ));
```
