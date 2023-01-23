package com.nedap.go.server;

import com.nedap.go.gui.GoGuiIntegrator;
import com.nedap.go.spel.*;

import java.util.List;

public class GameGo {
    private AbstractPlayer playerBlack ;
    private AbstractPlayer playerWhite ;
    private AbstractPlayer currentPlayer ;
    private GoGuiIntegrator gogui;
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

    public void spel() {
        game = new Game(new Board());
        gogui = new GoGuiIntegrator(true, false, boardSize);
        gogui.startGUI();
        gogui.setBoardSize(boardSize);

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

            }
            if(move == null){
                System.out.println("player has pass the move");
            }
            else {
                System.out.println("the current move is " + move.getRow() + " " + move.getCol());

                gogui.addStone(move.getRow(), move.getCol(), isStoneWhite(move));
                List<int[]> removedStones = game.changesForGUI(move);
                if(removedStones!=null) {
                    for (int[] removedStone : removedStones) {

                        gogui.removeStone(removedStone[0], removedStone[1]);
                    }
                }

            }
            game.doMove(move);
            tellMoveToPlayers(move);
            switchPlayer();
            if(game.isGameOver()){
                gameStop= true ;
            }

        }
        System.out.println("the player with stone colour "+game.isWinner()+ " is the winner")  ;

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


    private void tellMoveToPlayers(Move move){
        playerBlack.addMoveToOneGame(move);
        playerWhite.addMoveToOneGame(move);
    }

    private boolean isStoneWhite(Move move){
        return move.getColour().equals(StoneColour.WHITE);
    }


}

