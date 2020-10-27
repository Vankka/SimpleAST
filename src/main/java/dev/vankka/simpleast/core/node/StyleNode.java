package dev.vankka.simpleast.core.node;

import lombok.RequiredArgsConstructor;
import lombok.ToString;
import dev.vankka.simpleast.core.TextStyle;

import java.util.List;

/**
 * @param <RC> RenderContext
 * @param <T> Type of Style to apply
 */
@RequiredArgsConstructor
public class StyleNode<RC, T> extends Node<RC> {
    private final List<T> styles;

    public List<T> getStyles() {
        return styles;
    }

    @Override
    public String toString() {
        return "StyleNode{" +
                "styles=" + styles +
                ", children=" + getChildren() + "}";
    }

    public static class Companion {
        public static <RC> StyleNode<RC, TextStyle> createWithText(String content, List<TextStyle> styles) {
            StyleNode<RC, TextStyle> styleNode = new StyleNode<>(styles);
            styleNode.addChild(new TextNode<>(content));
            return styleNode;
        }
    }
}
