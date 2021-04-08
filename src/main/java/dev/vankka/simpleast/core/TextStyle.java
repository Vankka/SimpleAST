package dev.vankka.simpleast.core;

import dev.vankka.simpleast.core.simple.SimpleMarkdownRules;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("WeakerAccess")
public class TextStyle {

    private final Type type;
    private final Map<String, String> extra = new HashMap<>();

    public TextStyle(Type type) {
        this(type, Collections.emptyMap());
    }

    public TextStyle(Type type, Map<String, String> extra) {
        this.type = type;
        extra.forEach(this.extra::put);
    }

    public Type getType() {
        return type;
    }

    public Map<String, String> getExtra() {
        return extra;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof TextStyle)) {
            return false;
        }

        TextStyle other = (TextStyle) obj;
        return other.getType() == type;
    }

    @Override
    public String toString() {
        return "TextStyle(" + type + ") [" + extra + "]";
    }

    @SuppressWarnings("unused")
    public enum Type {
        /* Basic Markdown: */ /** {@link SimpleMarkdownRules} */
        BOLD,
        UNDERLINE,
        ITALICS,
        STRIKETHROUGH,

        /* Mentions */
        MENTION_EMOJI,
        MENTION_CHANNEL,
        MENTION_USER,
        MENTION_ROLE,

        /* Discord special */
        SPOILER,
        QUOTE,
        CODE_STRING,
        CODE_BLOCK
    }
}
