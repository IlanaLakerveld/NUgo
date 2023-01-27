package com.nedap.go.client;

import com.nedap.go.spel.Move;
import com.nedap.go.spel.StoneColour;

import java.util.List;

public class ComputerPlayer extends Player{
    /**
     * Constructor
     *
     * @param name name of the player
     */
    public ComputerPlayer(String name, StoneColour colour) {
        super(name,colour);
    }


    // get a list of possible moves (posible moves of possible checked moves? )
    // krijg een random number
    // kijk of dat een oke number is
    // return de move

    @Override
    public Move determineMove() {
//        List possibleMoves   ;
//        if(possibleMoves.size >0){
//            int randomInt = (int) Math.random()*possibleMoves.size() ;
//            return possibleMoves.get(randomInt);
//
//        }
//        else{
//            return null ;  // if no possible moves then pass
//        }
//
        return null;
    }
}
