package com.nedap.go.server;

import com.nedap.go.spel.*;

public class GameGo {
    private AbstractPlayer playerBlack ;
    private AbstractPlayer playerWhite ;
    private AbstractPlayer currentPlayer ;

    private Game game ;

    /**
     * Constructor
     * @param player1
     * @param player2
     */
    public GameGo(AbstractPlayer player1 , AbstractPlayer player2 ) {
        playerBlack=player1;
        playerWhite=player2;


    }

    public void spel() {
        game = new Game(new Board());
        currentPlayer=playerBlack; // player 1 ( Black ) always starts
        boolean gameStop = false ;
        while(!gameStop){
            // Hier komt het daadwerkelijk spel met funties uit Game
            // Dit gaat er ongeveer zo uitzien
            //  De speler die aan de beurt is ee zet mag doen
            // Een check of dit mag en of spel niet over is
            // verteld iederen welke zet er word gedaan
            // zet de zet op het bord

            if(game.isGameOver()){
                gameStop= true ;
            }

        }

    }

    private void SwitchPlayer(){
        if( currentPlayer.equals(playerBlack)){
            currentPlayer=playerWhite;
        }
        if(currentPlayer.equals(playerWhite)){
            currentPlayer=playerBlack;
        }
    }

    public AbstractPlayer getCurrentPlayer() {
        return currentPlayer;
    }
}
