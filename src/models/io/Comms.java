package models.io;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;


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

}