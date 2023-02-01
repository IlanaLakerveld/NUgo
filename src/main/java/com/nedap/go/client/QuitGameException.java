package com.nedap.go.client;

import java.io.IOException;

/**
 * Throw this exception if the player quits the game during the game
 */
public class QuitGameException extends IOException {
    /**
     * Constructs an {@code IOException} with the specified detail message.
     *

     */
    public QuitGameException() {
        super("The user has typed quit so want to stop the game ");
    }
}
