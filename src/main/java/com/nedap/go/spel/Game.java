package com.nedap.go.spel;

import java.lang.reflect.Array;
import java.util.List;
import java.util.Stack;

public class Game {

    public Board board ;

    private List<Board> listPreviousBoardStates  = new Stack<>();


    /**
     * constructor
     * @param board the board the game is played on
     */
    public Game(Board board) {

        this.board=board;
    }


    /**
     * A game is ended if : there is no more space left on the board, there are two passes(indicated by null in listPreviousBoardStates ) or manually when someone left the game
     * @return true if the game should if ended
     */
    public boolean isGameover(){

        //if board is full the game is over
        if(board.isFull()){
            return true;
        }
        // if two consecutive passes game should end.
        if(listPreviousBoardStates.size()>1) {
            if (listPreviousBoardStates.get(listPreviousBoardStates.size() - 1) == null && listPreviousBoardStates.get(listPreviousBoardStates.size() - 2) == null) {
                return true;
            }
        }

        return false;
    }

    /**
     * Checks whether a move is actually a legal move.
     * A legal move is can only be on an empty space inside the board and may not recreate a previous board.
     * @return true if the move is a legal move
     */
    public boolean isValidMove(Move move){
        if(!board.isField(move.getRow(),move.getCol())){
            return false;
        }
        if(!board.isEmptyField(move.getRow(),move.getCol())){
            return false ;
        }
        return true;
    }

    /**
     * Set the move to the field
     * @param move the move you want to make
     */
    protected void doMove(Move move){
        int row = move.getRow() ;
        int col = move.getCol() ;
        board.setField(row,col,move.getColour());
       if (board.isSurrounded(row+1,col)){
           List<int[]> capturedValues = board.caputured(row + 1, col);
           for(int i = 0 ; i< capturedValues.size(); i++){
               removeStone(capturedValues.get(i)[0],capturedValues.get(i)[1]) ;
           }
       }
       if(board.isSurrounded(row-1,col)){
           List<int[]> capturedValues = board.caputured(row -1, col);
           for(int i = 0 ; i< capturedValues.size(); i++){
               removeStone(capturedValues.get(i)[0],capturedValues.get(i)[1]) ;
           }

       }
        if(board.isSurrounded(row,col+1)){
            List<int[]> capturedValues = board.caputured(row, col+1 );
            for(int i = 0 ; i< capturedValues.size(); i++){
                removeStone(capturedValues.get(i)[0],capturedValues.get(i)[1]) ;
            }

        }
        if(board.isSurrounded(row,col-1)){
            List<int[]> capturedValues = board.caputured(row, col-1);
            for(int i = 0 ; i< capturedValues.size(); i++){
                removeStone(capturedValues.get(i)[0],capturedValues.get(i)[1]) ;
            }

        }
        // This one should be last because enemy capture is first
        if(board.isSurrounded(row,col)){
            List<int[]> capturedValues = board.caputured(row , col);
            for(int i = 0 ; i< capturedValues.size(); i++){
                removeStone(capturedValues.get(i)[0],capturedValues.get(i)[1]) ;
            }

        }
        listPreviousBoardStates.add(board.CopyBoard());
    }

    /**
     * will calculate the winner
     * @return the collour of the winner
     */
    public StoneColour isWinner(){
        return StoneColour.EMPTY ;
    }


    public List<Board> getListPreviousBoardStates() {
        return listPreviousBoardStates;
    }

    private void removeStone(int row, int col){
        board.setField(row,col,StoneColour.EMPTY);

    }

}
