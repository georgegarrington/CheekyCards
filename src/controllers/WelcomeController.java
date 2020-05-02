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

    private Client client;

    @FXML
    public void initialize(){

        client = Injector.getClient();
        client.associateWelcomeController(this);

    }

    @FXML
    public void requestJoin(){

        String text = username.getText();

        new Thread(() -> {
            try {
                client.requestJoin(text,
                        new Popup("Checking with server if username is valid..."));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

    }

}