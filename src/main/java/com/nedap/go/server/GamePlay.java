package com.nedap.go.server;

import com.nedap.go.spel.*;

public class GamePlay {
    private AbstractPlayer playerBlack ;
    private AbstractPlayer playerWhite ;
    private AbstractPlayer currentPlayer ;

    private Game game ;

    public GamePlay(AbstractPlayer player1 ,AbstractPlayer player2 ) {
        playerBlack=player1;
        playerWhite=player2;


    }

    public void spel() {
        game = new Game(new Board());
        currentPlayer=playerBlack; // player 1 ( Black ) always starts
        boolean gameStop = false ;
        while(!gameStop){



            if(game.isGameover()){
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
