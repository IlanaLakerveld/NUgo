package com.nedap.go.client;

import com.nedap.go.spel.Move;
import com.nedap.go.spel.StoneColour;

import java.util.List;

public class ComputerPlayer extends Player {
    /**
     * Constructor
     *
     * @param name name of the player
     */
    public ComputerPlayer(String name, StoneColour colour) {
        super(name, colour);
    }



    @Override
    public Move determineMove() {
        List<int[]> possibleMoves = game.getEmptyFields();
        return getValidMove(possibleMoves);



    }

    private Move getValidMove(List<int[]> possibleMoves) {
        if (possibleMoves.size() > 0) {
            int randomInt = (int) (Math.random() * possibleMoves.size());
            Move move = new Move(possibleMoves.get(randomInt)[0], possibleMoves.get(randomInt)[1], colour);
            if (game.isValidMove(move)) {
                return move;
            } else {
                possibleMoves.remove(randomInt);
                return getValidMove(possibleMoves);
            }

        } else {
            return null;
        }
    }
}
