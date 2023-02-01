package com.nedap.go.spel;

import com.nedap.go.BoardCompare;


import java.util.*;

/**
 * This is the class game. The rules of the game are in this class.
 */

public class Game {

    private final Board board;

    private List<Board> listPreviousBoardStates = new ArrayList<>();


    /**
     * Constructor
     *
     * @param board the board the game is played on
     */
    public Game(Board board) {

        this.board = board;
    }

    /**
     * Get function
     * @return returns the board.
     */
    public Board getBoard() {
        return board;
    }

    /**
     * Checks if there is a reason the game is over;
     * A game is ended if : there is no more space left on the board, there are two passes(indicated by null in listPreviousBoardStates ) or manually when someone left the game
     *
     * @return true if its game over
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
        if (move == null) {
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
            Board copyBoard = board.copyBoard();
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
     * Set the move to the field.
     *
     * @param move the move you want to make. Add a copy of the board because the board can change .
     */
    public void doMove(Move move) {
        if (move == null) {
            listPreviousBoardStates.add(null);
        } else {
            changeOnboardDoneByMove(move, board);

            listPreviousBoardStates.add(board.copyBoard());
        }
    }

    /**
     * Gives a list of the changes needs to update the GUI.
     *
     * @param move move that is done
     * @return a list of the changes needs to update the GUI.
     */
    public List<int[]> changesForGUI(Move move) {
        Board copyBoard = board.copyBoard();
        return changeOnboardDoneByMove(move, copyBoard);
    }

    /**
     * Makes the changes due to a move on the given board and safes the changed fields on the list.
     *
     * @param move  the move
     * @param board the board the move is on
     * @return a list of changes on the board
     */
    public List<int[]> changeOnboardDoneByMove(Move move, Board board) {
        int row = move.getRow();
        int col = move.getCol();
        List<int[]> alRemovedValues = new ArrayList<>();
        board.setField(row, col, move.getColour());

        if (board.isField(row + 1, col) && board.isSurrounded(row + 1, col)) {
            alRemovedValues.addAll(valueRemover(board, row + 1, col));
        }
        if (board.isField(row - 1, col) && board.isSurrounded(row - 1, col)) {
            alRemovedValues.addAll(valueRemover(board, row - 1, col));

        }
        if (board.isField(row, col + 1) && board.isSurrounded(row, col + 1)) {
            alRemovedValues.addAll(valueRemover(board, row, col + 1));
        }

        if (board.isField(row, col - 1) && board.isSurrounded(row, col - 1)) {
            alRemovedValues.addAll(valueRemover(board, row, col - 1));
        }
        // This one should be last because enemy capture is first
        if (board.isSurrounded(row, col)) {
            alRemovedValues.addAll(valueRemover(board, row, col));
        }
        return alRemovedValues;
    }

    /**
     * Removes values of the board.
     *
     * @param board board move is on
     * @param row   row
     * @param col   col
     * @return list of removed values
     */
    private List<int[]> valueRemover(Board board, int row, int col) {
        List<int[]> capturedValues = board.captured(row, col);
        List<int[]> alRemovedValues = new ArrayList<>(capturedValues);
        for (int[] capturedValue : capturedValues) {
            removeStone(capturedValue[0], capturedValue[1], board);

        }
        return alRemovedValues;
    }

    /**
     * Will calculate the winner.
     *
     * @return the colour of the Winner
     */
    public StoneColour isWinner() {
        Map<StoneColour, Integer> score = getScore();
        if (score.get(StoneColour.WHITE) > score.get(StoneColour.BLACK)) {
            return StoneColour.WHITE;
        } else if (score.get(StoneColour.WHITE) < score.get(StoneColour.BLACK)) {
            return StoneColour.BLACK;
        } else {
            return StoneColour.EMPTY;
        }
    }

    /**
     * gives a list of all the states the board has had
     *
     * @return a list of all the states the board has had
     */
    public List<Board> getListPreviousBoardStates() {
        return listPreviousBoardStates;
    }

