package com.nedap.go.server;

import com.nedap.go.spel.Board;
import com.nedap.go.spel.Game;
import com.nedap.go.spel.Move;
import com.nedap.go.spel.StoneColour;

import java.util.Scanner;

public class AbstractPlayer {
    static Scanner scanner = new Scanner(System.in);
    private String name ;
    private StoneColour colour ;
    private Game game ;

    public AbstractPlayer(String name, StoneColour colour) {
        this.name = name;
        this.colour =colour;
        game= new Game(new Board());
    }

    public Move determineMove() {
        boolean moveOke = false;
        Move move = null;
        String pass;
        System.out.println(name +" do you want to pass? type pass");
        pass =scanner.nextLine();
        if(pass.equals("pass")){
            return null;
        }
        while(!moveOke) {
            int row;
            int col;
            System.out.println(name + " which move you want to make tell the row");
            row = scanner.nextInt();// HIER NOG EEN TRY CATCH METHODE
            System.out.println(name+ " which move you want to make tell the col ");
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


    public void addMoveToOneGame(Move move){
        game.doMove(move);
    }
}
