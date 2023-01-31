package com.nedap.go.client;

import com.nedap.go.gui.GoGuiIntegrator;
import com.nedap.go.spel.Board;
import com.nedap.go.spel.Game;
import com.nedap.go.spel.Move;
import com.nedap.go.spel.StoneColour;
import java.util.List;

/**
 * This is the Abstract class player. Human player en computer player extends this class.
 * The function determineMove needs to be overridden through the class.
 * This class initializes the gui and sends the data to the gui
 */
public abstract class Player {
    protected String name;
    protected StoneColour colour ;
    protected final Game game ;
    private GoGuiIntegrator gogui;


    /**
     *
     * @param name name of the player
     * @param colour colour of the stone the person has
     */
    public Player(String name, StoneColour colour) {
        this.name = name;
        this.colour =colour;
        game = new Game(new Board());
        int boardSize = Board.DIM ;
        gogui = new GoGuiIntegrator(true, false, boardSize);
        gogui.startGUI();
        gogui.setBoardSize(boardSize);

    }



    /**
     * Determine which move the person wants to make
     * @return move that person wants to make
     */
    public abstract Move determineMove() throws QuitGameException;

    /**
     * updates the board so player knows al the moves that are on the board and update the gui
     * @param move the move that is made.
     */
    public void updateBoard(Move move){
        if(move == null){
            System.out.println("The move is PASS");
        }
        else {

            gogui.addStone(move.getRow(), move.getCol(), isStoneWhite(move.getColour()));
            List<int[]> removedStones = game.changesForGUI(move);
            if(removedStones!=null) {
                for (int[] removedStone : removedStones) {

                    gogui.removeStone(removedStone[0], removedStone[1]);
                }
            }

        }
        game.doMove(move);

    }

    /**
     * Returns the name of the player.
     * @return the name of the player
     */
    public String getName() {
        return name;
    }


    /**
     * returns the colour of the stone the person has.
     * @return stone colour of the player.
     */
    public StoneColour getColour() {
        return colour;
    }

    /**
     * Get the colour the other peron is playing with
     * @return the colour of the opponent
     */
    public StoneColour getColourOpponent() {
        if (colour.equals(StoneColour.WHITE)){
            return StoneColour.BLACK;
        }
        else{
            return StoneColour.WHITE;
        }

    }

    /**
     * This is for the gui. The gui does not know about the enum therefore the enum needs to be changed  into a boolean statement.
     * @param stone stone colour
     * @return true if the stone colour is white.
     */
    private boolean isStoneWhite(StoneColour stone){
        return stone.equals(StoneColour.WHITE);
    }


    public void stopGUI(){
        gogui.stopGUI();
    }

}