    /**
     * Calculates the score based on  the area rule.
     * It first checks how many stones both players have on the board after that it checks how many space the players have captured.
     *
     * @return a map with the stoneColour and the amount of points the player has.
     */
    public Map<StoneColour, Integer> getScore() {
        // this part counts how many stones there are on the board and create a list with empty spaces
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

        // This part is will check for each space if this is captured.
        for (Move move : emptySpace) {
            StoneColour firstSide = findSideColour(move, Sides.RIGHT);
            if (firstSide == null) {
                continue;
            }
            StoneColour secondSide = findSideColour(move, Sides.LEFT);
            if (secondSide == null) {
                continue;
            }
            // false if A=B || A=empty || B=empty ;
            // true if (A≠B && A≠empty && B≠empty ;
            if (!firstSide.equals(secondSide) && !firstSide.equals(StoneColour.EMPTY) && !secondSide.equals(StoneColour.EMPTY)) {
                continue;
            }

            StoneColour thirdSide = findSideColour(move, Sides.DOWN);
            if (thirdSide == null) {
                continue;
            }
            // false if C=empty || A=B=empty || ((A=empty || A=C) && (B=empty || B=C))
            // true if  C≠empty && (A≠empty || B≠empty) && (A≠empty && A≠C) || (B≠empty && B≠C)
            if (!thirdSide.equals(StoneColour.EMPTY) && !(firstSide.equals(StoneColour.EMPTY) && secondSide.equals(StoneColour.EMPTY)) && ((!firstSide.equals(StoneColour.EMPTY) && !thirdSide.equals(firstSide)) || (!secondSide.equals(StoneColour.EMPTY) && !thirdSide.equals(secondSide)))) {
                continue;
            }


            StoneColour fourthSide = findSideColour(move, Sides.UP);
            if (fourthSide == null) {
                continue;
            }

            // false if D=empty || A=B=C=empty || ((A=empty || A=C) && (B=empty || B=C) && (C=empty || C=D))
            // true  if D≠empty && (A≠empty || B≠empty || C≠empty) && ((A≠empty && A≠D) || (B≠empty && B≠D) || (C≠empty && C≠D))
            if (!fourthSide.equals(StoneColour.EMPTY) && !(firstSide.equals(StoneColour.EMPTY) && secondSide.equals(StoneColour.EMPTY) && thirdSide.equals(StoneColour.EMPTY)) && ((!firstSide.equals(StoneColour.EMPTY) && !fourthSide.equals(firstSide)) || (!secondSide.equals(StoneColour.EMPTY) && !fourthSide.equals(secondSide)) || (!thirdSide.equals(StoneColour.EMPTY) && !fourthSide.equals(thirdSide)))) {
                continue;
            }

            // false if A≠empty || B≠empty || C≠empty || D≠empty
            // true if A=B=C=D=empty
            if (firstSide.equals(StoneColour.EMPTY) && secondSide.equals(StoneColour.EMPTY) && thirdSide.equals(StoneColour.EMPTY) && fourthSide.equals(StoneColour.EMPTY)) {
                continue;
            }


            // has has one and only one colour only need to know which colour, so you can add it.
            if (isCapturedByWhite(firstSide, secondSide, thirdSide, fourthSide)) {
                capturedByWhite++;
            } else {
                capturedByBlack++;
            }
        }
        // make the return map
        return getStoneColourIntegerMap(capturedByBlack, capturedByWhite);
    }


    /**
     * Creates a returns map with stone colours and the amount of point this colour has
     *
     * @param capturedByBlack amount points for  black.
     * @param capturedByWhite amount points for white.
     * @return a map with colour and amount of captured fields.
     */
    private Map<StoneColour, Integer> getStoneColourIntegerMap(int capturedByBlack, int capturedByWhite) {
        Map<StoneColour, Integer> returnMap = new HashMap<>();
        returnMap.put(StoneColour.BLACK, capturedByBlack);
        returnMap.put(StoneColour.WHITE, capturedByWhite);
        return returnMap;
    }


    /**
     * Is used by getScore. When know only one colour is true for one of the four sides this function finds out which colour.
     *
     * @param firstSide  side one
     * @param secondSide side two
     * @param thirdSide  side three
     * @param fourthSide side four
     * @return true if colour is white false if colour is black.
     */
    private boolean isCapturedByWhite(StoneColour firstSide, StoneColour secondSide, StoneColour thirdSide, StoneColour fourthSide) {
        if (!firstSide.equals(StoneColour.EMPTY)) {
            if (firstSide.equals(StoneColour.WHITE)) {
                return true;
            } else {
                return false;
            }

        } else if (!secondSide.equals(StoneColour.EMPTY)) {
            if (secondSide.equals(StoneColour.WHITE)) {
                return true;
            } else {
                return false;
            }

        } else if (!thirdSide.equals(StoneColour.EMPTY)) {
            if (thirdSide.equals(StoneColour.WHITE)) {
                return true;
            } else {
                return false;
            }
        } else {
            if (fourthSide.equals(StoneColour.WHITE)) {
                return true;
            } else {
                return false;
            }

        }
    }


    /**
     * Removes stones on a given board.
     *
     * @param row   row
     * @param col   col
     * @param board board that the stone is placed on.
     */
    private void removeStone(int row, int col, Board board) {
        board.setField(row, col, StoneColour.EMPTY);

    }

