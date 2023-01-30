package com.nedap.go.server;

import com.nedap.go.spel.Move;
import com.nedap.go.spel.StoneColour;

/**
 * This class are the players on the server side. This gets the useful information from the client handler (and therefore from the client).
 */

public class ServerPlayer {
    private final String name;
    private final StoneColour colour;
    private final ClientHandler cl;

    /**
     * Constructor
     * @param colour the stone colour of this player (either black or white)
     * @param cl the clientHandler of this player
     */
    public ServerPlayer(StoneColour colour, ClientHandler cl) {
        this.name = cl.getMyUsername();
        this.colour = colour;
        this.cl = cl;

    }

    /**
     * Determines the next move (by asking the client)
     * @return Either Move(row,col,colour of stone) of null (if the move is pass)
     */
    public Move determineMove() {
        Move move = null;
        cl.sendMessage("YOURTURN");
        int[] moveFromClient = cl.getMove();
        if (moveFromClient != null) {
            move = new Move(moveFromClient[0], moveFromClient[1], colour);
        } else { // thus : moveFromClient == null
            System.out.println(getName() + "move is pass");
        }

        return move;
    }

    /**
     * sends a message to the client (by calling the sendMessage function of the clientHandler)
     * @param message the messages that needed to be sent to the client.
     */
    public void sendMessage(String message) {
        cl.sendMessage(message);
    }

    /**
     * get the name of this player
     * @return name of this player
     */
    public String getName() {
        return name;
    }

    /**
     * function that needs to be set in beginning and end of a game. This because of synchronisation.
     */
    public void setReadBooleanToFalse() {
        cl.setValueRead(true);
    }

    /**
     * Checking if the player is still connected to the server.
     * @return true if there is no more connection.
     */
    public boolean isConnectionLost() {
        return cl.isConnectionLost();
    }
}
