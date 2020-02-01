package dev.vankka.simpleast.core.node

import dev.vankka.simpleast.core.TextStyle

/**
 * @param RC RenderContext
 * @param T Type of Span to apply
 */
open class StyleNode<RC, T>(val styles: List<T>) : Node<RC>() {

  override fun toString() = "${javaClass.simpleName} >\n" +
      getChildren()?.joinToString("\n->", prefix = ">>", postfix = "\n>|") {
        it.toString()
      }

  companion object {

    /**
     * Convenience method for creating a [StyleNode] when we already know what
     * the text content will be.
     */
    @JvmStatic
    fun <RC> createWithText(content: String, styles: List<TextStyle>): StyleNode<RC, TextStyle> {
      val styleNode = StyleNode<RC, TextStyle>(styles)
      styleNode.addChild(TextNode(content))
      return styleNode
    }
  }
}
