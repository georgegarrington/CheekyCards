package models.client;

import models.io.Comms;
import models.io.Message;

import java.net.Socket;
import java.util.*;

public class Client {

    public static void main(String[] args){

        System.out.println(countBlanks("helllo___how__are_you___"));

        //new Client();

    }

    private final int SERVERPORT = 5005;
    private Scanner sc;
    private Comms c;
    private Map<String, Integer> players;
    private int myTurn;

    public Client(){

        try {
            this.init();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    String currentQuestionCard;

    private List<String> myDeck = new ArrayList<String>(7);

    private void init() throws Exception {

        players = new HashMap<String, Integer>();

        Socket s = new Socket("localhost", SERVERPORT);
        sc = new Scanner(System.in);

        c = new Comms(s);

        String username = null;

        while(username == null){

            System.out.println("Please enter your username: ");
            username = sc.nextLine();
            System.out.println("Requesting to join with username...");

            c.sendJoinRequest(username);

            //System.out.println("response sent!");
            //System.out.println("waiting for servers response...");

            Message response = c.getMessage();

            if(response.header.equals("joinDenied")){
                response = null;
                System.out.println("Username denied. Please try again and pick another one");
            }

        }

        System.out.println("Successfully joined :) with username: " + username);
        System.out.println("Now going to wait for everyone to join so the game can start");
        //System.out.println("My turn number is: " + response[1]);
        //System.out.println("My first card is: " + reader.readLine());

        Message m = c.getMessage();

        if(!m.header.equals("gameStarting")){

            System.out.println("Expected the game to start! uh oh");

        } else {

            System.out.println("Game starting woohoo!");

        }

        System.out.println("The list of players in order by their turn is: ");

        for(int i = 1; i < m.users.size(); i++){

            players.put(m.users.get(i), i);

        }

        myTurn = players.get(username);

        System.out.println("My initial deck cards are: ");


        for(String str: m.answerCards){

            System.out.println(str);

        }

        myDeck.addAll(m.answerCards);

        System.out.println("The first question card is: " + m.questionCard);

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
    public static int countBlanks(String questionCard){

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