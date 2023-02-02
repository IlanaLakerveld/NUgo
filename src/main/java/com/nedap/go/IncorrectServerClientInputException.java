package com.nedap.go;

public class IncorrectServerClientInputException extends RuntimeException {

    /**
     *
     * @param message the detail message. The detail message is saved for
     *                later retrieval by the {@link #getMessage()} method.
     */
    public IncorrectServerClientInputException(String message) {
        super(message);
    }
}
