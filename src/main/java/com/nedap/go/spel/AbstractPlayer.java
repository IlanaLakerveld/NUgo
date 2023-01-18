package com.nedap.go.spel;

public class AbstractPlayer {


    private final String name;
    private final StoneColour stone ;



    /**
     * Creates a new Player object.
     */
    public AbstractPlayer(String name, StoneColour stone) {
        this.name = name;
        this.stone = stone ;
    }

    /**
     * Get the name of the player.
     * @return the name of the player
     */
    public String getName() {
        return name;
    }

    /**
     * Get the stone colour of this player
     * @return The stone colour of the player ;
     */
    public StoneColour getStone() {
        return stone;
    }



    /**
     * Returns a representation of a player,with their name and colour
     * @return the String representation of this object
     */
    @Override
    public String toString() {
        return "Player " + getName() + "with colour " + getStone();
    }


}


