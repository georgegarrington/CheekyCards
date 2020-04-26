package controllers;

import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;
import models.Player;

import java.util.*;

public class Controller {

    @FXML private StackPane card1;
    @FXML private StackPane card2;
    @FXML private StackPane card3;
    @FXML private StackPane card4;
    @FXML private StackPane card5;
    @FXML private StackPane card6;
    @FXML private StackPane card7;
    @FXML private StackPane questionCard;
    private StackPane[] cards;

    @FXML private Label expectedLabel;

    //How many cards need to be selected
    private int expected;

    //Which index cards have been selected, and in which order
    private Queue<Integer> selectedIndices;

    //Which indices are missing cards
    private Queue<Integer> blankIndices;

    @FXML
    public void initialize(){

        setBlank(questionCard);

        //TODO FOR TESTING
        expected = 4;

        selectedIndices = new LinkedList<Integer>();
        blankIndices = new LinkedList<Integer>();
        cards = new StackPane[]{card1, card2, card3, card4, card5, card6, card7};
        for(StackPane p: cards){

            setBlank(p);

        }
        initListeners();

        //TODO FOR TESTING
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

        if(selectedIndices.size() < expected){

            selectedIndices.add(Integer.valueOf(p.getId().charAt(4)) - 49);
            p.setStyle("-fx-background-color: chartreuse");

            Rectangle c = new Rectangle(12, 24, 12, 24);
            c.setFill(Color.WHITE);
            p.getChildren().add(c);
            p.setAlignment(c, Pos.TOP_LEFT);

            Text num = new Text(String.valueOf(selectedIndices.size()));
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

        for(int i: selectedIndices){

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

        selectedIndices.clear();

    }

    //TODO FOR TESTING
    public void test(){

        new Thread(() -> {

            Player p = new Player();

            for(int i = 0; i < 7; i++){

                flipToNextEXT(cards[i], p.getCards()[i]);

            }

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            for(StackPane s: cards){

                flipToBlankEXT(s);

            }

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            for(int i = 0; i < 7; i++){

                flipToNextEXT(cards[i], p.getCards()[i]);

            }

        }).start();

        //setBlank(cards[0]);

    }

    @FXML
    public void test2(){

        flipToBlank(cards[0]);

    }

    /**
     * Call the same method from an external thread
     * @param expected
     */
    public void setExpectedEXT(int expected){

        Platform.runLater(() -> setExpected(expected));

    }

    /**
     * Updates the expected number of white cards the player must play
     */
    public void setExpected(int expected){

        this.expected = expected;
        expectedLabel.setText(String.valueOf(expected));

    }

    /**
     * Call method from external thread
     * @param p
     */
    public void flipToBlankEXT(StackPane p){

        Platform.runLater(() -> flipToBlank(p));

    }

    /**
     * Animation of flipping pane p to the slogan
     *
     * @param p
     */
    public void flipToBlank(StackPane p){

        ScaleTransition hide = new ScaleTransition(Duration.millis(500), p);
        hide.setFromX(1);
        hide.setToX(0);

        ScaleTransition show = new ScaleTransition(Duration.millis(500), p);
        show.setFromX(0);
        show.setToX(1);

        hide.setOnFinished(e -> {

            setBlank(p);
            show.play();

        });

        hide.play();

    }

    /**
     * Call method from external thread
     * @param p
     * @param next
     */
    public void flipToNextEXT(StackPane p, String next){

        Platform.runLater(() -> flipToNext(p, next));

    }

    /**
     * Animation of flipping pane p to the next card specified
     * @param p
     * @param next
     */
    public void flipToNext(StackPane p, String next){

        ScaleTransition hide = new ScaleTransition(Duration.millis(500), p);
        hide.setFromX(1);
        hide.setToX(0);

        ScaleTransition show = new ScaleTransition(Duration.millis(500), p);
        show.setFromX(0);
        show.setToX(1);

        hide.setOnFinished(e -> {

            updateCard(p, next);
            show.play();

        });

        hide.play();

    }

    //Add the new cards to the missing indices
    public void fillBlanks(String[] newCards){

        for(String s: newCards){

            flipToNext(cards[blankIndices.remove()], s);

        }

    }

    /**
     * Updates the number card specified with the string specified
     * @param p
     * @param str
     */
    public void updateCard(StackPane p, String str){

        List<Node> nodes = p.getChildren();

        //May need to check size is 1 first
        for(int j = nodes.size() - 1; j > 0; j--){

            nodes.remove(j);

        }

        nodes.add(new Text(str));

    }

    /**
     * Set card i to be blank with the slogan
     * @param p
     */
    public void setBlank(StackPane p){

        List<Node> nodes = p.getChildren();

        //May need to check size is 1 first
        for(int j = nodes.size() - 1; j > 0; j--){

            nodes.remove(j);

        }

        nodes.add(new Text("CHEEKY \n CARDS \n    ;-)"));

    }

}