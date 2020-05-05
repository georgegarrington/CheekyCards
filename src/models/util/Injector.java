package models.util;

import controllers.WelcomeController;
import javafx.stage.Stage;
import models.client.Client;
import models.server.Coordinator;

import javax.swing.*;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

/**
 * Contains all the references to each part of the application
 */
public class Injector {

    private static Launcher launcher;
    private static WelcomeController wc;
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
     */
    public static void initClientSession(){

        client = new Client();
        clientSession = true;

    }

    public static void initServerSession(String numPlayers){

        if(!Character.isDigit(numPlayers.charAt(0))){

            throw new Error("Invalid argument! Please enter a number between 1 and 7");

        }

        int val = Integer.parseInt(numPlayers);

        if(val < 1){

            throw new Error("Invalid argument! Please enter a number between 1 and 7");

        }

        if (val > 7) {

            throw new Error("Only a maximum of 7 players are supported at the moment!");

        }

        coordinator = new Coordinator(val);
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

    public static CyclicBarrier getBarrier(){

        return barrier;

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

    public static void associateLauncher(Launcher launcherRef){

        launcher = launcherRef;

    }

    public static Launcher getLauncher(){

        return launcher;

    }

    public static void associateWelcomeStage(Stage stageRef){

        welcomeStage = stageRef;

    }

    public static Stage getWelcomeStage(){

        return welcomeStage;

    }

    public static void associateWelcomeController(WelcomeController wcRef){

        wc = wcRef;

    }

    public static WelcomeController getWelcomeController(){

        return wc;

    }

    public static void error(String message){

        JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);

    }

    public static void inform(String message){

        JOptionPane.showMessageDialog(null, message, "Info message", JOptionPane.INFORMATION_MESSAGE);

    }


}