package com.cubeia.games.poker.tournament;

import com.cubeia.firebase.api.mtt.MttInstance;
import com.cubeia.firebase.api.mtt.support.MTTStateSupport;
import com.cubeia.games.poker.tournament.state.PokerTournamentState;
import com.cubeia.games.poker.tournament.state.PokerTournamentStatus;

public class PokerTournamentUtil {

	public PokerTournamentState getPokerState(MttInstance instance) {
		return (PokerTournamentState) instance.getState().getState();
	}

	public void setTournamentStatus(MttInstance instance, PokerTournamentStatus status) {
		instance.getLobbyAccessor().setStringAttribute(PokerTournamentLobbyAttributes.STATUS.name(), status.name());
		getPokerState(instance).setStatus(status);
	}

	public MTTStateSupport getStateSupport(MttInstance instance) {
		return (MTTStateSupport) instance.getState();
	}

	public PokerTournamentState getPokerState(MTTStateSupport state) {
		return (PokerTournamentState) state.getState();
	}

}
