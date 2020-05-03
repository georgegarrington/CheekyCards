package models.client;

import controllers.Controller;
import controllers.WelcomeController;
import controllers.popups.Popup;
import javafx.application.Platform;
import models.io.Comms;
import models.io.Message;
import models.util.Injector;

import java.io.IOException;
import java.net.Socket;
import java.util.*;

public class Client {

    private final int SERVERPORT = 5005;
    private String ADDRESS = "";

    //Once the ui has loaded up the controller will add its reference here
    private Controller controller;
    private WelcomeController welcomeController;

    //The last message just sent by the server
    private Message lastMessage;
    private Comms comms;
    private Map<String, Integer> players;
    private String myUsername;
    private int myTurn;

    private String currentQuestionCard;
    private List<String> myDeck = new ArrayList<String>(7);

    public Client(){

        try {
            this.init();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void associateController(Controller controller){

        this.controller = controller;

    }

    public void associateWelcomeController(WelcomeController welcomeController){

        this.welcomeController = welcomeController;

    }

    /**
     * Attempts to join with requested username and will keep prompting the user \
     * to enter a new username if the one they gave was invalid
     */
    public void requestJoin(String str, String answer, String question, String address, Popup p){

        ADDRESS = address;

        Socket s = null;
        try {
            System.out.println("value of address is: " + address);
            s = new Socket(address, SERVERPORT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        comms = new Comms(s);


        comms.sendJoinRequest(str, answer, question);

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //System.out.println("response sent!");
        //System.out.println("waiting for servers response...");

        Message response = comms.getMessage();

        if(response.header.equals("joinDenied")){

            response = null;
            p.updateLabel("Username denied. Please try again and pick another one");
            p.showButton();

        } else {

            p.updateLabel("Username accepted! Press ok to enter the game");
            p.updateAction(e -> {

                myUsername = str;
                //Signal to the await login thread that it can begin loading the program
                p.close();
                Platform.runLater(() -> Injector.getWelcomeStage().close());

                //To tell the main app to open
                Injector.waitOnBarrier();
                new Thread(() -> {

                    System.out.println("waiting for init message from server...");
                    lastMessage = comms.getMessage();
                    System.out.println("init message recieved");
                    //To let controller know the init message has been received
                    System.out.println("waiting on barrier after getting messaged");
                    Injector.waitOnBarrier();
                    System.out.println("finished waiting on barrier after getting message");
                    //System.out.println("init message received: " + lastMessage.header);
                }).start();

            });
            p.showButton();

        }

    }

    public void startGame(){

        //Wait for the message to be received then start
        Injector.waitOnBarrier();
        controller.initGameGUI(lastMessage.answerCards, lastMessage.questionCard, lastMessage.users);
        currentQuestionCard = lastMessage.questionCard;
        playGame();

    }

    public void playGame(){

        int currentTurn = 0;

        //The game just keeps on going :)
        while(true){

            System.out.println("my turn is: " + myTurn);

            if(myTurn == currentTurn){

                System.out.println("playing judge round");
                playJudgeRound();

            } else {

                System.out.println("Playing answer round");
                playAnswerRound();

            }

            currentTurn++;

            //Should never go larger than players.size() but do just incase for good practice
            if(currentTurn >= players.size());
                currentTurn = 1;

        }

    }

    public void playJudgeRound(){

        controller.informJudging();

    }

    public void playAnswerRound(){

        controller.promptSelection(countNumRequired(currentQuestionCard));

        //Wait until the player has chosen a valid selection
        Injector.waitOnBarrier();

        List<String> playedCards = controller.getSelected();
        comms.sendCards(playedCards);

    }

    public void init() throws Exception {

        players = new HashMap<String, Integer>();

    }

    public void setupGame(){

        if(!lastMessage.header.equals("gameStarting")){

            Injector.error("Expected the game to start! uh oh this shouldn't be happening");

        } else {

            //TODO for testing delete later
            System.out.println("Game starting woohoo!");

        }

        //TODO for testing delete later
        System.out.println("The list of players in order by their turn is: ");

        for(int i = 1; i < lastMessage.users.size(); i++){

            if(lastMessage.users.get(i).equals(myUsername))
                myTurn = i;

            players.put(lastMessage.users.get(i), i);
            System.out.println(lastMessage.users.get(i));

        }

        System.out.println("my turn is: " + myTurn);

        //TODO for testing delete later
        System.out.println("My initial deck cards are: ");
        for(String str: lastMessage.answerCards){

            System.out.println(str);

        }

        myDeck.addAll(lastMessage.answerCards);

        //TODO for testing delete later
        System.out.println("The first question card is: " + lastMessage.questionCard);

    }

    boolean judging = true;
    int currentTurn = 1;

    Scanner s = new Scanner(System.in);

    /* AN EXAMPLE OF HOW IT MAY WORK, VERY MESSY
    public void playGame(){

        //START
        if(currentTurn == myTurn){

            judging = true;

        }

        System.out.println("The current judge is: ");

        if(judging){

            System.out.print("YOU!");
            System.out.println("When the other players have made their selections,");
            System.out.println("you will be presented with them here and choose the best one");

        } else {

            System.out.print(players.get(currentTurn));

        }

        System.out.println("----------------------------");
        System.out.println("QUESTION CARD:");
        System.out.println();
        System.out.println(currentQuestionCard);
        System.out.println();

        System.out.println("------------------------------------------");


        if(!judging){

            int numNeeded = countBlanks(currentQuestionCard);

            System.out.println("Please pick " + numNeeded + " cards to");
            System.out.println("fill in the blanks. Your options are: ");
            System.out.println("-------------------------------------------");

            for(int i = 1; i < myDeck.size(); i++){

                System.out.println(i + ".");

                System.out.println(myDeck.get(i));
                System.out.println();

            }

            System.out.println("-------------------------------------------");

            System.out.println("What is your choice? Please enter your number");

            int selection = 0;

            while(selection == 0){

                String choice = s.nextLine().trim();

                if (!Character.isDigit(choice.charAt(0)) || choice.length() > 1
                        || Integer.valueOf(choice.charAt(0)) > 7
                        || Integer.valueOf(choice.charAt(0)) < 1){

                    System.out.println("Your selection was invalid! Please try again");
                    choice = null;

                } else {

                    selection = Integer.valueOf(choice.charAt(0));

                }

            }



        }

        //END
        currentTurn++;
        if(currentTurn > players.size()){
            currentTurn = 1;
        }

    }*/

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