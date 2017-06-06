import com.github.javaparser.JavaParser
import com.github.javaparser.ast.expr.BooleanLiteralExpr
import com.github.javaparser.ast.expr.MethodCallExpr
import com.github.javaparser.ast.stmt.BlockStmt
import com.github.javaparser.ast.stmt.ForStmt
import com.github.javaparser.ast.stmt.ForeachStmt
import com.github.javaparser.ast.stmt.WhileStmt
import com.github.javaparser.matchers.*
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import org.junit.Test as test

class MatcherTests {

    @test fun findAllStatementsWhichAreAForEach() {
        val ast = JavaParser.parse("""
        class Foo {
            public void a() {
               while (true) {
                   a.b(filter());
               }
            }
        }
    """)
        val match = match(ast,
                Is(ForeachStmt::class.java))
        assertEquals(true, match.isEmpty())
    }

    @test fun findAllStatementsWhichAreAWhile() {
        val ast = JavaParser.parse("""
        class Foo {
            public void a() {
               while (true) {
                   a.b(filter());
               }
            }
        }
    """)
        val match = match(ast,
                Is(WhileStmt::class.java))
        assertEquals(1, match.size)
        assert(match[0].currentNode is WhileStmt)
        assertEquals(match[0].matches, listOf(MatchContext.empty()))
    }

    @test fun findAllStatementsWhichHaveAsParentAWhile() {
        val ast = JavaParser.parse("""
        class Foo {
            public void a() {
               while (true) {
                   a.b(filter());
               }
            }
        }
    """)
        val match = match(ast,
                Parent(Is(WhileStmt::class.java)))
        assertEquals(2, match.size)
        assertNotNull(match.first { it.currentNode is BooleanLiteralExpr })
        assertNotNull(match.first { it.currentNode is BlockStmt })
    }

    @test fun findAllStatementsWhichHaveAsParentAWhileOrAFor() {
        val ast = JavaParser.parse("""
        class Foo {
            public void a() {
               while (true) {
                   a.b(filter());
               }
            }
        }
    """)
        val match = match(ast,
                AnyOf(
                    Parent(Is(WhileStmt::class.java)),
                    Parent(Is(ForStmt::class.java)),
                    Parent(Is(ForeachStmt::class.java))))
        assertEquals(2, match.size)
        assertNotNull(match.first { it.currentNode is BooleanLiteralExpr })
        assertNotNull(match.first { it.currentNode is BlockStmt })
    }

    @test fun findAllStatementsInsideAWhileOrAForWhichAreCallsToMethodNamesFilter() {
        val ast = JavaParser.parse("""
        class Foo {
            public void a() {
               while (true) {
                   a.b(filter());
               }
            }
        }
    """)
        val match = match(ast,
                AllOf(
                    AnyOf(
                        Parent(Is(WhileStmt::class.java)),
                        Parent(Is(ForStmt::class.java)),
                        Parent(Is(ForeachStmt::class.java))),
                    HasDescendant(Is(MethodCallExpr::class.java))
        ))
        assertEquals(2, match.size)
        assertNotNull(match.first { it.currentNode is BooleanLiteralExpr })
        assertNotNull(match.first { it.currentNode is BlockStmt })
    }

}