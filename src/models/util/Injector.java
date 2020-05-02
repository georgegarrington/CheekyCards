package models.util;

import models.client.Client;
import models.server.Coordinator;

import javax.swing.*;
import java.util.concurrent.CountDownLatch;

/**
 * Contains all the references to each part of the application
 */
public class Injector {

    private static Client client;
    private static Coordinator coordinator;
    private static boolean serverSession;
    private static boolean clientSession;
    private static CountDownLatch latch;

    static {

        serverSession = false;
        clientSession = false;
        latch = new CountDownLatch(2);

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

    public static void waitOnLatch(){

        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public static void countDown(){

        latch.countDown();
        System.out.println("new latch value: " + latch.getCount());

    }

    public void newLatch(int n){

        latch = new CountDownLatch(n);

    }

    public static void error(String message){

        JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);

    }

    public static void inform(String message){

        JOptionPane.showMessageDialog(null, message, "Info message", JOptionPane.INFORMATION_MESSAGE);

    }


}