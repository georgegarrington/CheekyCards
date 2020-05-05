package controllers.popups;

import com.jfoenix.controls.JFXButton;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * Simple popup class currently only used for the welcome part of the application,
 * so the button visibility can be toggled/untoggled to correspond to a change in event
 */
public class Popup {

    private Stage popup;
    private Label label;
    private JFXButton button;

    public Popup(String initMessage){

        Platform.runLater(() -> this.init(initMessage));

    }

    private void init(String message){

        popup = new Stage();
        popup.setResizable(false);

        //Makes the max, min and close buttons disappear and the bar they are in too
        popup.initStyle(StageStyle.UNDECORATED);
        VBox v = new VBox();
        v.setAlignment(Pos.CENTER);
        v.setPrefHeight(100);
        v.setPrefWidth(250);
        popup.setScene(new Scene(v));

        label = new Label(message);
        label.setWrapText(true);
        label.setStyle("-fx-font-size: 16");
        label.setPadding(new Insets(0,0,20,0));
        v.getChildren().add(label);

        //The button will be invisible initially until login response received from server
        button = new JFXButton("OK");
        button.setVisible(false);
        button.setOnAction(e -> Platform.runLater(() -> popup.close()));
        v.getChildren().add(button);

        popup.show();

    }

    public void updateMessage(String text){

        Platform.runLater(() -> label.setText(text));

    }

    public void showButton() {

        Platform.runLater(() -> button.setVisible(true));

    }

    public void updateAction(EventHandler handler){

        Platform.runLater(() -> button.setOnAction(handler));

    }

    public void close(){

        Platform.runLater(() -> popup.close());

    }

}