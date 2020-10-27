package dev.vankka.simpleast.core.node;

import lombok.RequiredArgsConstructor;

/**
 * Node representing simple text.
 */
@RequiredArgsConstructor
public class TextNode<R> extends Node<R> {
    private final String content;

    public String getContent() {
        return content;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{content=" + content + ", children=" + getChildren() + "}";
    }
}
