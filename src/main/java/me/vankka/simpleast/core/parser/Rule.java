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

import lombok.Data;
import me.vankka.simpleast.core.node.Node;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Data
public abstract class Rule<R, T extends Node<R>> {
    private final Matcher matcher;

    public Rule(Pattern pattern) {
        this.matcher = pattern.matcher("");
    }

    public Matcher match(CharSequence inspectionSource, String lastCapture) {
        matcher.reset(inspectionSource);
        return matcher.find() ? matcher : null;
    }

    public abstract ParseSpec<R, T> parse(Matcher matcher, Parser<R, T> parser);

    public abstract class BlockRule<R, T extends Node<R>> extends Rule<R, T> {

        public BlockRule(Pattern pattern) {
            super(pattern);
        }

        public Matcher match(CharSequence inspectionSource, String lastCapture) {
            if (lastCapture == null)
                return null;

            if (lastCapture.endsWith("\n")) {
                return super.match(inspectionSource, lastCapture);
            }
            return null;
        }
    }

}
