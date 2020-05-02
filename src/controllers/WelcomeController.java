package controllers;

import com.jfoenix.controls.JFXTextField;
import controllers.popups.Popup;
import javafx.application.Platform;
import javafx.fxml.FXML;
import models.client.Client;
import models.util.Injector;

import javax.swing.*;

public class WelcomeController {


    @FXML private JFXTextField username;
    @FXML private JFXTextField answer;
    @FXML private JFXTextField question;

    private Client client;

    @FXML
    public void initialize(){

        client = Injector.getClient();
        client.associateWelcomeController(this);

    }

    @FXML
    public void requestJoin(){

        String text1 = username.getText();
        String text2 = answer.getText();
        String text3 = question.getText();

        new Thread(() -> {
            try {
                client.requestJoin(text1, text2, text3,
                        new Popup("Checking with server if username is valid..."));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

    }

}