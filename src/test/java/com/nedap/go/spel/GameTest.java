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




    }

}
