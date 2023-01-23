package com.nedap.go.client;

import com.nedap.go.spel.Move;
import com.nedap.go.spel.StoneColour;

public class ComputerPlayer extends Player{
    /**
     * Constructor
     *
     * @param name name of the player
     */
    public ComputerPlayer(String name, StoneColour colour) {
        super(name,colour);
    }

    @Override
    public Move determineMove() {
        return null;
    }
}
