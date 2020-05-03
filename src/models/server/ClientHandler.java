package models.server;

import models.io.Comms;
import models.io.Message;

import java.net.Socket;
import java.util.concurrent.CyclicBarrier;

class ClientHandler implements Runnable {

    private int turn;
    private String username;

    //private BufferedReader reader;
    //private PrintWriter writer;
    private Comms comms;
    private Coordinator c;
    private CyclicBarrier b;

    public ClientHandler(Socket s, Coordinator c, CyclicBarrier b){

        comms = new Comms(s);

        this.c = c;
        this.b = b;

    }

    public void run(){

        try {
            doProtocol();
        } catch (Exception e) {
            e.printStackTrace();
        }

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


            turn = c.addUser(m.requestedName, this);

            if(turn == 0){

                comms.sendMessage("joinDenied");

            } else {

                //TODO possibility of two identical answer and question cards being picked very unlikely, leave like this for now
                c.addCustomAnswer(m.customAnswer);
                c.addCustomQuestion(m.questionCard);
                username = m.requestedName;

            }

        }

        //Keep the turns zero indexed
        turn--;
        comms.sendMessage("joinSuccess");

        b.await();

        //Wait for the cards to be shuffled and the current question card to be selected
        b.await();

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

        System.out.println(username + " is judging in this round");

        //Wait for the played cards to be received
        b.await();



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

        //Tell judge thread that the cards have now been received
        b.await();

        //Wait for the judge to make their choice
        b.await();

    }

}