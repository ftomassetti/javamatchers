package com.github.javaparser.matchers;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.BooleanLiteralExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.stmt.*;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

import static com.github.javaparser.matchers.Matchers.*;

public class MatchersTest {

    @Test
    public  void testIsMatcher() {
        Node ast = JavaParser.parseStatement("if (true) { }");
        List<MatchResult<Node>> matches = match(ast, is(IfStmt.class));
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
        List<MatchResult<Node>> matches = match(ast, is(ForeachStmt.class));
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
        List<MatchResult<Node>> matches = match(ast, is(WhileStmt.class));
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
        List<MatchResult<Node>> matches = match(ast, parent(is(WhileStmt.class)));
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
                anyOf(parent(is(WhileStmt.class)),
                      parent(is(ForStmt.class)),
                      parent(is(ForeachStmt.class))));
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
        List<MatchResult<Node>> matches = match(ast, allOf(
                                                        anyOf(parent(is(WhileStmt.class)),
                                                              parent(is(ForStmt.class)),
                                                              parent(is(ForeachStmt.class))
                                                        ),
                                                        hasDescendant(is(MethodCallExpr.class))
                                                    )
                                                );
//        assertEquals(2, matches.size());
//        assertTrue(matches.stream().anyMatch(mr -> BooleanLiteralExpr.class.isInstance(mr.getCurrentNode())));
//        assertTrue(matches.stream().anyMatch(mr -> BlockStmt.class.isInstance(mr.getCurrentNode())));
    }
}
