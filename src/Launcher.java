import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import models.util.Injector;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public class Launcher extends Application {


    @Override
    public void start(Stage stage) throws Exception {

        Injector.getClient().associateWelcomeStage(stage);
        Parent root = FXMLLoader.load(getClass().getResource("views/welcome.fxml"));
        stage.setTitle("Welcome to cheeky Cards ;)");
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
        awaitLogin();

    }

    /**
     * Once the login sequence has completed then the main GUI will load and the game will start
     */
    public void awaitLogin() {

        new Thread(() -> {

            Injector.countDown();
            System.out.println("waiting on latch...");
            Injector.waitOnLatch();
            System.out.println("finished waiting on latch.");

            Platform.runLater(() -> {

                System.out.println("hello im on the javafx thread");
                Stage stage = new Stage();
                Parent root = null;

                try {
                    root = FXMLLoader.load(getClass().getResource("views/layout.fxml"));
                } catch (IOException e) {
                    e.printStackTrace();
                }

                stage.setTitle("Cheeky cards ;)");
                stage.setScene(new Scene(root));
                stage.show();
                System.out.println("finished javafx stuff");

            });

        }).start();

    }

    public static void initGUI(String[] args){

        launch(args);

    }

}