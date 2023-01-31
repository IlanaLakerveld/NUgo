package com.nedap.go.spel;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import java.util.List;


public class BoardTest {
    private Board board;



    @Before // same as before each
    public void setUp() {
        board = new Board();
    }


    /**
     * Test if the board is empty at the beginning
     */
    @Test
    public void testSetup() {
        for (int i = 0; i < Board.DIM; i++) {
            for (int j = 0; j < Board.DIM; j++) {
                assertEquals(StoneColour.EMPTY, board.getField(i, j));
            }
        }
    }

    /**
     * Test the function isField, which is used to check if some field is in the scope of the board
     */
    @Test
    public void testIsFieldIndex() {
        assertFalse(board.isField(-1, 0));
        assertTrue(board.isField(0, 0));
        assertTrue(board.isField(Board.DIM - 1, Board.DIM - 1));
        assertFalse(board.isField(Board.DIM, Board.DIM));
    }

    /**
     * Test the if you can make a correct copy of the board
     */
    @Test
    public void testCopy() {
        board.setField(0, 0, StoneColour.WHITE);
        board.setField(1, 0, StoneColour.BLACK);
        Board deepCopyBoard = board.copyBoard();

        // First test if all the fields are the same
        for (int i = 0; i < Board.DIM; i++) {
            for (int j = 0; j < Board.DIM; j++) {
                assertEquals(board.getField(i, j), deepCopyBoard.getField(i, j));
            }
        }

//        // Check if a field in the deep copied board the original remains the same
        deepCopyBoard.setField(1, 1, StoneColour.BLACK);
        assertEquals(StoneColour.EMPTY, board.getField(1, 1));
        assertEquals(StoneColour.BLACK, deepCopyBoard.getField(1, 1));
    }

    @Test
    public void testIsFull() {
        for (int i = 0; i < Board.DIM; i++) {
            for (int j = 0; j < Board.DIM - 1; j++) {
                board.setField(i, j, StoneColour.BLACK);
            }
        }
        assertFalse(board.isFull());
        for (int i = 0; i < Board.DIM; i++) {
            board.setField(i, Board.DIM - 1, StoneColour.WHITE);
        }
        assertTrue(board.isFull());
    }

    @Test
    public void testIsSurroundedCorner() {
        // first check for the corner
        board.setField(0, 0, StoneColour.BLACK);
        board.setField(0, 1, StoneColour.WHITE);

        // not yet surrounded so false
        assertFalse(board.isSurrounded(0, 0));
        board.setField(1, 0, StoneColour.WHITE);
        // no surrounded so true
        assertTrue(board.isSurrounded(0, 0));
        //this is not  so false
        assertFalse(board.isSurrounded(0, 1));

        // it can not be surrounded by one stones
        board.setField(0, 0, StoneColour.WHITE);
        assertFalse(board.isSurrounded(0, 0));
    }

    @Test
    // Same test as  testIsSurroundedCorner but now for the Edge
    public void testIsSurroundedEdge() {

        board.setField(0, 1, StoneColour.WHITE);
        assertFalse(board.isSurrounded(0, 1));
        board.setField(0, 0, StoneColour.BLACK);
        board.setField(0, 2, StoneColour.BLACK);
        assertFalse(board.isSurrounded(0, 1));
        board.setField(1, 1, StoneColour.BLACK);
        assertTrue(board.isSurrounded(0, 1));
    }

    // Same test as  testIsSurroundedCorner but now for the middle
    @Test
    public void testIsSurroundedMiddle() {
        board.setField(2, 2, StoneColour.WHITE);
        assertFalse(board.isSurrounded(2, 2));
        board.setField(2, 3, StoneColour.BLACK);
        assertFalse(board.isSurrounded(2, 2));
        board.setField(2, 1, StoneColour.BLACK);
        assertFalse(board.isSurrounded(2, 2));
        board.setField(1, 2, StoneColour.BLACK);
        assertFalse(board.isSurrounded(2, 2));
        board.setField(3, 2, StoneColour.BLACK);
        assertTrue(board.isSurrounded(2, 2));
    }

    @Test
    public void testIsSurroundedMoreThenOneStone() {
        board.setField(2, 2, StoneColour.WHITE);
        board.setField(1, 2, StoneColour.WHITE);
        board.setField(2, 3, StoneColour.WHITE);
        board.setField(2, 4, StoneColour.WHITE);

        board.setField(0, 2, StoneColour.BLACK);
        board.setField(1, 3, StoneColour.BLACK);
        board.setField(1, 4, StoneColour.BLACK);
        board.setField(2, 5, StoneColour.BLACK);
        board.setField(3, 4, StoneColour.BLACK);
        board.setField(3, 3, StoneColour.BLACK);
        board.setField(3, 2, StoneColour.BLACK);
        board.setField(2, 1, StoneColour.BLACK);
        assertFalse(board.isSurrounded(2, 2));

        board.setField(1, 1, StoneColour.BLACK);

        assertTrue(board.isSurrounded(2, 2));


        board.setField(2, 3, StoneColour.EMPTY);
        assertFalse(board.isSurrounded(2, 2));
        board.setField(2, 4, StoneColour.EMPTY);
        assertFalse(board.isSurrounded(2, 2));
    }

    @Test
    public void testCaptured() {
        board.setField(2, 2, StoneColour.WHITE);
        board.setField(2, 3, StoneColour.WHITE);
        board.setField(2, 4, StoneColour.WHITE);
        board.setField(3, 2, StoneColour.WHITE);


        board.setField(2, 1, StoneColour.BLACK);
        board.setField(3, 1, StoneColour.BLACK);

        board.setField(1, 2, StoneColour.BLACK);
        board.setField(1, 3, StoneColour.BLACK);
        board.setField(1, 4, StoneColour.BLACK);
        board.setField(2, 5, StoneColour.BLACK);

        board.setField(3, 3, StoneColour.BLACK);
        board.setField(3, 4, StoneColour.BLACK);
        board.setField(4, 2, StoneColour.BLACK);


        List<int[]> list  ;
        list = board.captured(2, 2);

        assertEquals(4,list.size(),0.0);

        List<int[]> list2  ;
        list2 = board.captured(2, 3);

        assertEquals(4,list2.size(),0.0);


    }
    @Test
    public void testCaptured2() {
        board.setField(2, 2, StoneColour.WHITE);
        board.setField(2, 3, StoneColour.WHITE);
        board.setField(2, 4, StoneColour.WHITE);
        board.setField(3, 2, StoneColour.WHITE);
        board.setField(3, 3, StoneColour.WHITE);
        board.setField(4,3 ,StoneColour.WHITE);


        board.setField(2, 1, StoneColour.BLACK);
        board.setField(3, 1, StoneColour.BLACK);
       board.setField(4, 2, StoneColour.BLACK);
        board.setField(5, 3, StoneColour.BLACK);
        board.setField(4, 4, StoneColour.BLACK);
        board.setField(3, 4, StoneColour.BLACK);
        board.setField(2, 5, StoneColour.BLACK);
        board.setField(1, 4, StoneColour.BLACK);
        board.setField(1, 3, StoneColour.BLACK);
        board.setField(1, 2, StoneColour.BLACK);


        List<int[]> list  ;
        list = board.captured(3, 3);

        assertEquals(list.size(),6,0.0);
    }



}
