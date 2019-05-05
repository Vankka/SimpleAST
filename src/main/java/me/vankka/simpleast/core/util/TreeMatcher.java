/*
 * MCDiscordReserializer: A library for transcoding between Minecraft and Discord.
 * Copyright (C) 2019 Vankka
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package me.vankka.simpleast.core.util;

import me.vankka.simpleast.core.TextStyle;
import me.vankka.simpleast.core.node.Node;
import me.vankka.simpleast.core.node.StyleNode;
import me.vankka.simpleast.core.node.TextNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Compares two trees represented by {@link List}s of {@link Node}, primarily for testing
 * purposes.
 *
 * Clients can register {@link NodeMatcher}s to tell {@link TreeMatcher} how to
 * verify equality of their own custom nodes.
 */
public class TreeMatcher {

    private Map<Class, NodeMatcher> matchers = new HashMap<>();

    /**
     * @param tree1 first tree to consider.
     * @param tree2 second tree to consider.
     *
     * @return true if both trees are the same (same size, same classes of {@link Node},
     * and optionally that nodes match according to the provided matchers.
     */
    public boolean matches(final List<? extends Node> tree1, final List<? extends Node> tree2) {
        final List<Node> tree1PostOrder = new ArrayList<>();
        final List<Node> tree2PostOrder = new ArrayList<>();

        // Build a post-order walk of the first tree.
        ASTUtils.traversePostOrder(tree1, tree1PostOrder::add);

        // Build a post-order walk of the second tree.
        ASTUtils.traversePostOrder(tree2, tree2PostOrder::add);

        if (tree1PostOrder.size() != tree2PostOrder.size()) {
            return false;
        }

        // Proceed through the two walks step-by-step, comparing each
        // node along the way.
        for (int i = 0; i < tree1PostOrder.size(); i++) {
            final Node node1 = tree1PostOrder.get(i);
            final Node node2 = tree2PostOrder.get(i);

            if (node1.getClass() != node2.getClass()) {
                return false;
            }

            final Class clazz = node1.getClass();
            if (matchers.containsKey(clazz)) {
                if (!matchers.get(clazz).matches(node1, node2)) {
                    return false;
                }
            }
        }

        return true;
    }

    public void registerMatcher(final Class clazz, final NodeMatcher matcher) {
        matchers.put(clazz, matcher);
    }

    public void registerDefaultMatchers() {
        registerMatcher(TextNode.class, (node1, node2) -> {
            final TextNode textNode1 = (TextNode) node1;
            final TextNode textNode2 = (TextNode) node2;

            return textNode1.getContent().equals(textNode2.getContent());
        });

        registerMatcher(StyleNode.class, (node1, node2) -> {
            final StyleNode styleNode1 = (StyleNode) node1;
            final StyleNode styleNode2 = (StyleNode) node2;

            final List<TextStyle> styles1 = styleNode1.getStyles();
            final List<TextStyle> styles2 = styleNode2.getStyles();

            if (styles1.size() != styles2.size()) {
                return false;
            }

            for (int i = 0; i < styles1.size(); i++) {
                final TextStyle style1 = styles1.get(i);
                final TextStyle style2 = styles2.get(i);

                if (style1.getClass() != style2.getClass()) {
                    return false;
                }
            }

            return true;
        });
    }

    public interface NodeMatcher {
        boolean matches(Node node1, Node node2);
    }
}
