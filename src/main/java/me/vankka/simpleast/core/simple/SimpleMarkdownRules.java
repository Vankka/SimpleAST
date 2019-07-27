package me.vankka.simpleast.core.simple;

import me.vankka.simpleast.core.TextStyle;
import me.vankka.simpleast.core.node.Node;
import me.vankka.simpleast.core.node.StyleNode;
import me.vankka.simpleast.core.node.TextNode;
import me.vankka.simpleast.core.parser.ParseSpec;
import me.vankka.simpleast.core.parser.Parser;
import me.vankka.simpleast.core.parser.Rule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SimpleMarkdownRules {

    private static final Pattern PATTERN_BOLD = Pattern.compile("^\\*\\*([\\s\\S]+?)\\*\\*(?!\\*)");
    private static final Pattern PATTERN_UNDERLINE = Pattern.compile("^__([\\s\\S]+?)__(?!_)");
    private static final Pattern PATTERN_STRIKETHRU = Pattern.compile("^~~(?=\\S)([\\s\\S]*?\\S)~~");
    private static final Pattern PATTERN_NEWLINE = Pattern.compile("^(?:\n *)*\n", Pattern.LITERAL);
    private static final Pattern PATTERN_TEXT = Pattern.compile("^[\\s\\S]+?(?=[^0-9A-Za-z\\s\\u00c0-\\uffff]|\\n| {2,}\\n|\\w+:\\S|$)");
    private static final Pattern PATTERN_ESCAPE = Pattern.compile("^\\\\([^0-9A-Za-z\\s])");

    private static final Pattern PATTERN_ITALICS = Pattern.compile(
            // only match _s surrounding words.
            "^\\b_" + "((?:__|\\\\[\\s\\S]|[^\\\\_])+?)_" + "\\b" +
                    "|" +
                    // Or match *s that are followed by a non-space:
                    "^\\*(?=\\S)(" +
                    // Match any of:
                    //  - `**`: so that bolds inside italics don't close the
                    // italics
                    //  - whitespace
                    //  - non-whitespace, non-* characters
                    "(?:\\*\\*|\\s+(?:[^*\\s]|\\*\\*)|[^\\s*])+?" +
                    // followed by a non-space, non-* then *
                    ")\\*(?!\\*)"
    );

    private static <R, S> Rule<R, Node<R>, S> createBoldRule() {
        return createSimpleStyleRule(PATTERN_BOLD, TextStyle.BOLD);
    }

    private static <R, S> Rule<R, Node<R>, S> createUnderlineRule() {
        return createSimpleStyleRule(PATTERN_UNDERLINE, TextStyle.UNDERLINE);
    }

    private static <R, S> Rule<R, Node<R>, S> createStrikethruRule() {
        return createSimpleStyleRule(PATTERN_STRIKETHRU, TextStyle.STRIKETHROUGH);
    }

    private static <R, S> Rule<R, Node<R>, S> createTextRule() {
        return new Rule<R, Node<R>, S>(PATTERN_TEXT) {

            @Override
            public ParseSpec<R, Node<R>, S> parse(Matcher matcher, Parser<R, Node<R>, S> parser, S state) {
                return ParseSpec.createTerminal(new TextNode<>(matcher.group()), state);
            }
        };
    }

    private static <R, S> Rule<R, Node<R>, S> createNewlineRule() {
        return new Rule<R, Node<R>, S>(PATTERN_NEWLINE) {

            @Override
            public ParseSpec<R, Node<R>, S> parse(Matcher matcher, Parser<R, Node<R>, S> parser, S state) {
                return ParseSpec.createTerminal(new TextNode<>("\n"), state);
            }
        };
    }

    private static <R, S> Rule<R, Node<R>, S> createEscapeRule() {
        return new Rule<R, Node<R>, S>(PATTERN_ESCAPE) {

            @Override
            public ParseSpec<R, Node<R>, S> parse(Matcher matcher, Parser<R, Node<R>, S> parser, S state) {
                return ParseSpec.createTerminal(new TextNode<>(matcher.group(1)), state);
            }
        };
    }

    private static <R, S> Rule<R, Node<R>, S> createItalicsRule() {
        return new Rule<R, Node<R>, S>(PATTERN_ITALICS) {

            @Override
            public ParseSpec<R, Node<R>, S> parse(Matcher matcher, Parser<R, Node<R>, S> parser, S state) {
                int startIndex;
                int endIndex;
                String asteriskMatch = matcher.group(2);
                if (asteriskMatch != null && asteriskMatch.length() > 0) {
                    startIndex = matcher.start(2);
                    endIndex = matcher.end(2);
                } else {
                    startIndex = matcher.start(1);
                    endIndex = matcher.end(1);
                }

                List<TextStyle> styles = new ArrayList<>(Collections.singletonList(TextStyle.ITALICS));
                return ParseSpec.createNonterminal(new StyleNode<>(styles), state, startIndex, endIndex);
            }
        };
    }

    private static <R, S> Rule<R, Node<R>, S> createSimpleStyleRule(Pattern pattern, TextStyle textStyle) {
        return new Rule<R, Node<R>, S>(pattern) {

            @Override
            public ParseSpec<R, Node<R>, S> parse(Matcher matcher, Parser<R, Node<R>, S> parser, S state) {
                return ParseSpec.createNonterminal(new StyleNode<>(new ArrayList<>(Collections.singletonList(textStyle))), state, matcher.start(1), matcher.end(1));
            }
        };
    }

    public static <R, S> List<Rule<R, Node<R>, S>> createSimpleMarkdownRules(boolean includeTextRule) {
        List<Rule<R, Node<R>, S>> rules = new ArrayList<>();
        rules.add(createEscapeRule());
        rules.add(createNewlineRule());
        rules.add(createBoldRule());
        rules.add(createUnderlineRule());
        rules.add(createItalicsRule());
        rules.add(createStrikethruRule());
        if (includeTextRule) {
            rules.add(createTextRule());
        }
        return rules;
    }
}
