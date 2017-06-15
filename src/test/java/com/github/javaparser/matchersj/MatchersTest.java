package com.github.javaparser.matchersj;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.stmt.IfStmt;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

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
        List<MatchResult<Node>> match = match(ast, Is(IfStmt.class));
        assertEquals(1, match.size());
        assertTrue(match.stream().anyMatch(mr -> IfStmt.class.isInstance(mr.getCurrentNode())));
    }

}