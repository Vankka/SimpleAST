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
