package models.server;

import models.io.Comms;
import models.io.Message;
import models.util.Injector;

import java.net.Socket;

class ClientHandler implements Runnable {

    //The turn and username of the client communicating with this handler thread
    private int turn;
    private String username;

    //private BufferedReader reader;
    //private PrintWriter writer;
    private Comms comms;
    private Coordinator c;

    public ClientHandler(Socket s, Coordinator c){

        comms = new Comms(s);

        this.c = c;

    }

    public void run(){

        try {
            doProtocol();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public String getUsername(){

        return username;

    }

    public void doProtocol() throws Exception{

        //Keep going until they have entered a valid name
        while(turn == 0) {

            System.out.println("Waiting for response from client");

            Message m = comms.getMessage();

            System.out.println("Response was: " + m.header);

            if (!m.header.equals("joinRequest")) {
                throw new Error("This should not be possible!");
            }

            username = m.requestedName;
            turn = c.addUser(this);

            if(turn == 0){

                comms.sendMessage("joinDenied");

            } else {

                //TODO possibility of two identical answer and question cards being picked very unlikely, leave like this for now
                c.addCustomAnswer(m.customAnswer);
                c.addCustomQuestion(m.questionCard);

            }

        }

        //Keep the turns zero indexed
        turn--;
        comms.sendMessage("joinSuccess");

        //TODO for testing delete
        System.out.println("Client handler for: " + username + " is now waiting 1st");

        //Tell the coordinator to start shuffling and get the question card ready
        Injector.waitOnBarrier();

        //TODO for testing delete
        System.out.println(username + ": ok finished first now waiting on second");

        //Wait for the cards to be shuffled and the current question card to be selected
        Injector.waitOnBarrier();

        System.out.println(username + ": ok finished waiting on second");

        Thread.sleep(2000);

        //TODO for testing delete
        System.out.println("Going to let " + username + " know that the game is starting");
        comms.sendInitMessage(c.getCurrentQuestionCard(), c.getNAnswerCards(7), c.getUsers());

        playGame();

    }

    public void playGame() throws Exception {

        int currentTurn = 0;

        //The game just keeps on going :)
        while(true){

            if(turn == currentTurn){

                playJudgeRound();

            } else {

                playAnswerRound();

            }

            currentTurn++;
            if(turn >= currentTurn)
                currentTurn = 0;

        }

    }

    public void playJudgeRound() throws Exception{

        //TODO for testing delete later
        System.out.println(username + " is judging in this round");
        System.out.println("judge handler thread now going to wait...");

        //Wait for the played cards to be received
        Injector.waitOnBarrier();

        //TODO for testing delete later
        System.out.println("judge handler thread finished waiting");
        System.out.println("played cards received. Sending to judge...");

        comms.sendCardsToJudge(c.getPlayedCardsThisRound());

    }

    public void playAnswerRound() throws Exception {

        System.out.println(username + " is playing in this round");

        Message playedCardsMessage = comms.getMessage();

        System.out.println(username + " played some cards! They were:");

        for(String s: playedCardsMessage.answerCards){

            System.out.println(s);

        }

        if(!playedCardsMessage.header.equals("playedCards")){

            throw new Error("This should be impossible!");

        }

        c.addPlayedCardsThisRound(username, playedCardsMessage.answerCards);

        //TODO for testing delete later
        System.out.println("About to tell the judge the cards were received");
        System.out.println("Also the number waiting on the barrier is: " + Injector.getBarrier().getNumberWaiting() +
                " and the number of parties is: " + Injector.getBarrier().getParties());

        //Tell judge thread that the cards have now been received
        Injector.waitOnBarrier();

        //Wait for the judge to make their choice
        Injector.waitOnBarrier();

    }

}