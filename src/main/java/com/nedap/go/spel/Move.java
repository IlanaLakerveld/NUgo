package com.nedap.go.spel;

public class Move {

    private int row ;
    private int col ;
    private StoneColour colour ;


    public Move(int row, int col, StoneColour colour) {
        this.col = col ;
        this.row =row;
        this.colour=colour;

    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public StoneColour getColour() {
        return colour;
    }
}
