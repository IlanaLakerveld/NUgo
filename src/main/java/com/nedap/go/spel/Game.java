package com.nedap.go.spel;

import com.nedap.go.BoardCompare;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Game {

    public Board board;

    private List<Board> listPreviousBoardStates = new ArrayList<>();


    /**
     * constructor
     *
     * @param board the board the game is played on
     */
    public Game(Board board) {

        this.board = board;
    }


    /**
     * A game is ended if : there is no more space left on the board, there are two passes(indicated by null in listPreviousBoardStates ) or manually when someone left the game
     *
     * @return true if the game should if ended
     */
    public boolean isGameover() {

        //if board is full the game is over
        if (board.isFull()) {
            return true;
        }
        // if two consecutive passes game should end but therefore there needs to be more than one state in the list.
        if (listPreviousBoardStates.size() > 1) {
            if (listPreviousBoardStates.get(listPreviousBoardStates.size() - 1) == null && listPreviousBoardStates.get(listPreviousBoardStates.size() - 2) == null) {
                return true;
            }
        }

        return false;
    }

    /**
     * Checks whether a move is actually a legal move.
     * A legal move is can only be on an empty space inside the board and may not recreate a previous board.
     *
     * @return true if the move is a legal move
     */
    public boolean isValidMove(Move move) {
        // Check if the move is on the field
        if (!board.isField(move.getRow(), move.getCol())) {
            return false;
        }
        //check if the field is not empty
        if (!board.isEmptyField(move.getRow(), move.getCol())) {
            return false;
        }
        // check if board position is not already been done in the game
        if (listPreviousBoardStates.size() > 0) {
            Board copyBoard = board.CopyBoard();
            extracted(move, copyBoard);
            for (Board listPreviousBoardState : listPreviousBoardStates) {
                // if two board are equal then false
                if (BoardCompare.boardCompare(listPreviousBoardState, copyBoard)) {
                    return false;
                }
            }

        }
        return true;
    }

    /**
     * Set the move to the field
     *
     * @param move the move you want to make. Add a copy of the board because the board can change .
     */
    protected void doMove(Move move) {
        extracted(move, board);
        listPreviousBoardStates.add(board.CopyBoard());
    }


    private void extracted(Move move, Board board) {
        int row = move.getRow();
        int col = move.getCol();
        board.setField(row, col, move.getColour());
        if (board.isField(row + 1, col) && board.isSurrounded(row + 1, col)) {
            List<int[]> capturedValues = board.caputured(row + 1, col);
            for (int i = 0; i < capturedValues.size(); i++) {
                removeStone(capturedValues.get(i)[0], capturedValues.get(i)[1], board);
            }
        }
        if (board.isField(row - 1, col) && board.isSurrounded(row - 1, col)) {
            List<int[]> capturedValues = board.caputured(row - 1, col);
            for (int[] capturedValue : capturedValues) {
                removeStone(capturedValue[0], capturedValue[1], board);
            }

        }
        if (board.isField(row, col + 1) && board.isSurrounded(row, col + 1)) {
            List<int[]> capturedValues = board.caputured(row, col + 1);
            for (int[] capturedValue : capturedValues) {
                removeStone(capturedValue[0], capturedValue[1], board);
            }

        }
        if (board.isField(row, col - 1) && board.isSurrounded(row, col - 1)) {
            List<int[]> capturedValues = board.caputured(row, col - 1);
            for (int[] capturedValue : capturedValues) {
                removeStone(capturedValue[0], capturedValue[1], board);
            }
        }
        // This one should be last because enemy capture is first
        if (board.isSurrounded(row, col)) {
            List<int[]> capturedValues = board.caputured(row, col);
            for (int i = 0; i < capturedValues.size(); i++) {
                removeStone(capturedValues.get(i)[0], capturedValues.get(i)[1], board);
            }

        }
    }

    /**
     * will calculate the winner
     *
     * @return the colour of the winner
     */
    public StoneColour isWinner() {
        return StoneColour.EMPTY;
    }


    public List<Board> getListPreviousBoardStates() {
        return listPreviousBoardStates;
    }


    public Map<StoneColour, Integer> getScore() {
        int capturedByBlack = 0;
        int capturedByWhite = 0;
        List<Move> emptySpace = new ArrayList<>();
        for (int i = 0; i < Board.DIM; i++) {
            for (int j = 0; j < Board.DIM; j++) {
                StoneColour field = board.getField(i, j);
                if (field.equals(StoneColour.BLACK)) {
                    capturedByBlack++;
                } else if (field.equals(StoneColour.WHITE)) {
                    capturedByWhite++;
                } else {
                    emptySpace.add(new Move(i, j, StoneColour.EMPTY));
                }

            }
        }

        for (Move move : emptySpace) {
            StoneColour firstSide = findColourSide(move.getRow()+1, move.getCol(), true, true,false);
            StoneColour secondSide = findColourSide(move.getRow()-1, move.getCol(), true,false,false) ;
            if(!firstSide.equals(secondSide) && !firstSide.equals(StoneColour.EMPTY) && !secondSide.equals(StoneColour.EMPTY)) {
              continue;
            }
            StoneColour thirdSide = findColourSide(move.getRow(), move.getCol() + 1, false, false, true);
            if(!thirdSide.equals(StoneColour.EMPTY) && !(firstSide.equals(StoneColour.EMPTY)&&secondSide.equals(StoneColour.EMPTY))&& (!firstSide.equals(StoneColour.EMPTY) && !thirdSide.equals(firstSide))&& (!secondSide.equals(StoneColour.EMPTY) && !thirdSide.equals(secondSide)) ) {
                continue;
            }
            StoneColour fourthSide = findColourSide(move.getRow(), move.getCol() - 1, false, false, false);
            if(!fourthSide.equals(StoneColour.EMPTY )&& !(firstSide.equals(StoneColour.EMPTY)&&secondSide.equals(StoneColour.EMPTY)&&thirdSide.equals(StoneColour.EMPTY))&& (!firstSide.equals(StoneColour.EMPTY) && !fourthSide.equals(firstSide))&&(!secondSide.equals(StoneColour.EMPTY) && !fourthSide.equals(secondSide))&&(!thirdSide.equals(StoneColour.EMPTY) && !fourthSide.equals(thirdSide))){
                continue;
            }
            if (firstSide.equals(StoneColour.EMPTY)&& secondSide.equals(StoneColour.EMPTY)&&thirdSide.equals(StoneColour.EMPTY)&&fourthSide.equals(StoneColour.EMPTY)){
                continue;
            }
            if(!firstSide.equals(StoneColour.EMPTY)){
                if(firstSide.equals(StoneColour.WHITE)){

                    capturedByWhite++;
                }
                else{
                    capturedByBlack++;

                }

            }
            else if(!secondSide.equals(StoneColour.EMPTY)){
                if(secondSide.equals(StoneColour.WHITE)){
                    capturedByWhite++;
                }
                else{
                    capturedByBlack++;
                }

            }
            else if(!thirdSide.equals(StoneColour.EMPTY)){
                if(thirdSide.equals(StoneColour.WHITE)){
                    capturedByWhite++;
                }
                else{

                    capturedByBlack++;
                }
            }
            else{
                if(fourthSide.equals(StoneColour.WHITE)){
                    capturedByWhite++;
                }
                else{

                    capturedByBlack++;
                }

            }
        }
        Map<StoneColour, Integer> returnMap = new HashMap<>();
        returnMap.put(StoneColour.BLACK,capturedByBlack);
        returnMap.put(StoneColour.WHITE,capturedByWhite);
        return returnMap;
    }

    private void removeStone(int row, int col, Board board) {
        board.setField(row, col, StoneColour.EMPTY);

    }


    private StoneColour findColourSide(int row, int col, boolean horizontal, boolean rightSide, boolean down) {
        if (!board.isField(row, col)) {
            return StoneColour.EMPTY;
        } else if (board.getField(row, col).equals(StoneColour.WHITE)) {
            return StoneColour.WHITE;
        } else if (board.getField(row, col).equals(StoneColour.BLACK)) {
            return StoneColour.BLACK;
        } else { // is on the board not black not white so must be empty

            if(horizontal){
                if(rightSide){
                    return findColourSide(row+1,col,true,true, false) ;
                }
                else{
                    return findColourSide(row-1,col,true,false,false);
                }
            }
            else{
                if (down) {
                    return findColourSide(row, col + 1, false,  false, true);
                } else {
                    return findColourSide(row, col - 1, false,  false, false);
                }
            }

        }
    }

}
