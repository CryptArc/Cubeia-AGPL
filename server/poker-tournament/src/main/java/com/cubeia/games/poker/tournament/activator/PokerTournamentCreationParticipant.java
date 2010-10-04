package com.cubeia.games.poker.tournament.activator;

import org.apache.log4j.Logger;

import com.cubeia.firebase.api.lobby.LobbyAttributeAccessor;
import com.cubeia.firebase.api.lobby.LobbyPath;
import com.cubeia.firebase.api.mtt.MTTState;
import com.cubeia.firebase.api.mtt.activator.CreationParticipant;
import com.cubeia.firebase.api.mtt.support.MTTStateSupport;
import com.cubeia.games.poker.persistence.tournament.model.TournamentConfiguration;
import com.cubeia.games.poker.persistence.tournament.model.TournamentStartType;
import com.cubeia.games.poker.tournament.PokerTournamentLobbyAttributes;
import com.cubeia.games.poker.tournament.state.PokerTournamentState;
import com.cubeia.games.poker.tournament.state.PokerTournamentStatus;
import com.cubeia.poker.timing.Timings;

public class PokerTournamentCreationParticipant implements CreationParticipant {

    private final TournamentConfiguration config;
    
	private Timings timing = Timings.DEFAULT;
	
	@SuppressWarnings("unused")
	private static transient Logger log = Logger.getLogger(PokerTournamentCreationParticipant.class);
	
	public PokerTournamentCreationParticipant(TournamentConfiguration config) {
        this.config = config;
    }
	
	public PokerTournamentCreationParticipant(String name, int minPlayers) {
	    this(name, minPlayers, Timings.DEFAULT);
	}
	
	public PokerTournamentCreationParticipant(String name, int minPlayers, Timings timing) {
	    TournamentConfiguration config = new TournamentConfiguration();
        config.setName(name);
        config.setMinPlayers(minPlayers);
        config.setMaxPlayers(minPlayers);
        config.setSeatsPerTable(10);
        config.setStartType(TournamentStartType.SIT_AND_GO);
        config.setTimingType(timing.ordinal());
        
        this.config = config;
    }

	public LobbyPath getLobbyPathForTournament(MTTState mtt) {
		return new LobbyPath(mtt.getMttLogicId(), "sitandgo");
	}

	public void tournamentCreated(MTTState mtt, LobbyAttributeAccessor acc) {
		System.out.println("Poker tournament created. MTT: ["+mtt.getId()+"]"+mtt.getName());
		MTTStateSupport stateSupport = ((MTTStateSupport) mtt);
        stateSupport.setGameId(7);
        stateSupport.setSeats(config.getSeatsPerTable());
        stateSupport.setName(config.getName());
        stateSupport.setCapacity(config.getMaxPlayers());
        stateSupport.setMinPlayers(config.getMinPlayers());
        
        PokerTournamentState pokerState = new PokerTournamentState();
        pokerState.setStatus(PokerTournamentStatus.REGISTERING);
        pokerState.setTiming(Timings.values()[config.getTimingType()]);
        stateSupport.setState(pokerState);

        // Sit and go tournaments start in registering mode.
        acc.setStringAttribute(PokerTournamentLobbyAttributes.STATUS.name(), PokerTournamentStatus.REGISTERING.name());
        acc.setIntAttribute(PokerTournamentLobbyAttributes.TABLE_SIZE.name(), 10);      
        acc.setStringAttribute("SPEED", timing.name());
	}
}