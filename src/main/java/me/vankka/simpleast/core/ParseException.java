package me.vankka.simpleast.core;

public class ParseException extends RuntimeException {
    public ParseException(String message, CharSequence source, Throwable cause) {
        super("Error while parsing: " + message + " \n Source: " + source, cause);
    }

    public ParseException(String message, CharSequence source) {
        super("Error while parsing: " + message + " \n Source: " + source);
    }
}
