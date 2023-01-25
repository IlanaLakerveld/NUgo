package com.nedap.go.client;

import com.nedap.go.spel.Move;
import com.nedap.go.spel.StoneColour;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

public class HumanPlayer extends Player {

    BufferedReader reader;


    /**
     * Constructor
     *
     * @param name name of the player
     */
    public HumanPlayer(String name, StoneColour colour, Reader input) {
        super(name, colour);
        reader = new BufferedReader(input);
    }

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
        if (pass.equals("pass")) {
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
            } else {
                System.out.println("this is not a valid move");
            }
        }
        System.out.println("move oke ");
        return move;
    }


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
            }

        }

    }
}
