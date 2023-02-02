package com.nedap.go.server;

import com.nedap.go.Protocol;
import com.nedap.go.spel.*;

/**
 * This class has the flow of the game. This is a runnable class.
 */
public class GameGo implements Runnable {
    private ServerPlayer playerBlack;
    private ServerPlayer playerWhite;
    private ServerPlayer currentPlayer;
    private int boardSize;
    private Game game;

    /**
     * Constructor
     *
     * @param player1 player black
     * @param player2 player white
     */
    public GameGo(ServerPlayer player1, ServerPlayer player2, int boardSize) {
        playerBlack = player1;
        playerWhite = player2;
        this.boardSize = boardSize;


    }

    /**
     * This is the game flow. this keeps running until the game is ended or connection is lost.
     */
    public void run() {
        game = new Game(new Board());
        setReadBoolean(); //begin no one had read a move

        currentPlayer = playerBlack; // player 1 ( Black ) always starts
        boolean gameStop = false;
        while (!gameStop && !playerBlack.isConnectionLost() && !playerWhite.isConnectionLost()) { // game should stop if the rules tell that should stop of the connection is lost

            Move move = getMove(); // the move of the player.
            game.doMove(move);
            // tell players the move
            if (move == null) {
                sendMessages("MOVE"+Protocol.delimiter + currentPlayer.getName() + Protocol.delimiter +"PASS");
            } else {
                sendMessages("MOVE"+Protocol.delimiter + currentPlayer.getName() + Protocol.delimiter + move.getRow() + Protocol.delimiter + move.getCol());
            }

            switchPlayer();   // switch players
            if (game.isGameOver()) {
                gameStop = true;
            }

        }

        // end of the game tell players why game over
        endOfTheGame(); // tells everyone why the game is over.

        setReadBoolean();  //begin no one had read a move;

    }


    /**
     * Gets the move of the player
     * @return the move
     */
    private Move getMove() {
        boolean checkMove = false;
        Move move =null;
        while (!checkMove) {
            move = currentPlayer.determineMove();
            if (game.isValidMove(move)) {
                checkMove = true;
            } else {
                currentPlayer.sendMessage("INVALIDMOVE");
            }
        }
        return move;
    }

    /**
     * Tells everyone why teh game is over
     */
    private void endOfTheGame() {
        if (playerWhite.isConnectionLost()) {
            sendMessages("GAMEOVER"+Protocol.delimiter+"DISCONNECT"+Protocol.delimiter + playerBlack.getName());
        } else if (playerBlack.isConnectionLost()) {
            sendMessages("GAMEOVER"+Protocol.delimiter+"DISCONNECT"+Protocol.delimiter + playerWhite.getName());
        } else {
            if(game.isWinner().equals(StoneColour.EMPTY)){
                sendMessages("GAMEOVER"+Protocol.delimiter+"VICTORY"+Protocol.delimiter + "It's a tie");
            }
            else if(game.isWinner().equals(StoneColour.BLACK)) {
                sendMessages("GAMEOVER" + Protocol.delimiter + "VICTORY" + Protocol.delimiter + playerBlack.getName());
            }
            else{
                sendMessages("GAMEOVER" + Protocol.delimiter + "VICTORY" + Protocol.delimiter + playerWhite.getName());
            }
        }
    }

    /**
     * Switch te current player.
     */
    private void switchPlayer() {
        if (currentPlayer.equals(playerBlack)) {
            currentPlayer = playerWhite;
        } else if (currentPlayer.equals(playerWhite)) {
            currentPlayer = playerBlack;
        }
    }



    /**
     * This is used for synchronisation.
     */
    private void setReadBoolean() {
        playerBlack.setReadBooleanToFalse();
        playerWhite.setReadBooleanToFalse();
    }

    /**
     * This function sends messages to all the players of the game
     * @param message the messages that is sent to the players
     */
    private void sendMessages(String message) {
        playerWhite.sendMessage(message);
        playerBlack.sendMessage(message);
    }


}

