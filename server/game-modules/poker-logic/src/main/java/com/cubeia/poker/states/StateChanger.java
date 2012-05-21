package com.cubeia.poker.states;

/**
 * Interface for allowing the states to change the state in the holder, without introducing a circular dependency.
 *
 */
public interface StateChanger {

    /**
     * Changes the current state the new state.
     *
     * @param newState the new state
     */
    public void changeState(PokerGameSTM newState);
}