    /**
     * Gives the right input for the function findColourSide() for a side.
     *
     * @param move the field
     * @param side the side you are looking at
     * @return stoneColour of the side (output of the function findColourSide()
     */
    private StoneColour findSideColour(Move move, Sides side) {

        return switch (side) {
            case RIGHT -> findColourSide(move.getRow() + 1, move.getCol(), Sides.RIGHT, new ArrayList<>());
            case LEFT -> findColourSide(move.getRow() - 1, move.getCol(), Sides.LEFT, new ArrayList<>());
            case DOWN -> findColourSide(move.getRow(), move.getCol() + 1, Sides.DOWN, new ArrayList<>());
            case UP -> findColourSide(move.getRow(), move.getCol() - 1, Sides.UP, new ArrayList<>());
        };
    }

    /**
     * Checks which stoneColour the neighbour has. and if it is empty it check the neighbour neighbours.
     * If empty it checks three sides of the neighbour (the fourth side is the stone you started with)
     *
     * @param row row
     * @param col col
     * @param side enum side which is on
     * @param loopList list needed to prevent an endless loop
     * @return one of this options null/white/black/empty
     */
    private StoneColour findColourSide(int row, int col, Sides side, List<String> loopList) {
        if (loopList.contains("" + row + col)) {
            return StoneColour.EMPTY; // already seen this stones return empty to prevent looping
        }
        if (!board.isField(row, col)) {
            return StoneColour.EMPTY;
        } else if (board.getField(row, col).equals(StoneColour.WHITE)) {
            return StoneColour.WHITE;
        } else if (board.getField(row, col).equals(StoneColour.BLACK)) {
            return StoneColour.BLACK;
        } else { // is on the board not black not white so must be empty
            loopList.add("" + row + col);
            List<StoneColour> listOfStoneColourOfThreeSides = new ArrayList<>();
            if (side.equals(Sides.RIGHT)) {
                StoneColour A = findColourSide(row + 1, col, Sides.RIGHT, loopList);
                StoneColour B = findColourSide(row, col - 1, Sides.UP, loopList);
                StoneColour C = findColourSide(row, col + 1, Sides.DOWN, loopList);
                Collections.addAll(listOfStoneColourOfThreeSides, A, B, C);
            } else if (side.equals(Sides.LEFT)) {
                StoneColour A = findColourSide(row - 1, col, Sides.LEFT, loopList);
                StoneColour B = findColourSide(row, col - 1, Sides.UP, loopList);
                StoneColour C = findColourSide(row, col + 1, Sides.DOWN, loopList);
                Collections.addAll(listOfStoneColourOfThreeSides, A, B, C);
            } else if (side.equals(Sides.DOWN)) {
                StoneColour A = findColourSide(row + 1, col, Sides.RIGHT, loopList);
                StoneColour B = findColourSide(row - 1, col, Sides.LEFT, loopList);
                StoneColour C = findColourSide(row, col + 1, Sides.DOWN, loopList);
                Collections.addAll(listOfStoneColourOfThreeSides, A, B, C);

            } else if (side.equals(Sides.UP)) {
                StoneColour A = findColourSide(row + 1, col, Sides.RIGHT, loopList);
                StoneColour B = findColourSide(row - 1, col, Sides.LEFT, loopList);
                StoneColour C = findColourSide(row, col - 1, Sides.UP, loopList);
                Collections.addAll(listOfStoneColourOfThreeSides, A, B, C);

            }
            return colourCheckSides(listOfStoneColourOfThreeSides);

        }

    }


    /**
     * Checks if the list has more than one colour && has oneColour
     *
     * @param listOfStoneColourOfThreeSides stoneColour of three sides
     * @return null/white/black/empty
     */
    private StoneColour colourCheckSides(List<StoneColour> listOfStoneColourOfThreeSides) {
        if (listOfStoneColourOfThreeSides.contains(null)) {
            return null;
        }
        if (listOfStoneColourOfThreeSides.contains(StoneColour.WHITE) && listOfStoneColourOfThreeSides.contains(StoneColour.BLACK)) {
            return null;
        } else if (listOfStoneColourOfThreeSides.contains(StoneColour.WHITE)) {
            return StoneColour.WHITE;
        } else if (listOfStoneColourOfThreeSides.contains(StoneColour.BLACK)) {
            return StoneColour.BLACK;
        } else {
            return StoneColour.EMPTY;
        }
    }


    /**
     * Checks which fields are empty on the board
     *
     * @return a list of empty fields (row, col)
     */
    public List<int[]> getEmptyFields() {
        List<int[]> list = new ArrayList<>();
        for (int i = 0; i < Board.DIM; i++) {
            for (int j = 0; j < Board.DIM; j++) {
                if (board.isEmptyField(i, j)) {

                    list.add(new int[]{i, j});
                }
            }
        }

        return list;
    }

}
