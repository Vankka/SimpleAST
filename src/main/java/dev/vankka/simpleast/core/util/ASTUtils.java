package dev.vankka.simpleast.core.util;

import dev.vankka.simpleast.core.node.Node;

import java.util.Collection;

@SuppressWarnings("unchecked")
public class ASTUtils {

    public static void traversePreOrder(final Collection<? extends Node> ast, final NodeProcessor nodeProcessor) {
        for (final Node node : ast) {
            traversePreOrderSubtree(node, nodeProcessor);
        }
    }

    private static void traversePreOrderSubtree(final Node node, final NodeProcessor nodeProcessor) {
        nodeProcessor.processNode(node);
        if (node.hasChildren()) {
            final Iterable<Node> children = node.getChildren();
            for (final Node child : children) {
                traversePreOrderSubtree(child, nodeProcessor);
            }
        }
    }

    public static void traversePostOrder(final Collection<? extends Node> ast, final NodeProcessor nodeProcessor) {
        for (final Node node : ast) {
            traversePostOrderSubtree(node, nodeProcessor);
        }
    }

    private static void traversePostOrderSubtree(final Node node, final NodeProcessor nodeProcessor) {
        if (node.hasChildren()) {
            final Iterable<Node> children = node.getChildren();
            for (final Node child : children) {
                traversePostOrderSubtree(child, nodeProcessor);
            }
        }

        nodeProcessor.processNode(node);
    }
}
