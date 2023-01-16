package com.nedap.go.spel;

import org.junit.Test;

import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BoardTest {
    private Board board;

    @BeforeEach
    public void setUp() {
        board = new Board();
    }


    @Test
    public void testIsFieldIndex() {
//        assertFalse(board.isField(-1));
//        assertTrue(board.isField(0));
//        assertTrue(board.isField(Board.DIM * Board.DIM - 1));
//        assertFalse(board.isField(Board.DIM * Board.DIM));
    }

    /**
     * Test the if you can make a correct copy of the board
     */
    @Test
    public void testCopy() {
          board.setField(0,0,StoneColour.WHITE);
          board.setField(1,0,StoneColour.BLACK);
          Board deepCopyBoard = board.CopyBoard() ;

        // First test if all the fields are the same
        for (int i = 0; i < Board.DIM; i++) {
            for(int j = 0 ; j < Board.DIM ;j++){
                assertEquals(board.getField(i,j), deepCopyBoard.getField(i,j));
            }
        }

//        // Check if a field in the deepcopied board the original remains the same
          deepCopyBoard.setField(1,1, StoneColour.BLACK);
            assertEquals(StoneColour.EMPTY , board.getField(1,1));
            assertEquals(StoneColour.BLACK, deepCopyBoard.getField(1,1));
    }






}
