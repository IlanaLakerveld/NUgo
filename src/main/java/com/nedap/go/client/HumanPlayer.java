package com.nedap.go.client;

import com.nedap.go.spel.Game;
import com.nedap.go.spel.Move;
import com.nedap.go.spel.StoneColour;

import java.util.Scanner;

public class HumanPlayer extends Player{

     Scanner scanner ;


    /**
     * Constructor
     *
     * @param name name of the player
     */
    public HumanPlayer(String name, StoneColour colour,Scanner scanner) {
        super(name,colour);
        this.scanner=scanner;
    }

    @Override
    public Move determineMove() {
        boolean moveOke = false;
        Move move = null;
        String pass;
        System.out.println("Do you want to pass? type pass, otherwise do nothing");
        pass =scanner.nextLine();
        System.out.println(pass);
        if(pass.equals("pass")){
            return null;
        }
        while(!moveOke) {
            int row;
            int col;
            System.out.println(" which move you want to make tell the row");
            row = scanner.nextInt();
            System.out.println(" which move you want to make tell the col ");
            col = scanner.nextInt();
            scanner.nextLine();
            move = new Move(row, col, colour);
            if (game.isValidMove(move)) {
                moveOke =true;
            }
            else{
                System.out.println("this is not a valid move");
            }
        }
        System.out.println("move oke ");
        return move ;
    }
}
