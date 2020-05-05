package models.client;

import controllers.Controller;
import controllers.popups.Popup;
import javafx.application.Platform;
import models.io.Comms;
import models.io.Message;
import models.util.Injector;
import models.util.TraversibleMapIterator;

import java.io.IOException;
import java.net.Socket;
import java.util.*;

public class Client {

    private static final int SERVERPORT = 5005;

    //Once the ui has loaded up the controller will add its reference here
    private Controller controller;

    //The last message just sent by the server
    private Message lastMessage;
    private Socket socket;
    private Comms comms;

    //The players mapped to what turn they have
    private Map<String, Integer> players;

    //This players information
    private String myUsername;
    private int myTurn;
    private String currentQuestionCard;
    private String[] myDeck;

    //Used by the judge to look through the cards played
    private TraversibleMapIterator<String, List<String>> traversible;

    public Client(){

        //This information will be recieved in the init message
        players = new HashMap<String, Integer>();
        myDeck = new String[7];

    }

    /**
     * Once the FXML initialize method is called by the
     * @param controller
     */
    public void associateController(Controller controller){

        this.controller = controller;

    }

    /**
     * Attempts to join with requested username and will keep prompting the user \
     * to enter a new username if the one they gave was invalid
     */
    public void requestJoin(String str, String answer, String question, String address, Popup p){

        try {

            if(socket == null){

                System.out.println("value of address is: " + address);
                socket = new Socket(address, SERVERPORT);
                comms = new Comms(socket);

            }

        } catch (IOException e) {
            e.printStackTrace();
            p.close();
            Injector.error("That link was invalid! Please try again");
            //If an invalid address was given
            socket = null;
        }

        comms.sendJoinRequest(str, answer, question);

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Message response = comms.getMessage();

        if(response.header.equals("joinDenied")){

            response = null;
            p.updateMessage("Username denied. Please try again and pick another one");
            p.showButton();

        } else {

            p.updateMessage("Username accepted! Press ok to enter the game");
            p.updateAction(e -> {

                myUsername = str;
                //Signal to the await login thread that it can begin loading the program
                p.close();
                Platform.runLater(() -> Injector.getWelcomeStage().close());

                new Thread(() -> {

                    //To tell the main app to open
                    Injector.waitOnBarrier();

                    //Wait for init message from server
                    lastMessage = comms.getMessage();

                    //To let controller know the init message has been received
                    Injector.waitOnBarrier();

                }).start();

            });
            p.showButton();

        }

    }

    public void startGame(){

        //Wait for the init message to be received then start
        Injector.waitOnBarrier();

        if(!lastMessage.header.equals("gameStarting"))
            throw new Error("Expected the game to start! This should not be happening!");

        for(int i = 0; i < lastMessage.users.size(); i++){

            //Add all the players by their turn
            players.put(lastMessage.users.get(i),i);

            //Get which turn this client has
            if(lastMessage.users.get(i).equals(myUsername))
                myTurn = i;

        }

        controller.initGameGUI(lastMessage.answerCards, lastMessage.questionCard, lastMessage.users);
        currentQuestionCard = lastMessage.questionCard;
        playGame();

    }

    public void playGame(){

        int currentTurn = 0;

        //The game just keeps on going :)
        while(true){

            //TODO testing
            System.out.println("my turn is: " + myTurn);

            if(myTurn == currentTurn){

                //TODO testing
                System.out.println("playing judge round");
                playJudgeRound();

            } else {

                //TODO testing
                System.out.println("Playing answer round");
                playAnswerRound();

            }

            currentTurn++;

            //Should never go larger than players.size() but do just incase for good practice
            if(currentTurn >= players.size());
                currentTurn = 0;

        }

    }

    public void playJudgeRound(){

        controller.informJudging();

        //TODO testing
        System.out.println("Judge is now going to wait for played cards to be received");
        Message cardsForJudge = comms.getMessage();

        //TODO testing
        System.out.println("Judge finished waiting cos I got a message! :)");

        if(!cardsForJudge.header.equals("cardsForJudging")){

            throw new Error("This should not be possible!");

        }

        System.out.println("cards to judge received! size: " + cardsForJudge.playedCards.size());

        traversible = new TraversibleMapIterator<String, List<String>>(cardsForJudge.playedCards);

        //TODO for testing
        System.out.println("Contents of played cards:");
        for(String s: cardsForJudge.playedCards.keySet()){

            System.out.println("Player: " + s + " who played:");

            for(String str: cardsForJudge.playedCards.get(s)){

                System.out.println(str);

            }

        }

        //TODO for testing
        System.out.println("Contents of traversible key list:");
        for(String s: traversible.getOrderedKeys()){

            System.out.println(s);

        }

        //TODO for testing
        System.out.println("Value of current traversible index: " + traversible.getCurrentIndex());
        System.out.println("Value of current traversible objet: " + traversible.getCurrentObj());

        //Start with the first option
        controller.presentOptions(traversible.getCurrentObj());

        //Wait for the judge to pick their option
        Injector.waitOnBarrier();

    }

    /**
     * Returns the option to the right (if there is one), returns null if
     * there is no answer to the right
     * @return
     */
    public List<String> getRight(){

        if(traversible == null){

            throw new Error("This should be impossible!");

        }

        return traversible.goRight();

    }

    /**
     * Returns the option to the left (if there is one), returns null if
     * tehre is no answer to the left
     * @return
     */
    public List<String> getLeft(){

        if(traversible == null){

            throw new Error("This should be impossible!");

        }

        return traversible.goLeft();

    }

    public void playAnswerRound(){

        //TODO testing
        System.out.println("Playing answer round");

        controller.promptSelection(countNumRequired(currentQuestionCard));

        //Wait until the player has chosen a valid selection
        Injector.waitOnBarrier();

        //TODO for testing
        System.out.println("Past barrier so client must have chosen valid cards");

        List<String> playedCards = controller.getSelected();

        //TODO for testing
        System.out.println("Now going to send cards to judge. Size: " + playedCards.size());

        comms.sendCards(playedCards);

        //TODO for testing
        System.out.println("sent");

    }

    /**
     * How many cards the player needs to play
     * @param questionCard
     * @return
     */
    public static int countNumRequired(String questionCard){

        int out = 0;

        boolean onUnderscore = false;

        for(char c: questionCard.toCharArray()){

            if(c == '_'){

                if(!onUnderscore){
                    onUnderscore = true;
                    out++;
                }

            } else {

                if(onUnderscore)
                    onUnderscore = false;

            }

        }

        //If no blanks were specified then assume its 1
        if(out == 0){

            return 1;

            //Haiku card needs 3 answer cards
        } else if(questionCard.contains("Make a haiku")){

            return 3;

        } else {

            return out;

        }

    }

}