package com.nedap.go.client;

import com.nedap.go.spel.Board;
import com.nedap.go.spel.Move;
import com.nedap.go.spel.StoneColour;

import java.util.List;
import java.util.Map;

/**
 * This is the class computer player. This extends player.
 */
public class ComputerPlayer extends Player {
    /**
     * Constructor
     *
     * @param name name of the player
     */
    public ComputerPlayer(String name, StoneColour colour) {
        super(name, colour);
    }


    /**
     * Determines the move and check if this is possible.
     * @return a move
     */
    @Override
    public Move determineMove() {

        if (canIWinNextMove())
            return null; // you have more points than the other player and if you pass know the game will be over


        List<int[]> possibleMoves = game.getEmptyFields();
        return findLittleSmarterMove(possibleMoves);


    }

    /**
     * If both players pass the game is finished, if you have more points than the other person it is a nice moment to stop
     *
     * @return true if you should pass
     */
    private boolean canIWinNextMove() {
        if (game.getListPreviousBoardStates().size() > 0 && game.getListPreviousBoardStates().get(game.getListPreviousBoardStates().size() - 1) == null) {
            Map<StoneColour, Integer> score = game.getScore();
            if (colour.equals(StoneColour.WHITE) && score.get(StoneColour.WHITE) > score.get(StoneColour.BLACK)) {
                return true;
            } else if (colour.equals(StoneColour.BLACK) && score.get(StoneColour.WHITE) < score.get(StoneColour.BLACK)) {
                return true;
            }
        }
        return false;
    }


    /**
     * Gives a random valid move. If there is no valid move it returns null
     *
     * @param possibleMoves list empty fields
     * @return a valid move (or null if there is no valid moves)
     */
    private Move findStupidMove(List<int[]> possibleMoves) {

        if (possibleMoves.size() > 0) {
            int randomInt = (int) (Math.random() * possibleMoves.size());
            Move move = new Move(possibleMoves.get(randomInt)[0], possibleMoves.get(randomInt)[1], colour);
            if (game.isValidMove(move)) {
                return move;
            } else {
                possibleMoves.remove(randomInt);
                return findStupidMove(possibleMoves);
            }

        } else {
            return null;
        }
    }

    /**
     * Check what the impact of his move (ONLY HIS) if on the board and depending on that it makes a decision :
     *
     * @param possibleMoves list of empty fields.
     * @return a move
     */
    private Move findLittleSmarterMove(List<int[]> possibleMoves) {
        if (possibleMoves.size() < 1) {
            return null;
        }
        QualityOfMove quality = QualityOfMove.EMPTY;
        Move bestMoveToMake = null;
        int amountCaputured = 0;

        for (int[] currentMove : possibleMoves) {
            Move move = new Move(currentMove[0], currentMove[1], colour);

            if (!game.isValidMove(move)) { // First check if the move is legal
                continue;
            }
            Board copyBoard = game.getBoard().copyBoard();
            List<int[]> changes = game.changeOnboardDoneByMove(move, copyBoard);
            if (changes.size() > 0) {
                int positiveOrNegativeEffect = 0;
                for (int[] changedFields : changes) {
                    if (game.getBoard().getField(changedFields[0], changedFields[1]).equals(colour)) {
                        positiveOrNegativeEffect--; // a stone of you is captured
                    } else {
                        positiveOrNegativeEffect++; // you capture a stone
                    }
                }
                if (positiveOrNegativeEffect > 0) {
                    if (quality.equals(QualityOfMove.GOOD)) {
                        if (amountCaputured > positiveOrNegativeEffect) {
                            bestMoveToMake = move;
                            amountCaputured = positiveOrNegativeEffect;
                        }
                    } else {
                        bestMoveToMake = move;
                        quality = QualityOfMove.GOOD;
                        amountCaputured = positiveOrNegativeEffect;
                    }
                } else if (positiveOrNegativeEffect < 0) {
                    if (quality.equals(QualityOfMove.EMPTY)) {
                        bestMoveToMake = move;
                        quality = QualityOfMove.BAD;
                    }
                } else {
                    if (quality.equals(QualityOfMove.EMPTY) || quality.equals(QualityOfMove.BAD)) {
                        bestMoveToMake = move;
                        quality = QualityOfMove.NEUTRAL;
                    } else if (quality.equals(QualityOfMove.NEUTRAL)) {
                        double randomDouble = Math.random();
                        if (randomDouble < 0.1) { // a random factor otherwise it always the first move
                            bestMoveToMake = move;
                        }
                    }
                }
            } else {
                if (quality.equals(QualityOfMove.EMPTY) || quality.equals(QualityOfMove.BAD)) {
                    bestMoveToMake = move;
                    quality = QualityOfMove.NEUTRAL;
                } else if (quality.equals(QualityOfMove.NEUTRAL)) {
                    double randomDouble = Math.random();
                    if (randomDouble < 0.1) { // a random factor otherwise it always the first move
                        bestMoveToMake = move;
                    }
                }

            }

        }

        if (quality.equals(QualityOfMove.NEUTRAL) || quality.equals(QualityOfMove.GOOD)) {
            return bestMoveToMake;
        } else {
            return null;
        }
    }
}
