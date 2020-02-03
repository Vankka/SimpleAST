package dev.vankka.simpleast.core.parser;

import lombok.Data;
import dev.vankka.simpleast.core.node.Node;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Data
public abstract class Rule<R, T extends Node<R>, S> {
    private final Matcher matcher;

    public Rule(Pattern pattern) {
        this.matcher = pattern.matcher("");
    }

    public Matcher match(CharSequence inspectionSource, String lastCapture, S state) {
        matcher.reset(inspectionSource);
        return matcher.find() ? matcher : null;
    }

    public abstract ParseSpec<R, T, S> parse(Matcher matcher, Parser<R, T, S> parser, S state);

    public abstract class BlockRule<R, T extends Node<R>, S> extends Rule<R, T, S> {

        public BlockRule(Pattern pattern) {
            super(pattern);
        }

        public Matcher match(CharSequence inspectionSource, String lastCapture, S state) {
            if (lastCapture == null)
                return null;

            if (lastCapture.endsWith("\n")) {
                return super.match(inspectionSource, lastCapture, state);
            }
            return null;
        }
    }

}
