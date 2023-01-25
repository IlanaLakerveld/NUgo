package com.nedap.go.server;

import com.nedap.go.spel.*;

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
        readBoolean(); //begin no one had read a move

        currentPlayer = playerBlack; // player 1 ( Black ) always starts
        boolean gameStop = false;
        while (!gameStop && !playerBlack.isConnectionLost() && !playerWhite.isConnectionLost()) {
            boolean checkMove = false;
            Move move = null;
            while (!checkMove) {
                move = currentPlayer.determineMove();
                if (game.isValidMove(move)) {
                    checkMove = true;
                } else {
                    currentPlayer.sendMessage("INVALIDMOVE");
                }
            }

            game.doMove(move);
            // tell players the move
            if (move == null) {
                sendMessages("MOVE~" + currentPlayer.getName() + "~PASS");
            } else {
                sendMessages("MOVE~" + currentPlayer.getName() + "~" + move.getRow() + "~" + move.getCol());
            }

            // switch players
            switchPlayer();
            if (game.isGameOver()) {
                gameStop = true;
            }

        }

        // end of the game tell players why game over
        if (playerWhite.isConnectionLost()) {
            sendMessages("GAMEOVER~DISCONNECT~" + playerBlack.getName());
        } else if (playerBlack.isConnectionLost()) {
            sendMessages("GAMEOVER~DISCONNECT~" + playerWhite.getName());
        } else {
            sendMessages("GAMEOVER~the player with stone colour~" + game.isWinner());
        }

        readBoolean();  //begin no one had read a move;

    }

    /**
     * switch te current player.
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
    public void readBoolean() {
        playerBlack.setReadBooleanToFalse();
        playerWhite.setReadBooleanToFalse();
    }

    /**
     * This function sends messages to all the players of the game
     * @param message the messages that is sent to the players
     */
    public void sendMessages(String message) {
        playerWhite.sendMessage(message);
        playerBlack.sendMessage(message);
    }


}

