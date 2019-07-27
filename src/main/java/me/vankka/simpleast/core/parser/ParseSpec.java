package me.vankka.simpleast.core.parser;

import me.vankka.simpleast.core.node.Node;

public class ParseSpec<R, T extends Node<R>, S> {
    private T root;
    private boolean isTerminal;
    private S state;
    private int startIndex = 0;
    private int endIndex = 0;

    public ParseSpec(T root, S state, int startIndex, int endIndex) {
        this.root = root;
        this.state = state;
        this.isTerminal = false;
        this.startIndex = startIndex;
        this.endIndex = endIndex;
    }

    private ParseSpec(T root, S state) {
        this.root = root;
        this.state = state;
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

    public S getState() {
        return state;
    }

    public int getStartIndex() {
        return startIndex;
    }

    public int getEndIndex() {
        return endIndex;
    }

    public static <R, T extends Node<R>, S> ParseSpec<R, T, S> createNonterminal(T node, S state, int startIndex, int endIndex) {
        return new ParseSpec<>(node, state, startIndex, endIndex);
    }

    public static <R, T extends Node<R>, S> ParseSpec<R, T, S> createTerminal(T node, S state) {
        return new ParseSpec<>(node, state);
    }
}
