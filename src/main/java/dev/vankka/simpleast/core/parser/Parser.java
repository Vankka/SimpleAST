package dev.vankka.simpleast.core.parser;

import dev.vankka.simpleast.core.ParseException;
import dev.vankka.simpleast.core.node.Node;

import java.util.*;
import java.util.regex.Matcher;

/**
 * @param <R> The render context, can be any object that holds what's required for rendering.
 * @param <T> The type of nodes that are handled.
 */
public class Parser<R, T extends Node<R>, S> {

    private final List<Rule<R, T, S>> rules = new ArrayList<>();
    private final boolean enableDebugging;

    public Parser(boolean enableDebugging) {
        this.enableDebugging = enableDebugging;
    }

    public Parser() {
        this.enableDebugging = false;
    }

    public Parser<R, T, S> addRule(Rule<R, T, S> rule) {
        this.rules.add(rule);
        return this;
    }

    public Parser<R, T, S> addRules(Collection<Rule<R, T, S>> rules) {
        this.rules.addAll(rules);
        return this;
    }

    public Parser<R, T, S> removeRule(Rule<R, T, S> rule) {
        this.rules.remove(rule);
        return this;
    }

    public Parser<R, T, S> removeRules(Collection<Rule<R, T, S>> rules) {
        this.rules.removeAll(rules);
        return this;
    }

    /**
     * @return immutable list of rules that this Parser uses by default
     */
    public List<Rule<R, T, S>> getRules() {
        return Collections.unmodifiableList(rules);
    }

    public List<T> parse(CharSequence source) {
        return parse(source, null);
    }

    public List<T> parse(CharSequence source, S initialSource) {
        return parse(source, initialSource, rules);
    }

    /**
     * Transforms the source to a AST of {@link Node}s using the provided rules.
     *
     * @param rules Ordered List of rules to use to convert the source to nodes.
     *    If null, the parser will use its global list of Parser.rules.
     *
     * @throws ParseException for certain specific error flows.
     */
    public List<T> parse(CharSequence source, S initialState, List<Rule<R, T, S>> rules) {
        return parse(source, initialState, rules, enableDebugging);
    }

    /**
     * Transforms the source to a AST of {@link Node}s using the provided rules.
     *
     * @param rules Ordered List of rules to use to convert the source to nodes.
     *    If null, the parser will use its global list of Parser.rules.
     *
     * @throws ParseException for certain specific error flows.
     */
    public List<T> parse(CharSequence source, S initialState, List<Rule<R, T, S>> rules, boolean enableDebugging) {
        if (rules == null)
            rules = this.rules;

        Stack<ParseSpec<R, T, S>> remainingParses = new Stack<>();
        List<T> topLevelNodes = new ArrayList<>();

        String lastCapture = null;

        if (source != null && !source.toString().isEmpty()) {
            remainingParses.add(new ParseSpec<>(null, initialState, 0, source.length()));
        }

        while (!remainingParses.isEmpty()) {
            ParseSpec<R, T, S> builder = remainingParses.pop();

            if (builder.getStartIndex() >= builder.getEndIndex()) {
                break;
            }

            CharSequence inspectionSource = source.subSequence(builder.getStartIndex(), builder.getEndIndex());
            if (enableDebugging) {
                System.out.println("inspection: " + inspectionSource);
            }
            int offset = builder.getStartIndex();

            boolean foundRule = false;
            for (Rule<R, T, S> rule : rules) {
                Matcher matcher = rule.match(inspectionSource, lastCapture, builder.getState());
                if (matcher != null) {
                    if (enableDebugging) {
                        System.out.println("MATCH: with rule with pattern: " + rule.getMatcher().pattern().toString() + " to source: " + source + " with match: " + matcher.toMatchResult());
                    }
                    int matcherSourceEnd = matcher.end() + offset;
                    foundRule = true;

                    int matcherStart = matcher.start();
                    if (matcherStart != 0) {
                        remainingParses.push(new ParseSpec<>(null, initialState, matcherStart + offset, builder.getEndIndex()));
                        remainingParses.push(new ParseSpec<>(null, initialState, offset, matcherStart + offset));
                        break;
                    }

                    ParseSpec<R, T, S> newBuilder = rule.parse(matcher, this, builder.getState());
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
                        remainingParses.push(ParseSpec.createNonterminal(parent, builder.getState(), matcherSourceEnd, builder.getEndIndex()));
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
                } else if (enableDebugging) {
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
