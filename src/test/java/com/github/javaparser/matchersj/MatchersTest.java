package com.github.javaparser.matchersj;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.BooleanLiteralExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.stmt.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

import static com.github.javaparser.matchersj.Matchers.*;

/**
 * Created by cdo on 6/15/17.
 */
public class MatchersTest {
    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public  void testIsMatcher() {
        Node ast = JavaParser.parseStatement("if (true) { }");
        List<MatchResult<Node>> matches = match(ast, Is(IfStmt.class));
        assertEquals(1, matches.size());
        assertTrue(matches.stream().anyMatch(mr -> IfStmt.class.isInstance(mr.getCurrentNode())));
    }

    @Test
    public void findAllStatementsWhichAreAForEach() {
        Node ast = JavaParser.parse(  "class Foo {"
                + "    public void a() {"
                + "        while (true) {"
                + "            a.b(filter());"
                + "        }"
                + "    }"
                + "}");
        List<MatchResult<Node>> matches = match(ast, Is(ForeachStmt.class));
        assertTrue(matches.isEmpty());
    }

    @Test
    public void findAllStatementsWhichAreAWhile() {
        Node ast = JavaParser.parse(
                "class Foo {"
                        + "    public void a() {"
                        + "       while (true) {"
                        + "           a.b(filter());"
                        + "       }"
                        + "    }"
                        + "}");
        List<MatchResult<Node>> matches = match(ast, Is(WhileStmt.class));
        assertEquals(1, matches.size());
        assertTrue(WhileStmt.class.isInstance(matches.get(0).getCurrentNode()));
        assertEquals(matches.get(0).getMatches(), Arrays.asList(MatchContext.empty()));
    }

    @Test
    public void findAllStatementsWhichHaveAsParentAWhile() {
        Node ast = JavaParser.parse(
            "class Foo {"
            + "    public void a() {"
            + "        while (true) {"
            + "            a.b(filter());"
            + "        }"
            + "    }"
            + "}");
        List<MatchResult<Node>> matches = match(ast, Parent(Is(WhileStmt.class)));
        assertEquals(2, matches.size());
        assertTrue(matches.stream().anyMatch(mr -> BooleanLiteralExpr.class.isInstance(mr.getCurrentNode())));
        assertTrue(matches.stream().anyMatch(mr -> BlockStmt.class.isInstance(mr.getCurrentNode())));
    }

    @Test
    public void findAllStatementsWhichHaveAsParentAWhileOrAFor() {
        Node ast = JavaParser.parse(
     "class Foo {"
        + "    public void a() {"
        + "        while (true) {"
        + "            a.b(filter());"
        + "        }"
        + "    }"
        + "}");
        List<MatchResult<Node>> matches = match(ast,
                AnyOf(Parent(Is(WhileStmt.class)),
                      Parent(Is(ForStmt.class)),
                      Parent(Is(ForeachStmt.class))));
        assertEquals(2, matches.size());
        assertTrue(matches.stream().anyMatch(mr -> BooleanLiteralExpr.class.isInstance(mr.getCurrentNode())));
        assertTrue(matches.stream().anyMatch(mr -> BlockStmt.class.isInstance(mr.getCurrentNode())));
    }

    @Test
    public void findAllStatementsInsideAWhileOrAForWhichAreCallsToMethodNamesFilter() {
        Node ast = JavaParser.parse(
                "class Foo {"
                        + "    public void a() {"
                        + "        while (true) {"
                        + "            a.b(filter());"
                        + "        }"
                        + "    }"
                        + "}");
        List<MatchResult<Node>> matches = match(ast, AllOf(
                                                        AnyOf(Parent(Is(WhileStmt.class)),
                                                              Parent(Is(ForStmt.class)),
                                                              Parent(Is(ForeachStmt.class))
                                                        ),
                                                        HasDescendant(Is(MethodCallExpr.class))
                                                    )
                                                );
        assertEquals(2, matches.size());
        assertTrue(matches.stream().anyMatch(mr -> BooleanLiteralExpr.class.isInstance(mr.getCurrentNode())));
        assertTrue(matches.stream().anyMatch(mr -> BlockStmt.class.isInstance(mr.getCurrentNode())));
    }
}