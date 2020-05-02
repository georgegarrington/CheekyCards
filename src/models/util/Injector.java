package models.util;

import javafx.stage.Stage;
import models.client.Client;
import models.server.Coordinator;

import javax.swing.*;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;

/**
 * Contains all the references to each part of the application
 */
public class Injector {

    private static Client client;
    private static Coordinator coordinator;
    private static boolean serverSession;
    private static boolean clientSession;
    private static CyclicBarrier barrier;
    private static Stage welcomeStage;

    static {

        serverSession = false;
        clientSession = false;

        //2 to start with for the login sequence then change
        barrier = new CyclicBarrier(2);

    }

    /**
     * Start up a client session
     * @param address
     * @param port
     */
    public static void initClientSession(String address, int port){

        client = new Client();
        clientSession = true;

    }

    public static void initServerSession(int port){

        coordinator = new Coordinator();
        serverSession = true;

    }

    public static Client getClient(){

        if(clientSession){

            return client;

        } else {

            error("There is no client in a server session! This should not be happening");
            return null;

        }

    }

    /**
     * Get the coordinator instance
     * @return
     */
    public static Coordinator getCoordinator(){

        if(serverSession){

            return coordinator;

        } else {

            error("There is no coordinator in a client session! This should not be happening");
            return null;

        }

    }

    public static void newBarrier(int n){

        barrier = new CyclicBarrier(n);

    }

    public static void waitOnBarrier(){

        try {
            barrier.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (BrokenBarrierException e) {
            e.printStackTrace();
        }

    }

    public static void associateWelcomeStage(Stage stage){

        welcomeStage = stage;

    }

    public static Stage getWelcomeStage(){

        return welcomeStage;

    }

    public static void error(String message){

        JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);

    }

    public static void inform(String message){

        JOptionPane.showMessageDialog(null, message, "Info message", JOptionPane.INFORMATION_MESSAGE);

    }


}