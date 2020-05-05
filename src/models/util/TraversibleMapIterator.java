package models.util;

import java.util.*;

/**
 * Used by the controller class to go back and forth between the options, a data structure
 * where you can go right and left but which updates the index it's "head" is at
 * each time you call goRight() or goLeft()
 */
public class TraversibleMapIterator<U, V> {

    private Map<U, V> map;
    private List<U> orderedKeys;
    private int endIndex;

    //The current index position the iterator "head" is at
    private int i = 0;

    public TraversibleMapIterator(Map<U, V> map){

        this.map = map;
        orderedKeys = new ArrayList<U>();
        orderedKeys.addAll(map.keySet());
        endIndex = map.size() - 1;

    }

    /**
     * Attempts to move the index 1 to the right if index is not at the right extreme,
     * if it is successful then returns the value at this new index. If it fails
     * then it returns null
     * @return
     */
    public V goRight(){

        if(i < endIndex){

            i++;
            return map.get(orderedKeys.get(i));

        } else {

            return null;

        }

    }

    /**
     * Attempts to move the index 1 to the left if index is not at the left extreme,
     * if it is successful then returns the value at this new index. If it fails
     * then it returns null
     * @return
     */
    public V goLeft(){

        if(i > 0){

            i--;
            return map.get(orderedKeys.get(i));

        } else {

            return null;

        }

    }

    public V getCurrentObj(){

        return map.get(orderedKeys.get(i));

    }

    public int getCurrentIndex(){

        return i;

    }

    public List<U> getOrderedKeys(){

        return orderedKeys;

    }

}