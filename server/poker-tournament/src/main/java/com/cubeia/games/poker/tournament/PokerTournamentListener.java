package com.cubeia.games.poker.tournament;

import org.apache.log4j.Logger;

import com.cubeia.firebase.api.mtt.MttInstance;
import com.cubeia.firebase.api.mtt.model.MttRegistrationRequest;
import com.cubeia.firebase.api.mtt.support.MTTStateSupport;
import com.cubeia.firebase.api.mtt.support.registry.PlayerListener;
import com.cubeia.games.poker.tournament.activator.TournamentTableSettings;
import com.cubeia.games.poker.tournament.state.PokerTournamentState;
import com.cubeia.games.poker.tournament.state.PokerTournamentStatus;
import com.cubeia.poker.timing.TimingFactory;

public class PokerTournamentListener implements PlayerListener {

    private static transient Logger log = Logger.getLogger(PokerTournamentListener.class);
    
    private transient PokerTournament pokerTournament;

    private transient PokerTournamentUtil util = new PokerTournamentUtil();

    public PokerTournamentListener(PokerTournament pokerTournament) {
        this.pokerTournament = pokerTournament;
    }

    public void playerRegistered(MttInstance instance, MttRegistrationRequest request) {
        MTTStateSupport state = (MTTStateSupport) instance.getState();
        addJoinedTimestamps(state);
        
        if (tournamentShouldStart(state)) {
            startTournament(instance, state);
        }
    }

    private void addJoinedTimestamps(MTTStateSupport state) {
        PokerTournamentState pokerState = (PokerTournamentState)state.getState();
        if (state.getRegisteredPlayersCount() == 1) {
            pokerState.setFirstRegisteredTime(System.currentTimeMillis());
            
        } else if (state.getRegisteredPlayersCount() == state.getMinPlayers()) {
            pokerState.setLastRegisteredTime(System.currentTimeMillis());
        }
    }

    private void startTournament(MttInstance instance, MTTStateSupport state) {
        PokerTournamentState pokerState = util.getPokerState(instance);
        
        long registrationElapsedTime = pokerState.getLastRegisteredTime() - pokerState.getFirstRegisteredTime();
        log.debug("Starting tournament ["+instance.getId()+" : "+instance.getState().getName()+"]. Registration time was "+registrationElapsedTime+" ms");

        util.setTournamentStatus(instance, PokerTournamentStatus.RUNNING);		
        int tablesToCreate = state.getRegisteredPlayersCount() / state.getSeats();
        if (state.getRegisteredPlayersCount() % state.getSeats() > 0) {
            tablesToCreate++;
        }
        pokerState.setTablesToCreate(tablesToCreate);
        TournamentTableSettings settings = getTableSettings(pokerState);
        pokerTournament.createTables(state, tablesToCreate, "test", settings);
    }

    private boolean tournamentShouldStart(MTTStateSupport state) {
        return state.getRegisteredPlayersCount() == state.getMinPlayers();
    }

    public void playerUnregistered(MttInstance instance, int pid) {
        // TODO Auto-generated method stub

    }

    private TournamentTableSettings getTableSettings(PokerTournamentState state) {
        TournamentTableSettings settings = new TournamentTableSettings();
        settings.setTimingProfile(TimingFactory.getRegistry().getTimingProfile(state.getTiming()));
        return settings;
    }

}
