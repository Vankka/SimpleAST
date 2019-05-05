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

package me.vankka.simpleast.core.node;

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
        return getClass().getSimpleName() + "[" + getChildren().size() + "]: " + content;
    }
}
