package com.nedap.go.spel;

import com.nedap.go.BoardCompare;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This is the class game.
 */

public class Game {

    private Board board;

    private List<Board> listPreviousBoardStates = new ArrayList<>();


    /**
     * constructor
     *
     * @param board the board the game is played on
     */
    public Game(Board board) {

        this.board = board;
    }


    public Board getBoard() {
        return board;
    }

    /**
     * A game is ended if : there is no more space left on the board, there are two passes(indicated by null in listPreviousBoardStates ) or manually when someone left the game
     *
     * @return true if the game should if ended
     */
    public boolean isGameOver() {

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
        if(move == null){
            return true; // this is a pass move
        }
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
            changeOnboardDoneByMove(move, copyBoard);
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
    public void doMove(Move move) {
        if(move == null){
            listPreviousBoardStates.add(null);
        }
        else {
            changeOnboardDoneByMove(move, board);

            listPreviousBoardStates.add(board.CopyBoard());
        }
    }

    /**
     * Gives a list of the changes needs to update the GUI
     * @param move move that is done
     * @return a list of the changes needs to update the GUI
     */
    public List<int[]> changesForGUI(Move move){
        Board copyBoard = board.CopyBoard();
        return changeOnboardDoneByMove(move, copyBoard);
    }

    /**
     * Makes the changes due to the move on the given board
     * @param move the move
     * @param board the board the move is on
     * @return a list of changes on the board
     */
    private List<int[]> changeOnboardDoneByMove(Move move, Board board) {
        int row = move.getRow();
        int col = move.getCol();
        List<int[]> alRemovedValues = new ArrayList<>();
        board.setField(row, col, move.getColour());
        if (board.isField(row + 1, col) && board.isSurrounded(row + 1, col)) {
            List<int[]> capturedValues = board.captured(row + 1, col);
            alRemovedValues.addAll(capturedValues);
            for (int[] capturedValue : capturedValues) {
                removeStone(capturedValue[0], capturedValue[1], board);

            }
        }
        if (board.isField(row - 1, col) && board.isSurrounded(row - 1, col)) {
            List<int[]> capturedValues = board.captured(row - 1, col);
            alRemovedValues.addAll(capturedValues);
            for (int[] capturedValue : capturedValues) {
                removeStone(capturedValue[0], capturedValue[1], board);
            }

        }
        if (board.isField(row, col + 1) && board.isSurrounded(row, col + 1)) {
            List<int[]> capturedValues = board.captured(row, col + 1);
            alRemovedValues.addAll(capturedValues);
            for (int[] capturedValue : capturedValues) {
                removeStone(capturedValue[0], capturedValue[1], board);
            }

        }
        if (board.isField(row, col - 1) && board.isSurrounded(row, col - 1)) {
            List<int[]> capturedValues = board.captured(row, col - 1);
            alRemovedValues.addAll(capturedValues);
            for (int[] capturedValue : capturedValues) {
                removeStone(capturedValue[0], capturedValue[1], board);
            }
        }
        // This one should be last because enemy capture is first
        if (board.isSurrounded(row, col)) {
            List<int[]> capturedValues = board.captured(row, col);
            alRemovedValues.addAll(capturedValues);
            for (int[] capturedValue : capturedValues) {
                removeStone(capturedValue[0], capturedValue[1], board);
            }

        }
        return alRemovedValues;
    }

    /**
     * will calculate the winner
     *
     * @return the colour of the winner
     */
    public StoneColour isWinner() {
        Map<StoneColour, Integer> score = getScore();
        if(score.get(StoneColour.WHITE)>score.get(StoneColour.BLACK)){
            return StoneColour.WHITE ;
        }
        else if(score.get(StoneColour.WHITE)<score.get(StoneColour.BLACK)){
            return StoneColour.BLACK;
        }
        else {
            return StoneColour.EMPTY;
        }
    }

    /**
     * gives a list of all the states the board has had
     * @return  a list of all the states the board has had
     */
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
            StoneColour firstSide = findColourSide(move.getRow()+1, move.getCol(), true, true,false,new ArrayList<>());
            if(firstSide == null){
                continue;
            }
            StoneColour secondSide = findColourSide(move.getRow()-1, move.getCol(), true,false,false,new ArrayList<>()) ;
            if(secondSide == null){
                continue;
            }
            // false if A=B || A=empty || B=empty ;
            // true if (A≠B && A≠empty && B≠empty ;
            if(!firstSide.equals(secondSide) && !firstSide.equals(StoneColour.EMPTY) && !secondSide.equals(StoneColour.EMPTY)) {
              continue;
            }
            StoneColour thirdSide = findColourSide(move.getRow(), move.getCol() + 1, false, false, true,new ArrayList<>());
            if(thirdSide == null){
                continue;
            }
            // false if C=empty || A=B=empty || ((A=empty || A=C) && (B=empty || B=C))
            // true if  C≠empty && (A≠empty || B≠empty) && (A≠empty && A≠C) || (B≠empty && B≠C)
            if(!thirdSide.equals(StoneColour.EMPTY) && !(firstSide.equals(StoneColour.EMPTY)&& secondSide.equals(StoneColour.EMPTY))&& ((!firstSide.equals(StoneColour.EMPTY) && !thirdSide.equals(firstSide)) || (!secondSide.equals(StoneColour.EMPTY) && !thirdSide.equals(secondSide)) )) {
                continue;
            }
//
            // false if D=empty || A=B=C=empty || ((A=empty || A=C) && (B=empty || B=C) && (C=empty || C=D))
            // true  if D≠empty && (A≠empty || B≠empty || C≠empty) && ((A≠empty && A≠D) || (B≠empty && B≠D) || (C≠empty && C≠D))
            StoneColour fourthSide = findColourSide(move.getRow(), move.getCol() - 1, false, false, false,new ArrayList<>());
            if(fourthSide == null){
                continue;
            }

            if(!fourthSide.equals(StoneColour.EMPTY )&& !(firstSide.equals(StoneColour.EMPTY)&&secondSide.equals(StoneColour.EMPTY)&& thirdSide.equals(StoneColour.EMPTY))&& ((!firstSide.equals(StoneColour.EMPTY) && !fourthSide.equals(firstSide)) || ( !secondSide.equals(StoneColour.EMPTY) && !fourthSide.equals(secondSide) )|| (!thirdSide.equals(StoneColour.EMPTY) && !fourthSide.equals(thirdSide)))){
                continue;
            }

            // false if A≠empty || B≠empty || C≠empty || D≠empty
            // true if A=B=C=D=empty
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


    /**
     * removes stones on a given board
     * @param row row
     * @param col col
     * @param board board that the stone is placed on
     */
    private void removeStone(int row, int col, Board board) {
        board.setField(row, col, StoneColour.EMPTY);

    }


    private StoneColour findColourSide(int row, int col, boolean horizontal, boolean rightSide, boolean down, List loopList) {
        if(loopList.contains(""+row+col)){
            return StoneColour.EMPTY ;
        }
        if (!board.isField(row, col)) {
            return StoneColour.EMPTY;
        } else if (board.getField(row, col).equals(StoneColour.WHITE)) {
            return StoneColour.WHITE;
        } else if (board.getField(row, col).equals(StoneColour.BLACK)) {
            return StoneColour.BLACK;
        } else { // is on the board not black not white so must be empty
            loopList.add(""+row+col);
            if(horizontal){
                if(rightSide){
                    StoneColour A = findColourSide(row+1,col,true,true, false, loopList) ;
                    StoneColour B = findColourSide(row,col-1,false,false, false, loopList) ;
                    StoneColour C = findColourSide(row,col+1,false,false, true, loopList) ;
                    List<StoneColour> test2 = new ArrayList<StoneColour>();
                    test2.add(A);
                    test2.add(B);
                    test2.add(C);
                    return testUpdatePuntentelling(test2);
                }
                else{
                    StoneColour A = findColourSide(row-1,col,true,false,false,loopList);
                    StoneColour B = findColourSide(row,col-1,false,true, false, loopList) ;
                    StoneColour C = findColourSide(row,col+1,false,false, true, loopList) ;
                    List<StoneColour> test2 = new ArrayList<StoneColour>();
                    test2.add(A);
                    test2.add(B);
                    test2.add(C);
                    return testUpdatePuntentelling(test2);
                }
            }
            else{
                if (down) {
                    StoneColour A = findColourSide(row+1,col,true,true,false,loopList);
                    StoneColour B = findColourSide(row-1,col,true,false, false, loopList) ;
                    StoneColour C = findColourSide(row,col+1,false,false, true, loopList) ;
                    List<StoneColour> test2 = new ArrayList<StoneColour>();
                    test2.add(A);
                    test2.add(B);
                    test2.add(C);
                    return testUpdatePuntentelling(test2);

                } else {
                    StoneColour A = findColourSide(row+1,col,true,true,false,loopList);
                    StoneColour B = findColourSide(row-1,col,true,false, false, loopList) ;
                    StoneColour C = findColourSide(row,col-1,false,false, false, loopList) ;
                    List<StoneColour> test2 = new ArrayList<StoneColour>();
                    test2.add(A);
                    test2.add(B);
                    test2.add(C);
                    return testUpdatePuntentelling(test2);

                }
            }

        }
    }

    private  StoneColour testUpdatePuntentelling(List<StoneColour> test2) {
        if(test2.contains(null)){
            return null;
        }
        if(test2.contains(StoneColour.WHITE) && test2.contains(StoneColour.BLACK)){
            return null;
        }
        else if(test2.contains(StoneColour.WHITE)){
            return StoneColour.WHITE;
        }
        else if(test2.contains(StoneColour.BLACK)){
            return StoneColour.BLACK;
        }
        else{
            return StoneColour.EMPTY;
        }
    }

    public List<int[]> getEmptyFields(){
        List<int[]> list = new ArrayList<int[]>();
        for(int i = 0 ; i<board.DIM ; i++){
            for(int j = 0 ; j <board.DIM ; j++){
                if(board.isEmptyField(i,j)){

                    list.add(new int[]{i,j}) ;
                }
            }
        }

        return list ;
   }

}
