package com.nedap.go.spel;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * This is the class board contains the board logic.
 */

public class Board {

    private StoneColour[][] fields;

    /**
     *  Dimensions of the field (row and col are of size DIM)
     */
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
    public Board copyBoard() {
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
    public StoneColour getField(int row, int col) {
        return fields[row][col];

    }


    /**
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
     * Set stone on the board
     *
     * @param row   row
     * @param col   colum
     * @param stone Either black or white depending on the player
     */
    protected void setField(int row, int col, StoneColour stone) {
        fields[row][col] = stone;
    }


    /**
     * Checks if a stone is surrounded.
     * @param row row
     * @param col column
     * @return true if and only if all the side are either on the edge or are the colour of the other person
     */
    public boolean isSurrounded(int row, int col) {
        StoneColour stone = getField(row, col);
        // check al the neighbours of the stone

        if (checkerSideAreCaptured(row + 1, col, stone, Sides.RIGHT, new ArrayList<>()) && checkerSideAreCaptured(row - 1, col, stone, Sides.LEFT, new ArrayList<>()) && checkerSideAreCaptured(row, col + 1, stone, Sides.DOWN, new ArrayList<>()) && checkerSideAreCaptured(row, col - 1, stone, Sides.UP, new ArrayList<>())) {
            return true;
        }
        return false;

    }

    /**
     * Creates a list of arrays(row,col) that are fields that are captured
     *
     * @param row row of the stone that is captured/surrounded
     * @param col column of the stone that is captured/surrounded
     * @return list of arrays(row,col) that are surrounded.
     */
    public List<int[]> captured(int row, int col) {
        List<int[]> list = new ArrayList<>();
        // check if is actually surrounded
        if (!isSurrounded(row, col)) {
            return null;
        }

        StoneColour colour = getField(row, col);
        // check all directions
        list.add(new int[]{row, col});

        list.addAll(listOfFieldThatNeedsToBeRemoved(row + 1, col, colour, new ArrayList<>()));
        list.addAll(listOfFieldThatNeedsToBeRemoved(row - 1, col, colour, new ArrayList<>()));
        list.addAll(listOfFieldThatNeedsToBeRemoved(row, col + 1, colour, new ArrayList<>()));
        list.addAll(listOfFieldThatNeedsToBeRemoved(row, col - 1, colour, new ArrayList<>()));

        // Because it is an array you can not simply see the duplicates,
        // therefore first made it a string then you can see the duplicates and can remove them with a hashmap after removing the hashmap is returned to an array of strings
        List<String> listString = new ArrayList<>();
        for (int[] ints : list) {
            listString.add("" + ints[0] + "~" + ints[1]);
        }
        List<String> listWithoutDuplicatesString = new ArrayList<>(new HashSet<>(listString));
        List<int[]> listWithoutDuplicates = new ArrayList<>();
        for (String stringLine : listWithoutDuplicatesString) {
            String[] s = stringLine.split("~");
            listWithoutDuplicates.add(new int[]{Integer.parseInt(s[0]), Integer.parseInt(s[1])});
        }
        return listWithoutDuplicates;
    }

    /**
     * Creates a list of stones that need to be removed
     * @param row      row position of the place you are looking at
     * @param col      col position of the place you are looking at
     * @param colour   colour of the captured stone
     * @param loopList This is a list created to contain places the function has been to prevent infinite loops.
     * @return a list of fields that need to be removed
     */
    private List<int[]> listOfFieldThatNeedsToBeRemoved(int row, int col, StoneColour colour, List<String> loopList) {
        List<int[]> list = new ArrayList<>();
        if (!isField(row, col) || getField(row, col) != colour) {
            return list;
        }
        list.add(new int[]{row, col});
        if (!loopList.contains("" + (row + 1) + col)) {
            loopList.add("" + (row + 1) + col);
            list.addAll(listOfFieldThatNeedsToBeRemoved(row + 1, col, colour, loopList));
        }
        if (!loopList.contains("" + (row - 1) + col)) {
            loopList.add("" + (row - 1) + col);
            list.addAll(listOfFieldThatNeedsToBeRemoved(row - 1, col, colour, loopList));
        }
        if (!loopList.contains("" + row + (col + 1))) {
            loopList.add("" + row + (col + 1));
            list.addAll(listOfFieldThatNeedsToBeRemoved(row, col + 1, colour, loopList));
        }
        if (!loopList.contains("" + row + (col - 1))) {
            loopList.add("" + row + (col - 1));
            list.addAll(listOfFieldThatNeedsToBeRemoved(row, col - 1, colour, loopList));
        }
        return list;
    }


    /**
     * Check if the on one side if "captured"
     * if the stone is the same colour then you need to check the next stone, you check on three sides not of 4 because you know already the value of field from the previous stone.
     *
     * @param row         row of the field you want to check
     * @param col         col of the field you want to check
     * @param stone       stone colour of this stone
     * @param side        the side that you are looking at
     * @param loopChecker saves places where you have been so that you don't have an infinite loop
     * @return true if on that side the stone is captured i.e. his stone(s) row is followed by either the edge or a stone on the negative side
     */

    private boolean checkerSideAreCaptured(int row, int col, StoneColour stone, Sides side, List<String> loopChecker) {

        if (!isField(row, col)) {
            return true;
        } else if (getField(row, col) == StoneColour.EMPTY) {
            return false;
        } else if (getField(row, col) != StoneColour.EMPTY && getField(row, col) != stone) {
            return true;

        } else if (loopChecker.contains("" + row + col)) {
            return true;

        } else {
            loopChecker.add("" + row + col);
            if (side.equals(Sides.RIGHT)) {
                if (checkerSideAreCaptured(row + 1, col, stone, Sides.RIGHT, loopChecker) && checkerSideAreCaptured(row, col + 1, stone, Sides.DOWN, loopChecker) && checkerSideAreCaptured(row, col - 1, stone, Sides.UP, loopChecker)) {
                    return true;
                } else {
                    return false;
                }

            } else if (side.equals(Sides.LEFT)) {
                if (checkerSideAreCaptured(row - 1, col, stone, Sides.LEFT, loopChecker) && checkerSideAreCaptured(row, col + 1, stone, Sides.DOWN, loopChecker) && checkerSideAreCaptured(row, col - 1, stone, Sides.UP, loopChecker)) {
                    return true;
                } else {
                    return false;
                }
            } else if (side.equals(Sides.DOWN)) {

                if (checkerSideAreCaptured(row + 1, col, stone, Sides.RIGHT, loopChecker) && checkerSideAreCaptured(row - 1, col, stone, Sides.LEFT, loopChecker) && checkerSideAreCaptured(row, col + 1, stone, Sides.DOWN, loopChecker)) {
                    return true;
                } else {
                    return false;
                }
            } else {   // if (side.equals(Sides.UP)) {
                if (checkerSideAreCaptured(row + 1, col, stone, Sides.RIGHT, loopChecker) && checkerSideAreCaptured(row - 1, col, stone, Sides.LEFT, loopChecker) && checkerSideAreCaptured(row, col - 1, stone, Sides.UP, loopChecker)) {
                    return true;
                } else {
                    return false;
                }
            }
        }

    }


}
