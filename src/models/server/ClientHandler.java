package models.server;

import models.io.Comms;
import models.io.Message;
import models.Coordinator;

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

        /*
        try {
            init(s);
        } catch (Exception e) {
            e.printStackTrace();
        }*/

        this.c = c;
        this.b = b;

    }

    /*
    private void init(Socket s) throws Exception{

        reader = new BufferedReader(new InputStreamReader(s.getInputStream()));
        writer = new PrintWriter(s.getOutputStream(), true);

    }*/

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

                username = m.requestedName;

            }

        }

        comms.sendMessage("joinSuccess");

        b.await();
        //System.out.println("Sending first card to " + username);
        //comms.sendInitMessage(c.getCurrentQuestionCards());

        System.out.println("Going to let " + username + " know that the game is starting");
        comms.sendInitMessage(c.getCurrentQuestionCard(), c.getNAnswerCards(7), c.getUsers());

    }

}