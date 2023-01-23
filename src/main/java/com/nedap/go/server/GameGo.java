package com.nedap.go.server;

import com.nedap.go.gui.GoGuiIntegrator;
import com.nedap.go.spel.*;

import java.util.List;

public class GameGo implements Runnable{
    private AbstractPlayer playerBlack ;
    private AbstractPlayer playerWhite ;
    private AbstractPlayer currentPlayer ;

//    private GoGuiIntegrator gogui;
    private int boardSize ;
    private Game game ;

    /**
     * Constructor
     * @param player1
     * @param player2
     */
    public GameGo(AbstractPlayer player1 , AbstractPlayer player2 ,int boardSize ) {
        playerBlack=player1;
        playerWhite=player2;

        this.boardSize=boardSize;


    }

    public void run() {
        game = new Game(new Board());
        readBoolean(); //begin no one had read a move


        currentPlayer=playerBlack; // player 1 ( Black ) always starts
        boolean gameStop = false ;
        while(!gameStop){
            boolean checkMove=false;
            Move move = null;
            while(!checkMove) {
                move = currentPlayer.determineMove();
                if(game.isValidMove(move)){
                    checkMove=true;
                }
                else{
                    currentPlayer.sendMessage("INVALIDMOVE");
                }
            }

            game.doMove(move);
           // tell players the move
            if(move == null) {
                sendMessages("MOVE~" + currentPlayer.getName() + "~PASS");
            }
               else{
                   sendMessages("MOVE~"+currentPlayer.getName()+"~"+move.getRow()+move.getCol());
                }

            switchPlayer();
            if(game.isGameOver()){
                gameStop= true ;
            }

        }
        sendMessages("the player with stone colour "+game.isWinner()+ " is the winner");
        readBoolean();  //begin no one had read a move;

    }

    private void switchPlayer(){
        if( currentPlayer.equals(playerBlack)){
            currentPlayer=playerWhite;
        }
        else if(currentPlayer.equals(playerWhite)){
            currentPlayer=playerBlack;
        }
    }

    public AbstractPlayer getCurrentPlayer() {
        return currentPlayer;
    }


    public void readBoolean(){
        playerBlack.setReadBooleanToFalse();
        playerWhite.setReadBooleanToFalse();
    }

    public void sendMessages(String message){
        playerWhite.sendMessage(message);
        playerBlack.sendMessage(message);
    }



}

