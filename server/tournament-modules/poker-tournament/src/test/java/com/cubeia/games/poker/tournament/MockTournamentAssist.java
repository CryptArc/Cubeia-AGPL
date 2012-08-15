package com.cubeia.games.poker.tournament;

import com.cubeia.firebase.api.action.mtt.MttObjectAction;
import com.cubeia.firebase.api.action.mtt.MttRoundReportAction;
import com.cubeia.firebase.api.action.mtt.MttTablesCreatedAction;
import com.cubeia.firebase.api.mtt.MttInstance;
import com.cubeia.firebase.api.mtt.support.MTTStateSupport;
import com.cubeia.firebase.api.mtt.support.MTTSupport;
import com.cubeia.firebase.api.mtt.support.registry.PlayerInterceptor;
import com.cubeia.firebase.api.mtt.support.registry.PlayerListener;
import com.cubeia.firebase.guice.tournament.TournamentAssist;

public class MockTournamentAssist extends MTTSupport implements TournamentAssist {

    @Override
    public void process(MttRoundReportAction action, MttInstance mttInstance) {

    }

    @Override
    public void process(MttTablesCreatedAction action, MttInstance instance) {

    }

    @Override
    public void process(MttObjectAction action, MttInstance instance) {

    }

    @Override
    public void tournamentCreated(MttInstance mttInstance) {

    }

    @Override
    public void tournamentDestroyed(MttInstance mttInstance) {

    }

    @Override
    public PlayerListener getPlayerListener(MTTStateSupport state) {
        return null;
    }

    @Override
    public PlayerInterceptor getPlayerInterceptor(MTTStateSupport state) {
        return null;
    }
}
