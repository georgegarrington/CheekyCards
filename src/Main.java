import models.util.Injector;

public class Main {

    //Keep this hardcoded for now
    private static final int SERVERPORT = 5005;
    private static final String ADDRESS = "localhost";

    public static void main(String[] args){

        if(args.length == 0 || args == null){

            Injector.initClientSession(ADDRESS, SERVERPORT);

            //Seperate GUI stuff from main method
            new Launcher().initGUI(args);

        } else {

            Injector.initServerSession(SERVERPORT);

        }

    }

}