package com.nedap.go.client;

import com.nedap.go.spel.Game;
import com.nedap.go.spel.Move;

public class HumanPlayer extends Player{
    /**
     * Constructor
     *
     * @param name name of the player
     */
    public HumanPlayer(String name) {
        super(name);
    }

    @Override
    public Move determineMove(Game game) {
        return null;
    }
}
