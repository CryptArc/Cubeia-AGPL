package com.cubeia.poker.hand;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Shuffles a list.
 * 
 * This class implements the Fisher-Yates modern algorithm: http://en.wikipedia.org/wiki/Fisher%E2%80%93Yates_shuffle
 * 
 * <code>
 *   To shuffle an array a of n elements (indexes 0..n-1):
 *     for i from n − 1 downto 1 do
 *          j <- random integer with 0 <= j <= i
 *          exchange a[j] and a[i] 
 * </code>
 * 
 * @author w
 */
public class Shuffler<T> {

    private final Random rng;

    public Shuffler(Random rng) {
        this.rng = rng;
    }
    
    /**
     * Returns a new shuffled copy of the given list.
     * @param list list to be shuffled
     * @param rng random number generator
     * @return shuffled list
     */
    public List<T> shuffle(List<T> list) {
        ArrayList<T> shuffledList = new ArrayList<T>(list);
        for (int i = list.size() - 1; i > 0; i--) {
            int j = rng.nextInt(i + 1);
            Collections.swap(shuffledList, j, i);
        }
        return shuffledList;
    }
    
    
}
