package me.vankka.simpleast.core.parser;

import me.vankka.simpleast.core.ParseException;
import me.vankka.simpleast.core.node.Node;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Stack;
import java.util.regex.Matcher;

/**
 * @param <R> The render context, can be any object that holds what's required for rendering.
 * @param <T> The type of nodes that are handled.
 */
public class Parser<R, T extends Node<R>> {

    private final List<Rule<R, T>> rules = new ArrayList<>();

    public Parser<R, T> addRule(Rule<R, T> rule) {
        this.rules.add(rule);
        return this;
    }

    public Parser<R, T> addRules(Collection<Rule<R, T>> rules) {
        this.rules.addAll(rules);
        return this;
    }

    public List<Rule<R, T>> getRules() {
        return rules;
    }

    /**
     * Transforms the [source] to a AST of [Node]s using the global Parser.rules.
     *
     * @throws ParseException for certain specific error flows.
     */
    public List<T> parse(CharSequence source) {
        return parse(source, null);
    }

    /**
     * Transforms the source to a AST of {@link Node}s using the provided rules.
     *
     * @param rules Ordered List of rules to use to convert the source to nodes.
     *    If null, the parser will use its global list of Parser.rules.
     *
     * @throws ParseException for certain specific error flows.
     */
    public List<T> parse(CharSequence source, List<Rule<R, T>> rules) {
        if (rules == null)
            rules = this.rules;

        Stack<ParseSpec<R, T>> remainingParses = new Stack<>();
        List<T> topLevelNodes = new ArrayList<>();

        String lastCapture = null;

        if (source != null && !source.toString().isEmpty()) {
            remainingParses.add(new ParseSpec<>(null, 0, source.length()));
        }

        while (!remainingParses.isEmpty()) {
            ParseSpec<R, T> builder = remainingParses.pop();

            if (builder.getStartIndex() >= builder.getEndIndex()) {
                break;
            }

            CharSequence inspectionSource = source.subSequence(builder.getStartIndex(), builder.getEndIndex());
            int offset = builder.getStartIndex();

            boolean foundRule = false;
            for (Rule<R, T> rule : rules) {
                Matcher matcher = rule.match(inspectionSource, lastCapture);
                if (matcher != null) {
                    System.out.println("MATCH: with rule with pattern: " + rule.getMatcher().pattern().toString() + " to source: " + source);
                    int matcherSourceEnd = matcher.end() + offset;
                    foundRule = true;

                    ParseSpec<R, T> newBuilder = rule.parse(matcher, this);
                    T parent = builder.getRoot();

                    T newRoot = newBuilder.getRoot();
                    if (newRoot != null) {
                        if (parent != null) {
                            parent.addChild(newRoot);
                        } else {
                            topLevelNodes.add(newRoot);
                        }
                    }

                    // In case the last match didn't consume the rest of the source for this subtree,
                    // make sure the rest of the source is consumed.
                    if (matcherSourceEnd != builder.getEndIndex()) {
                        remainingParses.push(ParseSpec.createNonterminal(parent, matcherSourceEnd, builder.getEndIndex()));
                    }

                    // We want to speak in terms of indices within the source string,
                    // but the Rules only see the matchers in the context of the substring
                    // being examined. Adding this offset addresses that issue.
                    if (!newBuilder.isTerminal()) {
                        newBuilder.applyOffset(offset);
                        remainingParses.push(newBuilder);
                    }

                    try {
                        lastCapture = matcher.group(0);
                    } catch (Throwable throwable) {
                        throw new ParseException("matcher found no matches", source, throwable);
                    }

                    break;
                } else {
                    System.out.println("MISS: with rule with pattern: " + rule.getMatcher().pattern().toString() + " to source: " + source);
                }
            }

            if (!foundRule) {
                throw new ParseException("failed to find rule to match source: ", inspectionSource);
            }
        }

        return topLevelNodes;
    }
}
