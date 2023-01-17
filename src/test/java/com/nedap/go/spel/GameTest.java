package com.nedap.go.spel;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class GameTest {

    private Game game  ;

    @Before
    public void setUp() {
        game = new Game(new Board());
    }


    @Test
    public void gameoverTest(){
        // test of the game give game over if there is passed two times in a row
        game.getListPreviousBoardStates().add(null);
        game.getListPreviousBoardStates().add(game.board.CopyBoard());
        game.getListPreviousBoardStates().add(null);
        assertFalse(game.isGameover());
        game.getListPreviousBoardStates().add(null);
        assertTrue(game.isGameover());
        game.getListPreviousBoardStates().clear();
        assertFalse(game.isGameover());
        //test of the game is over when the board is full
        for(int i=0 ; i<game.board.DIM ; i++){
            for(int j=0 ; j<game.board.DIM ; j++){
                game.board.setField(i,j,StoneColour.BLACK);
            }
        }
        assertTrue(game.isGameover());

    }

    @Test
    public void isValidMoveTest(){
        // Make a move outside the board
        Move move = new Move(-1,0,StoneColour.BLACK) ;
        assertFalse(game.isValidMove(move));
        // Make a legal move
        move = new Move(0,0,StoneColour.BLACK) ;
        assertTrue(game.isValidMove(move));
        // Make a move on a place that has already is taken
        game.board.setField(0,0,StoneColour.WHITE);
        assertFalse(game.isValidMove(move));



        // Make a move that result in a recreation of the previous state
    }

}
