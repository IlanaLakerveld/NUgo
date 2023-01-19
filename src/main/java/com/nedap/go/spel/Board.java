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
    protected void setField(int row, int col, StoneColour stone) {
        fields[row][col] = stone;
    }


    /**
     * @param row row
     * @param col column
     * @return true if and only if all the side are either edge or the colour of the other person
     */
    public boolean isSurrounded(int row, int col) {
        StoneColour stone = getField(row, col);
        // check al the neighbours of the stone
        if (checkerSideAreCaptured(row+1, col, stone, true, true,false) && checkerSideAreCaptured(row-1, col, stone, true, false,false) && checkerSideAreCaptured(row, col+1, stone, false, false,true) && checkerSideAreCaptured(row, col-1, stone, false, false,false)) {
            return true;
        }
        return false;

    }

    /**
     * Creates a list of arrays(row,col) that are fields that are captured
     * @param row row of the stone that is caputured/surrounded
     * @param col column of the stone that is captured/surrounded
     * @return list of arrays(row,col) that are surrounded.
     */
    public List<int[]> caputured(int row, int col) {
        List<int[]> list= new ArrayList<int[]>();
        // check if is actually surrounded
        if(!isSurrounded(row,col)){
            return null ;
        }
        // check all directions
        list.add(new int[]{row, col});
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
                int[] thisField = new int[]{row,col+counter} ;
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
                int[] thisField = new int[]{row +counter,col} ;
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





    /**  check if the on one side if "captured"
     *
     * @param row row of the field you want to check
     * @param col col of the field you want to check
     * @param stone sstone colour of this stone
     * @param horizontal true is you want to check of the rows, false if you want to check the columns
     * @param rightSide true if you want to check the right side, false is you want to check the left side
     * @param down true is you want to check the colums false is you want to check the rows
     * @return true if on that side the stone is captured i.e. his stone(s) row is followed by either the edge or a stone on the negative side
     */
    private boolean checkerSideAreCaptured(int row, int col, StoneColour stone, boolean horizontal, boolean rightSide, boolean down) {

            if (!isField(row ,col)) {
              return true;
            }
            else if (getField(row, col) == StoneColour.EMPTY) {
                return false;
            }
            else if (getField(row , col) != StoneColour.EMPTY && getField(row , col) != stone) {
                return true;
            }
            else {
                if(horizontal){
                    if(rightSide){
                        return checkerSideAreCaptured(row+1,col,stone,true,true, false) ;
                    }
                    else{
                        return checkerSideAreCaptured(row-1,col,stone,true,false,false);
                    }
                }
                else{
                    if (down) {
                        return checkerSideAreCaptured(row, col + 1, stone,false,  false, true);
                    } else {
                        return checkerSideAreCaptured(row, col - 1, stone,false,  false, false);
                    }
                }
            }
    }






}
