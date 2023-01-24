package com.nedap.go.client;

//import com.nedap.go.spel.*;

import com.nedap.go.gui.GoGuiIntegrator;
import com.nedap.go.spel.Board;
import com.nedap.go.spel.Game;
import com.nedap.go.spel.Move;
import com.nedap.go.spel.StoneColour;

import java.util.List;

public abstract class Player {
    protected String name;
    protected StoneColour colour ;
    protected final Game game ;

    private GoGuiIntegrator gogui;


    /**
     * Constructor
     * @param name name of the player
     */
    public Player(String name, StoneColour colour) {
        this.name = name;
        this.colour =colour;
        game = new Game(new Board());
        int boardSize = game.board.DIM ;
        gogui = new GoGuiIntegrator(true, false, boardSize);
        gogui.startGUI();
        gogui.setBoardSize(boardSize);

    }

    /**
     * Returns the name of the player.
     * @return the name of the player
     */
    public String getName() {
        return name;
    }


    public abstract Move determineMove();

    public void updateBoard(Move move){
        if(move == null){
            System.out.println("The move is PASS");
        }
        else {

            gogui.addStone(move.getRow(), move.getCol(), isStoneWhite(move));
            List<int[]> removedStones = game.changesForGUI(move);
            if(removedStones!=null) {
                for (int[] removedStone : removedStones) {

                    gogui.removeStone(removedStone[0], removedStone[1]);
                }
            }

        }
        game.doMove(move);

    }


    public StoneColour getColour() {
        return colour;
    }


    public StoneColour getColourOpponend() {
        if (colour.equals(StoneColour.WHITE)){
            return StoneColour.BLACK;
        }
        else{
            return StoneColour.WHITE;
        }

    }

    private boolean isStoneWhite(Move move){
        return move.getColour().equals(StoneColour.WHITE);
    }

}
