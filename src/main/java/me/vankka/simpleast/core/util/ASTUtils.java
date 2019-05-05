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

import me.vankka.simpleast.core.node.Node;

import java.util.Collection;

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
