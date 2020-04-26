package models;

/**
 * A thread which handles communication between the coordinator and a player
 */
public class Player {

    private String[] cards;

    public Player(){

        cards = new String[7];
        cards = new String[]{"hello","my","name","is","george","how","you"};

    }

    public String[] getCards(){

        return cards;

    }

}