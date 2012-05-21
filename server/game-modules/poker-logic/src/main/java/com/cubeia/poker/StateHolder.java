package com.cubeia.poker;

import com.cubeia.poker.states.PokerGameSTM;
import com.cubeia.poker.states.StateChanger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

/**
 * This class holds the current state and is responsible for changing the current state.
 *
 * It exists so that PokerState won't have to expose a public "changeState" method.
 */
public class StateHolder implements StateChanger, Serializable {

    private PokerGameSTM currentState;

    private static final Logger log = LoggerFactory.getLogger(StateHolder.class);

    @Override
    public void changeState(PokerGameSTM newState) {
        if (newState == null) throw new IllegalArgumentException("New state is null");
        log.debug("Changing state from " + currentState + " to " + newState);
        currentState = newState;
        currentState.enterState();
    }

    PokerGameSTM get() {
        return currentState;
    }

}
