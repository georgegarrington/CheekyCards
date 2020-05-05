package controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import controllers.popups.Popup;
import javafx.application.Platform;
import javafx.fxml.FXML;
import models.client.Client;
import models.util.Injector;

public class WelcomeController {


    @FXML private JFXTextField address;
    @FXML private JFXTextField username;
    @FXML private JFXTextField answer;
    @FXML private JFXTextField question;
    @FXML private JFXButton joinButton;

    private Client client;

    @FXML
    public void initialize(){

        client = Injector.getClient();

        System.out.println("Hello Im in welcome controller initialize method.");
        System.out.println("About to inject my reference into the client...");
        Injector.associateWelcomeController(this);

    }

    @FXML
    public void requestJoin(){

        String text1 = username.getText();
        String text2 = answer.getText();
        String text3 = question.getText();
        String text4 = address.getText();

        new Thread(() -> {
            try {
                client.requestJoin(text1, text2, text3, text4,
                        new Popup("Checking with server if username is valid..."));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

    }

    /**
     * Toggle the button on or off
     */
    public void toggleButton(){

        Platform.runLater(() -> joinButton.setDisable(!joinButton.isDisable()));

    }

}