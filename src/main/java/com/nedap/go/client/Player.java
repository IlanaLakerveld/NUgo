package com.nedap.go.client;

//import com.nedap.go.spel.*;

public abstract class Player {
    private String name;

    /**
     * Constructor
     * @param name name of the player
     */
    public Player(String name) {
        this.name = name;
    }

    /**
     * Returns the name of the player.
     * @return the name of the player
     */
    public String getName() {
        return name;
    }


//    public abstract Move determineMove(Game game);


}
