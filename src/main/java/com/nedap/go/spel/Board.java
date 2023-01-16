package com.nedap.go.spel;

import java.util.ArrayList;
import java.util.List;

public class Board {

    private StoneColour[][] fields;
    public static final int DIM = 9;


    /**
     * Constructor
     * Makes an empty board
     */
    public Board() {
        fields = new StoneColour[DIM][DIM];
        for (int i = 0; i < DIM; i++) {
            for (int j = 0; j < DIM; j++) {
                fields[i][j] = StoneColour.EMPTY;
            }
        }
    }


    /**
     * Makes a copy of the board
     *
     * @return A copy of the current board
     */
    public Board CopyBoard() {
        Board copyBoard = new Board();
        for (int i = 0; i < DIM; i++) {
            for (int j = 0; j < DIM; j++) {
                copyBoard.fields[i][j] = this.getField(i, j);
            }

        }
        return copyBoard;

    }


    /**
     * @param row row
     * @param col colum
     * @return returns what is on that place on the board, can either be empty, black or white.
     */
    /*@ requires row >= 0 && row < DIM;
    requires col >= 0 && row < DIM;
     @*/
    public StoneColour getField(int row, int col) {
        return fields[row][col];

    }


    /**
     *
     * @param row row
     * @param col column
     * @return true is the field is part of the board
     */
    public boolean isField(int row, int col) {
        if (row >= 0 && row < DIM) {
            if (col >= 0 && col < DIM) {
                return true;
            }
        }
        return false;
    }



    /**
     * Returns true if field is empty false if field is taken
     *
     * @param row row
     * @param col column
     * @return true if field is empty false if field is taken
     */
    /*@ requires row >= 0 && row < DIM;
    requires col >= 0 && row < DIM;
     @*/
    public boolean isEmptyField(int row, int col) {
        if (getField(row, col) != StoneColour.EMPTY) {
            return false;
        }
        return true;
    }


    /**
     * Checks the board if full
     *
     * @return true is the board is full false otherwise
     */
    public boolean isFull() {
        for (int i = 0; i < DIM; i++) {
            for (int j = 0; j < DIM; j++) {
                if (getField(i, j) == StoneColour.EMPTY) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * set stone on the board
     *
     * @param row row
     * @param col colum
     * @param stone Either black or white depending on the player
     */
    public void setField(int row, int col, StoneColour stone) {
        fields[row][col] = stone;
    }


    /**
     * @param row row
     * @param col column
     * @return true if and only if all the side are either edge or the colour of the other person
     */
    public boolean surrounded(int row, int col) {
        StoneColour stone = getField(row, col);
        if (verzinnaam(row, col, stone, true, true) && verzinnaam(row, col, stone, true, false) && verzinnaam(row, col, stone, false, true) && verzinnaam(row, col, stone, false, false)) {
            return true;
        }
        return false;

    }

    public List<int[]> caputured(int row, int col) {
        List<int[]> list= new ArrayList<int[]>();
        // check if is actually surrounded
        if(!surrounded(row,col)){
            return null ;
        }
        // check all directions
        list.addAll(extracted(row, col,true));
        list.addAll(extracted(row,col,false));
        list.addAll(extractedcol(row,col,false));
        list.addAll(extractedcol(row,col,true));

        return list;
    }

    private List<int[]> extractedcol(int row, int col, Boolean up){
        List<int[]> list= new ArrayList<int[]>();
        boolean sameColour = true;
        int counter ;
        if (up){
            counter= 1 ;
        }
        else{
            counter = -1 ;
        }
        while(sameColour){
            if(isField(row,col+counter)&& getField(row,col)==getField(row,col+counter)){
                int[] thisField = new int[2] ;
                thisField[0]= row  ;
                thisField[1]= col+counter;
                list.add(0,thisField);
                if(up){
                    counter++ ;
                }
                else{
                    counter-- ;
                }
            }
            else{
                sameColour=false ;
            }
        }
        return list ;
    }

    private List<int[]>  extracted(int row, int col, Boolean right) {
        List<int[]> list= new ArrayList<int[]>();
        boolean sameColour =true;
        int counter ;
        if(right){
            counter = 1;
        }
        else{
            counter = -1 ;
        }
        while(sameColour){
            if(isField(row +counter, col) && getField(row, col) == getField(row +counter, col)){
                int[] thisField = new int[2] ;
                thisField[0]= row +counter ;
                thisField[1]= col;
                list.add(0,thisField);
                if(right) {
                    counter++;
                }
                else{
                    counter--;
                }
            }
            else{
                sameColour=false ;
            }
        }
        return list ;
    }


    /**
     * check if the on one side if "captured"
     *
     * @param row row
     * @param col column
     * @param stone    stone colour of this stone .
     * @param positive true if you want to look at the upper of right side false if you want to check on the right side
     * @param rowcount true if you want to check on the rows false if you want to check on columns
     * @return true if on that side the stone is captured i.e. his stone(s) row is followed by either the edge or a stone on the negative side
     */
    private boolean verzinnaam(int row, int col, StoneColour stone, boolean positive, boolean rowcount) {
        int next;
        if (positive) {
            next = 1;
        } else {
            next = -1;
        }

        if (rowcount) {
            if (!isField(row + next,col)) {
                return true;
            } else if (getField(row + next, col) == StoneColour.EMPTY) {
                return false;
            } else if (getField(row + next, col) != StoneColour.EMPTY && getField(row + next, col) != stone) {
                return true;

            } else {
                return(verzinnaam(row + next, col, stone, positive, rowcount));
            }

        }
        else  { //(!rowcount)
            if (!isField(row, col + 1)) {
                return true;
            } else if (getField(row, col + 1) == StoneColour.EMPTY) {
                return false;
            } else if (getField(row, col + 1) != StoneColour.EMPTY && getField(row, col + 1) != stone) {
                return true;
            } else {
                return(verzinnaam(row, col + 1, stone, positive, rowcount));
            }

        }


    }






}
