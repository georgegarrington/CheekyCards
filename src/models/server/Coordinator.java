package models.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.CyclicBarrier;

public class Coordinator {

    /**
     * Get the IP address this server is hosting on to give it to the clients
     * so they can play the game
     * @return
     */
    public static String getIP(){

        URL whatismyip = null;
        try {
            whatismyip = new URL("http://checkip.amazonaws.com");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        BufferedReader in = null;
        try {
            in = new BufferedReader(new InputStreamReader(
                    whatismyip.openStream()));
            return in.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        //Should never happen but add incase
        return null;

    }

    //The port this server is listening on
    private static final int PORT = 5005;

    //How many players are expected to join this game
    private final int EXPECTED = 2;

    private String currentQuestionCard;
    private Stack<String> questionCards;
    private Stack<String> answerCards;

    private List<String> users;
    private List<ClientHandler> handlers;
    private CyclicBarrier barrier;

    public Coordinator(){

        //TODO for testing delete later
        System.out.println("Coordinator started :) Now going to start loading cards: ");

        questionCards = new Stack<String>();
        answerCards = new Stack<String>();
        importCards();

        //TODO for testing delete later
        System.out.println("Cards loaded. Question cards size: " + questionCards.size());
        System.out.println("And the answer cards list size: " + answerCards.size());

        users = new ArrayList<String>();
        handlers = new ArrayList<ClientHandler>();

        //To account for each client handler thread and the main thread also
        barrier = new CyclicBarrier(EXPECTED + 1);

        try {
            this.init();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Import the cards from the text files and store them in the stacks
     */
    public void importCards(){

        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        InputStream is = classloader.getResourceAsStream("resources/questionCards.txt");
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));

        try{

            while(reader.ready()){

                questionCards.add(reader.readLine());

            }

            reader.close();

        } catch(IOException e){

            e.printStackTrace();

        }

        classloader = Thread.currentThread().getContextClassLoader();
        is = classloader.getResourceAsStream("resources/answerCards.txt");
        reader = new BufferedReader(new InputStreamReader(is));

        try{

            while(reader.ready()){

                answerCards.add(reader.readLine());

            }

            reader.close();

        } catch(IOException e){

            e.printStackTrace();

        }

    }

    public void init() throws Exception{

        ServerSocket ss = new ServerSocket(5005);

        //TODO FOR TESTING DELETE LATER
        System.out.println("server socket address says: " + ss.getInetAddress());
        System.out.println("but other address thing says: " + getIP());
        System.out.println("waiting for people to join");

        for(int numJoined = 0; numJoined < EXPECTED; numJoined++){

            Socket s = ss.accept();
            System.out.println("New client joined!");

            new Thread(new ClientHandler(s, this, barrier)).start();

        }

        ss.close();

        barrier.await();

        //All custom cards have been added by this point so shuffle them
        Collections.shuffle(answerCards);
        Collections.shuffle(questionCards);
        currentQuestionCard = questionCards.pop();

        barrier.await();

    }

    /**
     * Returns 0 if failed to add, and if successful then returns the turn of the player
     * @param username
     * @return
     */
    public int addUser(String username, ClientHandler h){

        synchronized (users){

            synchronized (handlers){

                if(!users.contains(username)){

                    users.add(username);
                    handlers.add(h);
                    System.out.println("new player added: " + username);
                    return handlers.size();

                } else {

                    return 0;

                }

            }

        }

    }

    public void addCustomQuestion(String q){

        synchronized (questionCards){

            //TODO for testing delete later
            System.out.println("Adding custom question: " + q);
            questionCards.push(q);

        }

    }

    public void addCustomAnswer(String a){

        synchronized (answerCards){

            //TODO for testing delete later
            System.out.println("Adding custom answer: " + a);
            answerCards.push(a);

        }

    }

    public List<String> getUsers(){

        return users;

    }

    public int getNumHandlers(){

        return handlers.size();

    }

    public String getCurrentQuestionCard(){

        return currentQuestionCard;

    }

    public List<String> getNAnswerCards(int n){

        List<String> out = new ArrayList<String>();

        synchronized(answerCards){

            for(int i = 0; i < n; i++){
                out.add(answerCards.pop());
            }

        }

        return out;

    }

}