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

    public void initGUI(String[] args) {

        doLoginSequence();
        launch(args);

    }

    public void start(Stage stage) throws Exception {

        new Thread(() -> {

            Injector.waitOnBarrier();

            System.out.println("Finished waiting on barrier");

            Platform.runLater(() -> {

                //Injector.associateStage(stage); probably wont need this but keep here incase
                Parent root = null;

                try {
                    root = FXMLLoader.load(getClass().getResource("views/layout.fxml"));
                    System.out.println("value of root: " + root.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }

                stage.setTitle("Cheeky cards ;)");
                stage.setScene(new Scene(root));
                stage.show();

            });

        }).start();

    }

    /**
     * Once the login sequence has completed then the main GUI will load and the game will start
     */
    public void doLoginSequence() {

        Platform.startup(() -> {

            Stage stage = new Stage();
            Injector.associateWelcomeStage(stage);
            Parent root = null;
            try {
                root = FXMLLoader.load(getClass().getResource("views/welcome.fxml"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            stage.setTitle("Welcome to cheeky Cards ;)");
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();

        });

    }

}