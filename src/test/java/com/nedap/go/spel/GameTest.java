package com.nedap.go.spel;

import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Map;

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
    public void gameOverTest(){
        // test of the game give game over if there is passed two times in a row
        game.getListPreviousBoardStates().add(null);
        game.getListPreviousBoardStates().add(game.getBoard().copyBoard());
        game.getListPreviousBoardStates().add(null);
        assertFalse(game.isGameOver());
        game.getListPreviousBoardStates().add(null);
        assertTrue(game.isGameOver());
        game.getListPreviousBoardStates().clear();
        assertFalse(game.isGameOver());
        //test of the game is over when the board is full
        for(int i=0 ; i<Board.DIM ; i++){
            for(int j=0 ; j<Board.DIM ; j++){
                game.getBoard().setField(i,j,StoneColour.BLACK);
            }
        }
        assertTrue(game.isGameOver());

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
        game.getBoard().setField(0,0,StoneColour.WHITE);
        assertFalse(game.isValidMove(move));



        // Make a move that result in a recreation of the previous state
        game.getBoard().setField(1,1,StoneColour.WHITE);
        game.getBoard().setField(0,2,StoneColour.WHITE);
        game.getBoard().setField(2,2,StoneColour.WHITE);
        game.getListPreviousBoardStates().add(game.getBoard().copyBoard());
        game.getBoard().setField(1,2,StoneColour.BLACK);
        game.getBoard().setField(0,3,StoneColour.BLACK);
        game.getBoard().setField(2,3,StoneColour.BLACK);
        game.getBoard().setField(1,4,StoneColour.BLACK);
        game.getListPreviousBoardStates().add(game.getBoard().copyBoard());
        move= new Move(1,3,StoneColour.WHITE);
        assertTrue(game.isValidMove(move));
        game.doMove(move);
        move = new Move(1,2,StoneColour.BLACK);
        assertFalse(game.isValidMove(move));


    }

    @Test
   public void getScoreTestANDisWInnerTest(){
        // To test is the if the points are correctly count I use the example of https://en.wikipedia.org/wiki/Rules_of_Go#Area_scoring
        game.getBoard().setField(0,2,StoneColour.BLACK);
        game.getBoard().setField(0,3,StoneColour.BLACK);
        game.getBoard().setField(0,4,StoneColour.BLACK);
        game.getBoard().setField(0,5,StoneColour.WHITE);
        game.getBoard().setField(1,1,StoneColour.BLACK);
        game.getBoard().setField(1,3,StoneColour.BLACK);
        game.getBoard().setField(1,4,StoneColour.WHITE);
        game.getBoard().setField(1,7,StoneColour.WHITE);
        game.getBoard().setField(2,1,StoneColour.BLACK);
        game.getBoard().setField(2,2,StoneColour.BLACK);
        game.getBoard().setField(2,3,StoneColour.WHITE);
        game.getBoard().setField(2,4,StoneColour.WHITE);
        game.getBoard().setField(2,8,StoneColour.WHITE);
        game.getBoard().setField(3,2,StoneColour.BLACK);
        game.getBoard().setField(3,3,StoneColour.BLACK);
        game.getBoard().setField(3,4,StoneColour.WHITE);
        game.getBoard().setField(3,6,StoneColour.WHITE);
        game.getBoard().setField(3,7,StoneColour.WHITE);
        game.getBoard().setField(3,8,StoneColour.BLACK);
        game.getBoard().setField(4,2,StoneColour.BLACK);
        game.getBoard().setField(4,3,StoneColour.WHITE);
        game.getBoard().setField(4,6,StoneColour.WHITE);
        game.getBoard().setField(4,7,StoneColour.BLACK);
        game.getBoard().setField(4,8,StoneColour.BLACK);
        game.getBoard().setField(5,1,StoneColour.BLACK);
        game.getBoard().setField(5,2,StoneColour.WHITE);
        game.getBoard().setField(5,3,StoneColour.WHITE);
        game.getBoard().setField(5,4,StoneColour.WHITE);
        game.getBoard().setField(5,5,StoneColour.WHITE);
        game.getBoard().setField(5,6,StoneColour.BLACK);
        game.getBoard().setField(5,8,StoneColour.BLACK);
        game.getBoard().setField(6,2,StoneColour.BLACK);
        game.getBoard().setField(6,3,StoneColour.WHITE);
        game.getBoard().setField(6,4,StoneColour.WHITE);
        game.getBoard().setField(6,5,StoneColour.BLACK);
        game.getBoard().setField(6,6,StoneColour.BLACK);
        game.getBoard().setField(6,7,StoneColour.BLACK);
        game.getBoard().setField(6,8,StoneColour.BLACK);
        game.getBoard().setField(7,2,StoneColour.BLACK);
        game.getBoard().setField(7,3,StoneColour.WHITE);
        game.getBoard().setField(7,4,StoneColour.WHITE);
        game.getBoard().setField(7,5,StoneColour.WHITE);
        game.getBoard().setField(7,6,StoneColour.BLACK);
        game.getBoard().setField(7,8,StoneColour.WHITE);
        game.getBoard().setField(8,2,StoneColour.BLACK);
        game.getBoard().setField(8,3,StoneColour.BLACK);
        game.getBoard().setField(8,4,StoneColour.BLACK);
        game.getBoard().setField(8,5,StoneColour.WHITE);
        game.getBoard().setField(8,6,StoneColour.BLACK);
        game.getBoard().setField(8,7,StoneColour.WHITE);

        Map<StoneColour, Integer> score = game.getScore();
        // 44 and 36 are the results as calculated in https://en.wikipedia.org/wiki/Rules_of_Go#Area_scoring
        assertEquals(44, score.get(StoneColour.BLACK)  ,0.0);
        assertEquals(36,score.get(StoneColour.WHITE),0.0);
        assertEquals(game.isWinner(),StoneColour.BLACK);


    }


    @Test
    public void getScoreTestANDisWInnerTest2(){
        // Same field as before only field 1,7 is different so therefor white should have a much lower score
        game.getBoard().setField(0,2,StoneColour.BLACK);
        game.getBoard().setField(0,3,StoneColour.BLACK);
        game.getBoard().setField(0,4,StoneColour.BLACK);
        game.getBoard().setField(0,5,StoneColour.WHITE);
        game.getBoard().setField(1,1,StoneColour.BLACK);
        game.getBoard().setField(1,3,StoneColour.BLACK);
        game.getBoard().setField(1,4,StoneColour.WHITE);
        game.getBoard().setField(1,7,StoneColour.BLACK);
        game.getBoard().setField(2,1,StoneColour.BLACK);
        game.getBoard().setField(2,2,StoneColour.BLACK);
        game.getBoard().setField(2,3,StoneColour.WHITE);
        game.getBoard().setField(2,4,StoneColour.WHITE);
        game.getBoard().setField(2,8,StoneColour.WHITE);
        game.getBoard().setField(3,2,StoneColour.BLACK);
        game.getBoard().setField(3,3,StoneColour.BLACK);
        game.getBoard().setField(3,4,StoneColour.WHITE);
        game.getBoard().setField(3,6,StoneColour.WHITE);
        game.getBoard().setField(3,7,StoneColour.WHITE);
        game.getBoard().setField(3,8,StoneColour.BLACK);
        game.getBoard().setField(4,2,StoneColour.BLACK);
        game.getBoard().setField(4,3,StoneColour.WHITE);
        game.getBoard().setField(4,6,StoneColour.WHITE);
        game.getBoard().setField(4,7,StoneColour.BLACK);
        game.getBoard().setField(4,8,StoneColour.BLACK);
        game.getBoard().setField(5,1,StoneColour.BLACK);
        game.getBoard().setField(5,2,StoneColour.WHITE);
        game.getBoard().setField(5,3,StoneColour.WHITE);
        game.getBoard().setField(5,4,StoneColour.WHITE);
        game.getBoard().setField(5,5,StoneColour.WHITE);
        game.getBoard().setField(5,6,StoneColour.BLACK);
        game.getBoard().setField(5,8,StoneColour.BLACK);
        game.getBoard().setField(6,2,StoneColour.BLACK);
        game.getBoard().setField(6,3,StoneColour.WHITE);
        game.getBoard().setField(6,4,StoneColour.WHITE);
        game.getBoard().setField(6,5,StoneColour.BLACK);
        game.getBoard().setField(6,6,StoneColour.BLACK);
        game.getBoard().setField(6,7,StoneColour.BLACK);
        game.getBoard().setField(6,8,StoneColour.BLACK);
        game.getBoard().setField(7,2,StoneColour.BLACK);
        game.getBoard().setField(7,3,StoneColour.WHITE);
        game.getBoard().setField(7,4,StoneColour.WHITE);
        game.getBoard().setField(7,5,StoneColour.WHITE);
        game.getBoard().setField(7,6,StoneColour.BLACK);
        game.getBoard().setField(7,8,StoneColour.WHITE);
        game.getBoard().setField(8,2,StoneColour.BLACK);
        game.getBoard().setField(8,3,StoneColour.BLACK);
        game.getBoard().setField(8,4,StoneColour.BLACK);
        game.getBoard().setField(8,5,StoneColour.WHITE);
        game.getBoard().setField(8,6,StoneColour.BLACK);
        game.getBoard().setField(8,7,StoneColour.WHITE);

        Map<StoneColour, Integer> score = game.getScore();
        // 44 and 36 are the results as calculated in https://en.wikipedia.org/wiki/Rules_of_Go#Area_scoring
        assertEquals(45,score.get(StoneColour.BLACK)  ,0.0);
        assertEquals(23,score.get(StoneColour.WHITE),0.0);
        assertEquals(game.isWinner(),StoneColour.BLACK);


    }

    @Test
    public void doMoveTest(){
        Move move = new Move(0,0,StoneColour.BLACK);
        game.doMove(move);
        assertEquals(game.getBoard().getField(0,0) ,StoneColour.BLACK);
        move = new Move(0,1,StoneColour.WHITE);
        game.doMove(move);
        move = new Move(1,0,StoneColour.WHITE);
        game.doMove(move);
        assertEquals(game.getBoard().getField(0,1) ,StoneColour.WHITE);
        assertEquals(game.getBoard().getField(1,0) ,StoneColour.WHITE);
        // this stone should be removed because it captured
        assertEquals(game.getBoard().getField(0,0) ,StoneColour.EMPTY);
        // there are 3 moves done in this game so the list should be of size 3
        assertEquals(game.getListPreviousBoardStates().size(),3,0.0);

    }

    @Test
    public void testGetEmptyFields(){
        List<int[]> emptyFields = game.getEmptyFields();
        assertEquals(emptyFields.size(),game.getBoard().DIM*game.getBoard().DIM,0);
        game.getBoard().setField(1,0,StoneColour.BLACK);
        game.getBoard().setField(1,1,StoneColour.BLACK);
        List<int[]> emptyFields2 = game.getEmptyFields();
        assertEquals(emptyFields2.size(),game.getBoard().DIM*game.getBoard().DIM-2);
    }

}
