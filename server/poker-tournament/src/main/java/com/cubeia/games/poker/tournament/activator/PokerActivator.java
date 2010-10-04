package com.cubeia.games.poker.tournament.activator;

import com.cubeia.firebase.api.mtt.activator.MttActivator;
import com.cubeia.firebase.api.server.Startable;

public interface PokerActivator extends MttActivator, Startable {

    public void checkTournamentsNow();

}
