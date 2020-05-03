package models.io;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;
import java.util.Map;


public class Comms {

    private Socket s;

    private ObjectInputStream reader;
    private ObjectOutputStream writer;

    public Comms(Socket s){

        System.out.println("Made a new comms!");
        this.s = s;

        //System.out.println("Finished making reader and writer!");

    }

    public void sendToNetwork(Message m){

        if(writer == null){

            try {
                writer = new ObjectOutputStream(s.getOutputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        try {
            writer.writeObject(m);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public Message getMessage(){

        if(reader == null){

            try {
                reader = new ObjectInputStream(s.getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        try {
            return (Message) reader.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        //Should never happen but keep incase
        return null;

    }

    /**
     * Send a simple one liner message
     */
    public void sendMessage(String message){

        Message m = new Message(message);
        sendToNetwork(m);

    }

    public void sendJoinRequest(String requested, String answer, String question){

        Message m = new Message("joinRequest", requested, answer, question);
        sendToNetwork(m);

    }

    /**
     * Send to the client to signal starting the game
     */
    public void sendInitMessage(String questionCard, List<String> initDeck, List<String> players){

        Message m = new Message("gameStarting", questionCard, initDeck, players);
        sendToNetwork(m);

    }

    //How many new deck cards the client that calls this needs
    public void requestNewDeckCards(int n){

        Message m = new Message("requestNewDeckCards", n);
        sendToNetwork(m);

    }

    public void sendCards(List<String> playedCards){

        Message m = new Message("playedCards", playedCards);
        sendToNetwork(m);

    }

    public void sendCardsToJudge(Map<String, List<String>> playedCards){

        Message m = new Message("cardsForJudging", playedCards);
        sendToNetwork(m);

    }

    /**
     * Tell all client sessions to go to the option on the right
     */
    public void goRight(){

        Message m = new Message("goRight");
        sendToNetwork(m);

    }

    /**
     * Tell all client sessions to go to the option on the left
     */
    public void goLeft(){

        Message m = new Message("goLeft");
        sendToNetwork(m);

    }

}