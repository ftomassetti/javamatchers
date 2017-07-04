package com.github.javaparser.matchers;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.type.VoidType;
import com.github.javaparser.matchers.classes.Binder;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static com.github.javaparser.matchers.Matchers.*;

public class PatternsTest {

    private CompilationUnit bean1;

    @Before
    public void setup() {
        bean1 = JavaParser.parse(this.getClass().getResourceAsStream("/Bean1.java"));
    }

    @Test
    public void testPropertyPattern() {
        List<MatchResult<Node>> matches = match(bean1,
                allOf(
                        isClass(),
                        anyChild(new Binder<>("field", is(FieldDeclaration.class, f -> f.isPrivate() && !f.isStatic()))),
                        anyChild(new Binder<>("getter", is(MethodDeclaration.class, m -> m.isPublic() && !m.isStatic() && m.getParameters().isEmpty()))),
                        hasChild(is(MethodDeclaration.class, m -> m.isPublic() && !m.isStatic() && m.getParameters().size() == 1 && m.getType() instanceof VoidType)
                ))
        );
        System.out.println("Matches: " + matches);
    }
}
