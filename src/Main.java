import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    public static void main(String[] args){

        launch(args);

        /*
        String[] arr = "hello how are you ".split(" ");
        System.out.println(arr[3]);*/

    }

    @Override
    public void start(Stage stage) throws Exception {

        Parent root = FXMLLoader.load(getClass().getResource("views/layout.fxml"));
        stage.setTitle("Cheeky Cards ;)");

        Scene scene = new Scene(root);

        stage.setScene(scene);
        stage.show();

    }

}