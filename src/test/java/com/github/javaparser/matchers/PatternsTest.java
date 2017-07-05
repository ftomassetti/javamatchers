package com.github.javaparser.matchers;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.type.VoidType;
import com.github.javaparser.matchers.classes.Binder;
import com.github.javaparser.utils.StringEscapeUtils;
import com.github.javaparser.utils.Utils;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.function.Function;

import static com.github.javaparser.matchers.Matchers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PatternsTest {

    private CompilationUnit bean1;

    @Before
    public void setup() {
        bean1 = JavaParser.parse(this.getClass().getResourceAsStream("/Bean1.java"));
    }

    @Test
    public void testFindFields() {
        List<MatchResult<Node>> matches = match(bean1,
                allOf(
                        isClass(),
                        anyChild(new Binder<>("field", is(FieldDeclaration.class, f -> f.isPrivate() && !f.isStatic())))
        ));
        assertEquals(1, matches.size());
        assertEquals(3, matches.get(0).getMatches().size());
        assertTrue(matches.get(0).getMatches().stream().anyMatch(matchContext -> matchContext.getBoundValue("field") == bean1.getClassByName("A").get().getFieldByName("foo").get()));
        assertTrue(matches.get(0).getMatches().stream().anyMatch(matchContext -> matchContext.getBoundValue("field") == bean1.getClassByName("A").get().getFieldByName("bar").get()));
        assertTrue(matches.get(0).getMatches().stream().anyMatch(matchContext -> matchContext.getBoundValue("field") == bean1.getClassByName("A").get().getFieldByName("zum").get()));
    }

    @Test
    public void testFindGetters() {
        List<MatchResult<Node>> matches = match(bean1,
                allOf(
                        isClass(),
                        anyChild(new Binder<>("getter", is(MethodDeclaration.class, m -> m.isPublic() && !m.isStatic() && m.getParameters().isEmpty())))
                ));
        assertEquals(1, matches.size());
        assertEquals(3, matches.get(0).getMatches().size());
        assertTrue(matches.get(0).getMatches().stream().anyMatch(matchContext -> matchContext.getBoundValue("getter") == bean1.getClassByName("A").get().getMethodsByName("getFoo").get(0)));
        assertTrue(matches.get(0).getMatches().stream().anyMatch(matchContext -> matchContext.getBoundValue("getter") == bean1.getClassByName("A").get().getMethodsByName("getBar").get(0)));
        assertTrue(matches.get(0).getMatches().stream().anyMatch(matchContext -> matchContext.getBoundValue("getter") == bean1.getClassByName("A").get().getMethodsByName("getZum").get(0)));
    }

    @Test
    public void testFindSetters() {
        List<MatchResult<Node>> matches = match(bean1,
                allOf(
                        isClass(),
                        anyChild(new Binder<>("setter", is(MethodDeclaration.class, m -> m.isPublic() && !m.isStatic() && m.getParameters().size() == 1 && m.getType() instanceof VoidType)))
                ));
        assertEquals(1, matches.size());
        assertEquals(3, matches.get(0).getMatches().size());
        assertTrue(matches.get(0).getMatches().stream().anyMatch(matchContext -> matchContext.getBoundValue("setter") == bean1.getClassByName("A").get().getMethodsByName("setFoo").get(0)));
        assertTrue(matches.get(0).getMatches().stream().anyMatch(matchContext -> matchContext.getBoundValue("setter") == bean1.getClassByName("A").get().getMethodsByName("setBar").get(0)));
        assertTrue(matches.get(0).getMatches().stream().anyMatch(matchContext -> matchContext.getBoundValue("setter") == bean1.getClassByName("A").get().getMethodsByName("setZum").get(0)));
    }

    @Test
    public void testPropertyPattern() {
        Function<Node, String> getterNameToPropertyName = m -> {
            String getterName = ((MethodDeclaration)m).getName().getIdentifier();
            String res = (getterName.startsWith("get") && getterName.length() > "get".length()) ? Utils.decapitalize(getterName.substring("get".length())) : null;
            return res;
        };
        Function<Node, String> setterNameToPropertyName = m -> {
            String getterName = ((MethodDeclaration)m).getName().getIdentifier();
            return (getterName.startsWith("set") && getterName.length() > "set".length()) ? Utils.decapitalize(getterName.substring("set".length())) : null;
        };
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
        assertEquals(1, matches.size());
        assertEquals(3, matches.get(0).getMatches().size());
        MatchContext matchContext;
        matchContext = matches.get(0).getMatches().stream().filter(e -> e.getBoundValue("name").equals("foo")).findFirst().get();
        assertEquals("int", matchContext.getBoundValue("type").toString());
        matchContext = matches.get(0).getMatches().stream().filter(e -> e.getBoundValue("name").equals("bar")).findFirst().get();
        assertEquals("String", matchContext.getBoundValue("type").toString());
        matchContext = matches.get(0).getMatches().stream().filter(e -> e.getBoundValue("name").equals("zum")).findFirst().get();
        assertEquals("List<Double>", matchContext.getBoundValue("type").toString());
    }
}
