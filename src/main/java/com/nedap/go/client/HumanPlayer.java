package com.nedap.go.client;

import com.nedap.go.spel.Move;
import com.nedap.go.spel.StoneColour;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

/**
 * This is the class Human player. This extends the class player.
 */
public class HumanPlayer extends Player {

    private BufferedReader reader;


    /**
     * Constructor
     *
     * @param name name of the player
     */
    public HumanPlayer(String name, StoneColour colour, Reader input) {
        super(name, colour);
        reader = new BufferedReader(input);
    }

    /**
     * Determines the move the human wants to make check if this is possible.
     * @return The move the human wants to make.
     */
    @Override
    public Move determineMove() {
        boolean moveOke = false;
        Move move = null;
        String pass;
        System.out.println("Do you want to pass? type pass, otherwise enter");
        try {
            pass = reader.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (pass == null ) { /// ALS DISCONNECT TIJDENS WORD DAN NUL GESTUURD
            return new Move(-1,-1,StoneColour.EMPTY);
        }
        else if(pass.equals("pass")){
            return null;
        }
        while (!moveOke) {
            int row;
            int col;
            System.out.println(" which move you want to make tell the row");
            row = checkIfItsAnInteger();
            System.out.println(" which move you want to make tell the col ");
            col = checkIfItsAnInteger();
            move = new Move(row, col, colour);
            if (game.isValidMove(move)) {
                moveOke = true;
            }else if(move.getRow()==-1000 || move.getCol()==-1000){
                return new Move(-1,-1,StoneColour.EMPTY);// in this case the player disconnected
            }
            else {
                System.out.println("this is not a valid move");
            }
        }
        System.out.println("move oke ");
        return move;
    }


    /**
     * function that checks if the input is a number if it is not the case he asked for new input
     * @return the required integer.
     */
    private int checkIfItsAnInteger() {
        int number;
        while (true) {
            try {
                number = Integer.parseInt(reader.readLine());
                return number;
            } catch (NumberFormatException e) {
                System.out.println("this is not a number please type a number");
            } catch (IOException e) {

                System.out.println("you quited the game");
                return -1000;
            }

        }

    }
}
