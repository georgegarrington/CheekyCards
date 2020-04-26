package controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import models.Player;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Controller {

    @FXML private StackPane card1;
    @FXML private StackPane card2;
    @FXML private StackPane card3;
    @FXML private StackPane card4;
    @FXML private StackPane card5;
    @FXML private StackPane card6;
    @FXML private StackPane card7;
    private StackPane[] cards;

    @FXML private Label expectedLabel;

    //How many cards need to be selected
    private int expected;
    //Which cards have been selected, and in which order
    private Queue<Integer> selectedCards;

    @FXML
    public void initialize(){

        expected = 4;
        selectedCards = new LinkedList<Integer>();
        cards = new StackPane[]{card1, card2, card3, card4, card5, card6, card7};
        initListeners();
        test();

    }

    public void initListeners(){

        for(StackPane p: cards){

            p.setOnMouseEntered(e -> handleEnter(p));
            p.setOnMouseExited(e -> handleExit(p));
            p.setOnMouseClicked(e -> handleClick(p));

        }

    }

    public void handleEnter(StackPane p){

        //Only change colour if there are 2 children, if there are 3 then it has been selected
        if(p.getChildren().size() < 3){

            p.setStyle("-fx-background-color: aliceblue");

        }

    }

    public void handleExit(StackPane p){

        if(p.getChildren().size() < 3){

            p.setStyle("-fx-background-color:  #e0e0e0");

        }

    }

    public void handleClick(StackPane p){

        if(selectedCards.size() < expected){

            selectedCards.add(Integer.valueOf(p.getId().charAt(4)) - 49);
            p.setStyle("-fx-background-color: chartreuse");

            Rectangle c = new Rectangle(12, 24, 12, 24);
            c.setFill(Color.WHITE);
            p.getChildren().add(c);
            p.setAlignment(c, Pos.TOP_LEFT);

            Text num = new Text(String.valueOf(selectedCards.size()));
            num.setFill(Color.RED);
            num.setStyle("-fx-font-size: 20");
            p.getChildren().add(num);
            p.setAlignment(num, Pos.TOP_LEFT);

        }

    }

    /**
     * Called by the clear button
     */
    @FXML
    public void clear(){

        for(int i: selectedCards){

            List<Node> nodes = cards[i].getChildren();

            for(int j = nodes.size() - 1; j > 1; j--){

                nodes.remove(j);

            }

            cards[i].setStyle("-fx-background-color:  #e0e0e0");

        }

        /*
        System.out.println("Selections were:");
        while(!selectedCards.isEmpty()){

            System.out.println(selectedCards.remove());

        }*/

        selectedCards.clear();

    }

    public void test(){

        for(int i = 0; i < 7; i++){

            //updateCard(i, "test");

        }

        Player p = new Player();

        for(int i = 0; i < 7; i++){

            updateCard(i, p.getCards()[i]);

        }

        setBlank(6);

    }

    /**
     * Updates the exptected number of white cards the player must play
     */
    public void setExpected(int expected){

        this.expected = expected;
        Platform.runLater(() -> expectedLabel.setText(String.valueOf(expected)));

    }

    /**
     * Updates the number card specified with the string specified
     * @param i
     * @param str
     */
    public void updateCard(int i, String str){

        List<Node> nodes = cards[i].getChildren();

        //May need to check size is 1 first
        for(int j = nodes.size() - 1; j > 0; j--){

            nodes.remove(j);

        }

        nodes.add(new Text(str));

    }

    /**
     * Set card i to be blank with the slogan
     * @param i
     */
    public void setBlank(int i){

        List<Node> nodes = cards[i].getChildren();

        //May need to check size is 1 first
        for(int j = nodes.size() - 1; j > 0; j--){

            nodes.remove(j);

        }

        nodes.add(new Text("CHEEKY \n CARDS \n    ;-)"));

    }

}