import models.util.Injector;
import models.util.Launcher;

public class Main {

    public static void main(String[] args){

        if(args.length == 0 || args == null){

            Injector.initClientSession();

            //Seperate GUI stuff from main method
            new Launcher().initGUI(args);

            //A server session is only started if specified as an argument
        } else if (args[0].equals("server")) {

            Injector.initServerSession(args[1]);

        }

    }

}