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

package me.vankka.simpleast.core.parser;

import me.vankka.simpleast.core.node.Node;

public class ParseSpec<R, T extends Node<R>> {
    private T root;
    private boolean isTerminal;
    private int startIndex = 0;
    private int endIndex = 0;

    public ParseSpec(T root, int startIndex, int endIndex) {
        this.root = root;
        this.isTerminal = false;
        this.startIndex = startIndex;
        this.endIndex = endIndex;
    }

    private ParseSpec(T root) {
        this.root = root;
        this.isTerminal = true;
    }

    public void applyOffset(int offset) {
        startIndex += offset;
        endIndex += offset;
    }

    public T getRoot() {
        return root;
    }

    public boolean isTerminal() {
        return isTerminal;
    }

    public int getStartIndex() {
        return startIndex;
    }

    public int getEndIndex() {
        return endIndex;
    }

    public static <R, T extends Node<R>> ParseSpec<R, T> createNonterminal(T node, int startIndex, int endIndex) {
        return new ParseSpec<>(node, startIndex, endIndex);
    }

    public static <R, T extends Node<R>> ParseSpec<R, T> createTerminal(T node) {
        return new ParseSpec<>(node);
    }
}
