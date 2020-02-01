package dev.vankka.simpleast.core.parser

import dev.vankka.simpleast.core.node.Node

/**
 * Facilitates fast parsing of the source text.
 *
 *
 * For nonterminal subtrees, the provided root will be added to the main, and text between
 * startIndex (inclusive) and endIndex (exclusive) will continue to be parsed into Nodes and
 * added as children under this root.
 *
 *
 * For terminal subtrees, the root will simply be added to the tree and no additional parsing will
 * take place on the text.
 *
 * @param R The type of render context needed by the node that this contains.
 * @param T The type of node that this contains.
 * @param S The type of state that child nodes will use. This is mainly used to just pass through
 *          the state back to the parser.
 */
class ParseSpec<R, T : Node<R>, S> {
  val root: T?
  val isTerminal: Boolean
  val state: S
  var startIndex: Int = 0
  var endIndex: Int = 0

  constructor(root: T?, state: S, startIndex: Int, endIndex: Int) {
    this.root = root
    this.state = state
    this.isTerminal = false
    this.startIndex = startIndex
    this.endIndex = endIndex
  }

  constructor(root: T?, state: S) {
    this.root = root
    this.state = state
    this.isTerminal = true
  }

  fun applyOffset(offset: Int) {
    startIndex += offset
    endIndex += offset
  }

  companion object {

    @JvmStatic
    fun <R, T : Node<R>, S> createNonterminal(node: T?, state: S, startIndex: Int, endIndex: Int): ParseSpec<R, T, S> {
      return ParseSpec(node, state, startIndex, endIndex)
    }

    @JvmStatic
    fun <R, T : Node<R>, S> createTerminal(node: T?, state: S): ParseSpec<R, T, S> {
      return ParseSpec(node, state)
    }
  }
}

