package com.cubeia.poker.hand;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * <p>Generic combination generator</p>
 * 
 * <p>Generates all possible combinations for a given set with a minimum amount
 * of elements.</p>
 *
 * Usage example: 
 * <pre>
 * List<Card> set = new Hand("2s 3s 4s 5s").getCards();
 * Combinator<Card> cg = new Combinator<Card>(set, 3);
 * for(List<Card> combination : cg) {
 *     System.out.println(combination);
 * }
 * </pre>  
 * 
 * @author Fredrik Johansson, Cubeia Ltd
 */
public class Combinator<E> implements Iterator<List<E>>, Iterable<List<E>> {

	private final List<E> set;
	private int[] current;
	private final int[] last;

	public Combinator(List<E> set, int minElements) {
		if(minElements < 1 || minElements > set.size()) {
			throw new IllegalArgumentException("minElements must be > 1 and < set.size(). minElements["+minElements+"] setSize["+set.size()+"]");
		}
		this.set = new ArrayList<E>(set);
		this.current = new int[minElements];
		this.last = new int[minElements];
		for(int i = 0; i < minElements; i++) {
			this.current[i] = i;
			this.last[i] = set.size() - minElements + i;
		}
	}

	public List<List<E>> getAsList() {
		List<List<E>> result = new ArrayList<List<E>>();
		for(List<E> e : this) {
			result.add(new ArrayList<E>(e));
		}
		return result;
	}

	public boolean hasNext() {
		return current != null;
	}

	public Iterator<List<E>> iterator() {
		return this;
	}

	public List<E> next() {
		if(!hasNext()) {
			throw new NoSuchElementException();
		}

		List<E> currentCombination = new ArrayList<E>();
		for(int i : current) {
			currentCombination.add(set.get(i));
		}

		setNextIndexes();
		return currentCombination;
	}

	public void remove() {
		throw new UnsupportedOperationException();
	}

	private void setNextIndexes() {
		for(int i = current.length-1, j = set.size()-1; i >= 0; i--, j--) {
			if(current[i] != j) {
				current[i]++;
				for(int k = i+1; k < current.length; k++) {
					current[k] = current[k-1]+1;
				}
				return;
			}
		}
		current = null;
	}

}