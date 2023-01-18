package com.nedap.go;

import com.nedap.go.spel.Board;

public final class BoardCompare {
    /**
     * Check if the board are the same
     * @param board1 first board
     * @param board2 second board
     * @return true is boards are the same
     */
    public static boolean boardCompare(Board board1, Board board2) {
        if(board1 == null || board2 == null){
            return false;
        }
        for (int i = 0; i < Board.DIM; i++) {
            for (int j = 0; j < Board.DIM; j++) {
                if (!board1.getField(i, j).equals(board2.getField(i, j))) {
                    return false;
                }
            }
        }
        return true;
    }

}
