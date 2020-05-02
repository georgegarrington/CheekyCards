package models.io;

import java.io.Serializable;
import java.util.List;

/**
 * Contains all the information that the client/players need to send to each other
 */
public class Message implements Serializable {

    public String header;
    public String requestedName;
    public String questionCard;
    public String customAnswer;

    /*
    If sending to coordinator then the list of cards played,
    if sending to the player then the list of cards played by other players
     */
    public List<String> answerCards;

    //each user mapped to their turn, only used at the start
    public List<String> users;

    public Message(String header){

        this.header = header;

    }

    /**
     * Used by the user to request a username
     *
     * @param header
     * @param requestedName
     */
    public Message(String header, String requestedName, String customAnswer, String customQuestion){

        this(header);
        this.requestedName = requestedName;
        this.customAnswer = customAnswer;
        questionCard = customQuestion;

    }


    public Message(String header, String questionCard, List<String> answerCards){

        this(header);
        this.questionCard = questionCard;
        this.answerCards = answerCards;

    }

    public Message(String header, String questionCard, List<String> answerCards,
                   List<String> users){

        this(header, questionCard, answerCards);
        this.users = users;

    }

}