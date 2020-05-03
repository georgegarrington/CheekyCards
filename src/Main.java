import models.util.Injector;

public class Main {

    public static void main(String[] args){

        if(args.length == 0 || args == null){

            Injector.initClientSession();

            //Seperate GUI stuff from main method
            new Launcher().initGUI(args);

        } else if (args[0].equals("server")) {

            Injector.initServerSession(args[1]);

        } else {

            throw new Error("Usage: java Main server [number of players], or provide no arguments to start a client session");

        }

    }

}