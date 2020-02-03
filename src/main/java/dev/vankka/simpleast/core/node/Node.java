package dev.vankka.simpleast.core.node;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a single node in an Abstract Syntax Tree. It can (but does not need to) have children.
 *
 * @param <R> The render context, can be any object that holds what's required for rendering. See [render].
 */
public abstract class Node<R> {

    private final List<Node<R>> children = new ArrayList<>();

    public List<Node<R>> getChildren() {
        return children;
    }

    public boolean hasChildren() {
        return !children.isEmpty();
    }

    public void addChild(Node<R> child) {
        this.children.add(child);
    }
}
