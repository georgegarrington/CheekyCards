package models;

import java.util.Collection;
import java.util.Collections;
import java.util.Stack;

public class Coordinator {

    private Stack<String> whiteCards;
    private Stack<String> blackCards;

    public Coordinator(Collection<String> whiteCards, Collection<String> blackCards){

        this.whiteCards = new Stack<String>();
        this.blackCards = new Stack<String>();
        this.whiteCards.addAll(whiteCards);
        this.blackCards.addAll(blackCards);
        Collections.shuffle(this.whiteCards);
        Collections.shuffle(this.blackCards);

    }

    //Most likely won't need this constructor, choose to use arrays or some kind of collection later
    public Coordinator(String[] whiteCards, String[] blackCards){

        this.whiteCards = new Stack<String>();
        this.blackCards = new Stack<String>();

        for(String s: whiteCards){

            this.whiteCards.push(s);

        }

        for(String s: blackCards){

            this.blackCards.push(s);

        }

        Collections.shuffle(this.whiteCards);
        Collections.shuffle(this.blackCards);

    }



}