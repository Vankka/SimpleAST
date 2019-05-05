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
