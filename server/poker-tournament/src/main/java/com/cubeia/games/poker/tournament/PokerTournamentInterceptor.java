package com.cubeia.games.poker.tournament;

import com.cubeia.firebase.api.mtt.MttInstance;
import com.cubeia.firebase.api.mtt.model.MttRegisterResponse;
import com.cubeia.firebase.api.mtt.model.MttRegistrationRequest;
import com.cubeia.firebase.api.mtt.support.registry.PlayerInterceptor;
import com.cubeia.games.poker.tournament.state.PokerTournamentState;
import com.cubeia.games.poker.tournament.state.PokerTournamentStatus;

public class PokerTournamentInterceptor implements PlayerInterceptor {

	private transient PokerTournamentUtil util = new PokerTournamentUtil();
	
    @SuppressWarnings("unused")
    private final PokerTournament pokerTournament;

    public PokerTournamentInterceptor(PokerTournament pokerTournament) {
        this.pokerTournament = pokerTournament;
    }

    public MttRegisterResponse register(MttInstance instance, MttRegistrationRequest request) {
    	PokerTournamentState state = util.getPokerState(instance);
        if (state.getStatus() != PokerTournamentStatus.REGISTERING) {
            return MttRegisterResponse.ALLOWED;
        } else {
            return MttRegisterResponse.ALLOWED;
        }
    }

    public MttRegisterResponse unregister(MttInstance instance, int pid) {
    	PokerTournamentState state = util.getPokerState(instance);
    	
        if (state.getStatus() != PokerTournamentStatus.REGISTERING) {
            return MttRegisterResponse.DENIED;
        } else {
            return MttRegisterResponse.ALLOWED;
        }
    }

}
