package controllers;

import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;
import models.client.Client;
import models.util.Injector;
import models.util.TraversibleMapIterator;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Controller {

    private Client client;

    @FXML private StackPane card1;
    @FXML private StackPane card2;
    @FXML private StackPane card3;
    @FXML private StackPane card4;
    @FXML private StackPane card5;
    @FXML private StackPane card6;
    @FXML private StackPane card7;
    private StackPane[] cards;

    @FXML private StackPane played1;
    @FXML private StackPane played2;
    @FXML private StackPane played3;
    private StackPane[] playedCards;

    @FXML private StackPane questionCard;

    @FXML private Polygon right;
    @FXML private Polygon left;

    @FXML private Label expectedLabel;
    @FXML private Label display;
    @FXML private Text overlayText;

    @FXML private GridPane leaderboardGrid;

    //How many cards need to be selected
    private int expected;

    //Which index(es) are being used to play on depending on the number of cards expected
    private int[] activePlayIndices;

    //Which index cards have been selected, and in which order
    private Queue<Integer> selectedIndices;

    //Which indices are missing cards
    private Queue<Integer> blankIndices;

    //Whether or not the user should currently be selecting or not
    private boolean selecting;

    //Whether or not the user is the current judge
    private boolean judge;

    //For use when judging, each user is mapped to the cards they picked
    //private Map<String, String[]> options;

    private TraversibleMapIterator<String, String[]> optionIt;

    //How long each card takes to flip
    private static final int FLIPTIME = 300;

    @FXML
    public void initialize(){

        System.out.println("Hello Im in the controller initialize method");
        client = Injector.getClient();
        client.associateController(this);

        cards = new StackPane[]{card1, card2, card3, card4, card5, card6, card7};
        playedCards = new StackPane[]{played1, played2, played3};

        setToBack(questionCard);
        for(StackPane p: cards){
            setToBack(p);
        }

        selecting = false;
        selectedIndices = new LinkedList<Integer>();
        blankIndices = new LinkedList<Integer>();
        initListeners();
        new Thread(() -> client.startGame()).start();

        //Wait for the init message to be received
        //Injector.waitOnBarrier();

        //TODO dont do this here, make a seperate startgame method which affects the GUI
        //Once the init message has been received the label can vanish
        //overlayText.setVisible(false);
        //new Thread(() -> client.setupGame()).start();

        /*
        //TODO FOR TESTING
        expected = 4;

        selectedIndices = new LinkedList<Integer>();
        blankIndices = new LinkedList<Integer>();

        //Use linked hash map so that order is retained
        HashMap<String, String[]> options = new LinkedHashMap<String, String[]>();

        //TODO FOR TESTING
        options.put("Amelia", new String[]{"Academy award winner Meryl Streep", "Getting naked and watching cbeebies"});
        options.put("Harriet", new String[]{"German chancellor Angela Merkel", "Becoming a blueberry"});
        options.put("George", new String[]{"The gays.", "One titty hanging out"});

        optionIt = new TraversibleMapIterator<String, String[]>(options);

        cards = new StackPane[]{card1, card2, card3, card4, card5, card6, card7};
        playedCards = new StackPane[]{played1, played2, played3};

        //Make the played cards start at the top out of the ui
        for(StackPane p: playedCards){

            setToBack(p);
            TranslateTransition transition = new TranslateTransition(Duration.millis(1), p);
            transition.setByY(-300);
            transition.setOnFinished(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {

                    p.getChildren().get(0).setVisible(true);

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    enterPlayed(getIndex(p), "hello");

                }
            });
            transition.play();

        }

        //isFrontCards = new boolean[]{false,false,false,false,false,false,false};
        //isFrontPlayed = new boolean[]{false,false,false};

        selecting = false;

        setToBack(questionCard);

        for(StackPane p: cards){

            setToBack(p);

        }

        for(int i = 0; i < cards.length; i += 2){

            int in = i;
            exitCard(in, e -> enterCard(in, "hello"));

        }

        initListeners();

        //TODO FOR TESTING
        //test();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        for(int i = 0; i < playedCards.length; i++){

            enterPlayed(i, "hello");

        }*/

    }

    /**
     * Called when the game is about to start and the first message has been received
     */
    public void initGameGUI(List<String> answerCards, String questionCard, List<String> players){

        Platform.runLater(() -> {

            overlayText.setVisible(false);

            if(answerCards.size() != 7)
                throw new Error("This should not be happening!");

            for(int i = 0; i < 7; i++){

                flipToNext(cards[i], answerCards.get(i));

            }

            flipToNext(this.questionCard, questionCard);

            if(players.size() > 7){
                throw new Error("There is only support for 7 players at the moment!");
            }

            for(int i = 0; i < players.size(); i++){

                leaderboardGrid.add(new Label(players.get(i)),0,i);
                leaderboardGrid.add(new Label("0"),1,i);

            }

        });

    }

    public void initListeners(){

        for(StackPane p: cards){

            p.setOnMouseEntered(e -> handleEnter(p));
            p.setOnMouseExited(e -> handleExit(p));
            p.setOnMouseClicked(e -> handleClick(p));

        }

        left.setOnMouseEntered(e -> handleButtonOver(left));
        left.setOnMouseExited(e -> handleButtonExit(left));
        left.setOnMouseClicked(e -> goLeft());
        right.setOnMouseEntered(e -> handleButtonOver(right));
        right.setOnMouseExited(e -> handleButtonExit(right));
        right.setOnMouseClicked(e -> goRight());

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

        System.out.println("went in handle click method");
        System.out.println("selected indices: " + selectedIndices.size());
        System.out.println("expected: " + expected);
        System.out.println("doesnt contains p? " + !selectedIndices.contains(getIndex(p)));
        System.out.println("selecting: " + selecting);

        if(selectedIndices.size() < expected && !selectedIndices.contains(getIndex(p)) && selecting) {

            selectedIndices.add(getIndex(p));
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

    public void handleButtonOver(Polygon p){

        p.setFill(Color.LIGHTBLUE);

    }

    public void handleButtonExit(Polygon p){

        p.setFill(Color.DODGERBLUE);

    }

    public void goRight(){

        right.setFill(Color.LIMEGREEN);

    }

    public void goLeft(){

        left.setFill(Color.LIMEGREEN);

    }

    /**
     * Gets the index of the card in the list it should be in
     * @param p
     * @return
     */
    public int getIndex(StackPane p){

        return Integer.valueOf(p.getId().charAt(p.getId().length() - 1) - 49);

    }

    /**
     * Whether or not this card is a played card or a deck card
     * @param p
     * @return
     */
    public boolean isPlayedCard(StackPane p){

        if(p.getId().substring(0, p.getId().length() - 1) == "played"){

            return true;

        } else {

            return false;

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

    /*
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

                flipToBackEXT(s, null);

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

    }*/

    /**
     * Let the player know that they are the judge for this round
     */
    public void informJudging(){

        Platform.runLater(() -> {

            overlayText.setText("You are the judge for this round. \n Waiting for the other players to \n submit their cards...");
            overlayText.setVisible(true);

        });

    }

    /**
     * Prompt the player to select the number of cards specified
     */
    public void promptSelection(int n){

        Platform.runLater(() -> {

            overlayText.setText("Please select the required number of cards \n in the order you wish them to be played");
            overlayText.setVisible(true);
            selecting = true;
            expected = 1;
            System.out.println("selecting is: " + selecting);

        });

    }

    @FXML
    /**
     * Called by the play button
     */
    public void playCards(){

        if(selectedIndices.size() == expected){

            overlayText.setText("Selection sent to judge. \n Waiting for other players...");

            selecting = false;

            System.out.println("the selected indices are:");

            for(int i: selectedIndices){

                System.out.println(i);
                handleExit(cards[i]);
                exitCard(i, null);
                //flipToBack(cards[i], e -> exitCard(i, null));
                //handleExit(cards[i]);

            }

            //Tell the client thread that the player has chosen their cards
            System.out.println("Client chose valid cards");
            System.out.println("number waiting on barrier: " + Injector.getBarrier().getNumberWaiting());
            new Thread(() -> Injector.waitOnBarrier()).start();

        }

    }

    public List<String> getSelected(){

        List<String> out = new ArrayList<String>();

        System.out.println("selected indices size: " + selectedIndices.size());
        System.out.println("contents of selected indices are: ");

        for(int i: selectedIndices){

            System.out.println(i);

            System.out.println("Contents of text on selected index: " + ((Text) cards[i].getChildren().get(1)).getText());

        }

        for(int i: selectedIndices){

            out.add(((Text) cards[i].getChildren().get(1)).getText());

        }

        return out;

    }

    /**
     * Call the same method from an external thread
     * @param expected
     */
    public void setExpectedEXT(int expected){

        Platform.runLater(() -> setExpected(expected));

    }

    /**
     * Updates the expected number of answer cards the player must play
     */
    public void setExpected(int expected){

        this.expected = expected;
        expectedLabel.setText(String.valueOf(expected));

    }

    /**
     * Call the same method from an external thread
     * @param p
     * @param onFinished
     */
    public void flipToBackEXT(StackPane p, EventHandler onFinished){

        Platform.runLater(() -> flipToBack(p, onFinished));

    }

    /**
     * Animation of flipping pane p to the slogan
     *
     * @param p
     * @param onFinished the action to perform after flipping, null if there is none
     */
    public void flipToBack(StackPane p, EventHandler onFinished){

        ScaleTransition hide = new ScaleTransition(Duration.millis(FLIPTIME), p);
        hide.setFromX(1);
        hide.setToX(0);

        ScaleTransition show = new ScaleTransition(Duration.millis(FLIPTIME), p);
        show.setFromX(0);
        show.setToX(1);

        if(onFinished != null){

            show.setOnFinished(onFinished);

        }

        hide.setOnFinished(e -> {

            setToBack(p);
            show.play();

        });

        hide.play();

    }

    /**
     * Call same method from an external thread
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

        ScaleTransition hide = new ScaleTransition(Duration.millis(FLIPTIME), p);
        hide.setFromX(1);
        hide.setToX(0);

        ScaleTransition show = new ScaleTransition(Duration.millis(FLIPTIME), p);
        show.setFromX(0);
        show.setToX(1);

        hide.setOnFinished(e -> {

            setToFrontAndUpdate(p, next);
            show.play();

        });

        hide.play();

    }

    /**
     * Updates the number card specified with the string specified
     * @param p
     * @param str
     */
    public void setToFrontAndUpdate(StackPane p, String str){

        List<Node> nodes = p.getChildren();

        //May need to check size is 1 first
        for(int j = nodes.size() - 1; j > 0; j--){

            nodes.remove(j);

        }

        Rectangle r = (Rectangle) nodes.get(0);
        r.setFill(Color.CORNSILK);
        Text t = new Text(str);

        //Allow 5 pixel margin each side of the text
        t.setWrappingWidth(r.getWidth() - 10);
        t.setFill(Color.BLACK);
        nodes.add(t);

    }

    /**
     * Set card i to be blank with the slogan
     * @param p
     */
    public void setToBack(StackPane p){

        List<Node> nodes = p.getChildren();

        //May need to check size is 1 first
        for(int j = nodes.size() - 1; j > 0; j--){

            nodes.remove(j);

        }

        Color c;
        Color txt;

        if(p.getId().equals("questionCard")){

            c = Color.DARKBLUE;
            txt = Color.WHITE;

        } else {

            c = Color.HOTPINK;
            txt = Color.CORNSILK;

        }

        ((Rectangle) nodes.get(0)).setFill(c);
        Text t = new Text("CHEEKY \n CARDS \n    ;-)");
        t.setFill(txt);
        t.setStyle("-fx-font-size: 15");
        nodes.add(t);

    }

    /**
     * Does an animation for played card index i exiting
     * @param i
     * @param onFinished what to do when the transition finishes, null if nothing
     */
    public void exitPlayed(int i, EventHandler onFinished){

        TranslateTransition transition = new TranslateTransition(Duration.millis(1000), playedCards[i]);
        transition.setByY(-300);
        flipToBack(playedCards[i], e -> transition.play());

        if(onFinished != null){

            transition.setOnFinished(onFinished);

        }

    }

    /**
     * Does an animation for played card index i entering
     * with the string of the new card
     * @param i
     * @param str
     */
    public void enterPlayed(int i, String str){

        TranslateTransition transition = new TranslateTransition(Duration.millis(1000), playedCards[i]);
        transition.setByY(300);
        transition.play();
        transition.setOnFinished(e -> flipToNext(playedCards[i], str));

    }

    /**
     * Does an animation for deck card index i exiting
     * @param i
     * @param onFinished what to do when the transition has finished
     */
    public void exitCard(int i, EventHandler onFinished){

        TranslateTransition transition = new TranslateTransition(Duration.millis(1000), cards[i]);
        transition.setByY(300);

        //First flip card to back then exit
        flipToBack(cards[i], e -> transition.play());

        transition.setOnFinished(onFinished);

    }

    /**
     * Does an animation for deck card index i entering
     * with the string of the new card
     * @param i
     * @param str
     */
    public void enterCard(int i, String str){

        /*
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/

        TranslateTransition transition = new TranslateTransition(Duration.millis(1000), cards[i]);
        transition.setByY(-300);
        transition.play();
        transition.setOnFinished(e -> flipToNext(cards[i], str));

    }

}