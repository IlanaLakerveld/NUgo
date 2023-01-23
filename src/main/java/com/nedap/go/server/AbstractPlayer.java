package com.nedap.go.server;

import com.nedap.go.spel.Board;
import com.nedap.go.spel.Game;
import com.nedap.go.spel.Move;
import com.nedap.go.spel.StoneColour;

import java.util.Scanner;

public class AbstractPlayer {
    static Scanner scanner = new Scanner(System.in);
    private final String name ;
    private final  StoneColour colour ;
  private ClientHandler cl ;

    public AbstractPlayer( StoneColour colour,ClientHandler cl ) {
        this.name = cl.getMyUsername();
        this.colour =colour;
        this.cl = cl ;

    }

    public Move determineMove() {
       Move move = null ;
       cl.sendMessage("YOURTURN");
        int[] moveFromClient = cl.getMove();
        if( moveFromClient!=null) {
            move = new Move(moveFromClient[0], moveFromClient[1], colour);
            System.out.println(getName()+"move that is outputed is "+ move.getRow() + move.getCol());
        } else if (moveFromClient != null) {
            System.out.println(getName()+"move is pass");
        }

        return move ;
    }

    public void sendMessage(String message){
        cl.sendMessage(message);
    }

    public String getName() {
        return name;
    }

    public void setReadBooleanToFalse(){
        cl.setValueRead(false);
    }
}
