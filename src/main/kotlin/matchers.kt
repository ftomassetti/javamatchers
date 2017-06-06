package com.github.javaparser.matchers

import com.github.javaparser.ast.Node
import com.github.javaparser.symbolsolver.javaparser.Navigator
import java.util.*

interface Matcher<N : Node> {
    fun match(node: N, matchContext: MatchContext = MatchContext()) : MatchResult<N>
}

data class MatchResult<N: Node>(val currentNode : N, val matches : List<MatchContext>) {

    companion object {
        fun <N: Node> empty(node: N) = MatchResult(node, emptyList())
        fun <N: Node> of(node: N, matchContext: MatchContext): MatchResult<N> {
            return MatchResult(node, listOf(matchContext))
        }
    }

    fun bind(name: String) : MatchResult<N> {
        return MatchResult(currentNode, matches.map { it.bind(name, currentNode) })
    }

    fun isEmpty() = matches.isEmpty()

    fun isNotEmpty() = matches.isNotEmpty()

    fun <N: Node> currentNode(node: N) : MatchResult<N> {
        return MatchResult(node, matches)
    }

    fun combine(other: MatchResult<N>): MatchResult<N> {
        if (this.isEmpty() || other.isEmpty()) {
            return empty(currentNode)
        }
        val combinations = HashSet<MatchContext?>()
        for (mine in matches) {
            for (its in other.matches) {
                combinations.add(mine.combine(its))
            }
        }
        return MatchResult(currentNode, combinations.filterNotNull().toList())
    }
}

class AllOf<N : Node>(vararg elements : Matcher<N>) : Matcher<N> {
    private val _elements = elements
    init {
        if (elements.size == 0) {
            throw IllegalArgumentException()
        }
    }
    override fun match(node: N, matchContext: MatchContext) : MatchResult<N> {
        val partialResults : List<MatchResult<N>> = _elements.map { it.match(node, matchContext).currentNode(node) }
        if (partialResults.any { it.isNotEmpty() }) {
            return MatchResult.empty(node)
        }
        return combine(partialResults)
    }

    private fun combine(partialResults: List<MatchResult<N>>) : MatchResult<N> {
        return combine(partialResults.first(), partialResults.takeLast(partialResults.size - 1))
    }

    private fun combine(matchResult: MatchResult<N>, partialResults: List<MatchResult<N>>) : MatchResult<N> {
        if (partialResults.isEmpty()) {
            return matchResult
        } else {
            return combine(matchResult.combine(partialResults.first()), partialResults.takeLast(partialResults.size - 1))
        }
    }
}

class AnyOf<N : Node>(vararg elements : Matcher<N>) : Matcher<N> {
    private val _elements = elements
    override fun match(node: N, matchContext: MatchContext) : MatchResult<N> {
        return _elements
                .map { it.match(node, matchContext).currentNode(node) }
                .firstOrNull { it.isNotEmpty() } ?: MatchResult.empty(node)
    }
}

class Is<N : Node, T : Node>(val type: Class<T>) : Matcher<N> {
    override fun match(node: N, matchContext: MatchContext): MatchResult<N> {
        if (type.isInstance(node)) {
            return MatchResult.of(node, matchContext)
        } else {
            return MatchResult.empty(node)
        }
    }

}

class Parent<N : Node>(val parentMatcher: Matcher<Node>) : Matcher<N> {

    override fun match(node: N, matchContext: MatchContext) : MatchResult<N> {
        if (node.parentNode.isPresent) {
            return parentMatcher.match(node.parentNode.get(), matchContext).currentNode(node)
        } else {
            return MatchResult.empty(node)
        }
    }

}

class HasChild<N : Node>(val childMatcher: Matcher<Node>) : Matcher<N> {

    override fun match(node: N, matchContext: MatchContext) : MatchResult<N> {
        return node.childNodes.map { childMatcher.match(it, matchContext).currentNode(node) }
                .firstOrNull() ?: MatchResult.empty(node)
    }

}

class HasDescendant<N : Node>(val descendantMatcher: Matcher<Node>) : Matcher<N> {

    override fun match(node: N, matchContext: MatchContext) : MatchResult<N> {
        return descendants(node).map { descendantMatcher.match(it, matchContext).currentNode(node) }
                .firstOrNull() ?: MatchResult.empty(node)
    }

    private fun descendants(node: Node) : List<Node> {
        val descendants = LinkedList<Node>()
        descendants.addAll(node.childNodes)
        node.childNodes.forEach {
            descendants.addAll(descendants(it))
        }
        return descendants
    }

}

/**
 * This class will contained the matched node and possibly bound values
 */
data class MatchContext(val boundValues : Map<String, Any> = emptyMap()) {

    companion object {
        fun empty() = MatchContext()
    }

    fun bind(name: String, value: Any) : MatchContext {
        return MatchContext(boundValues.plus(Pair(name, value)))
    }

    fun combine(other: MatchContext): MatchContext? {
        if (this.boundValues.keys.intersect(other.boundValues.keys).any {
            boundValues[it] != other.boundValues[it]
        }) {
            return null
        }
        var combinedBoundValues = boundValues
        for (newKey in other.boundValues.keys.filter { !other.boundValues.keys.contains(it) }) {
            combinedBoundValues = combinedBoundValues.plus(Pair(newKey, other.boundValues[newKey]!!))
        }
        return MatchContext(combinedBoundValues)
    }

}

fun <N: Node> match(ast: Node, nodeClass: Class<N>, matcher: Matcher<N>) : List<MatchResult<N>> {
    return Navigator.findAllNodesOfGivenClass(ast, nodeClass)
            .map { matcher.match(it) }
            .filter { it.isNotEmpty() }
}

fun match(ast: Node, matcher: Matcher<Node>) : List<MatchResult<Node>> {
    return match(ast, Node::class.java, matcher)
}
