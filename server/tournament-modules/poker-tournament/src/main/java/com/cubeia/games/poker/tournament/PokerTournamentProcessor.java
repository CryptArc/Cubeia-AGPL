/**
 * Copyright (C) 2010 Cubeia Ltd <info@cubeia.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.cubeia.games.poker.tournament;

import com.cubeia.firebase.api.action.mtt.MttObjectAction;
import com.cubeia.firebase.api.action.mtt.MttRoundReportAction;
import com.cubeia.firebase.api.action.mtt.MttTablesCreatedAction;
import com.cubeia.firebase.api.mtt.MttInstance;
import com.cubeia.firebase.api.mtt.support.MTTStateSupport;
import com.cubeia.firebase.api.mtt.support.MTTSupport;
import com.cubeia.firebase.api.mtt.support.registry.PlayerInterceptor;
import com.cubeia.firebase.api.mtt.support.registry.PlayerListener;
import org.apache.log4j.Logger;
import org.apache.log4j.MDC;

public class PokerTournamentProcessor extends MTTSupport {

    // Use %X{pokerid} in the layout pattern to include this information.
    private static final String MDC_TAG = "pokerid";

    private static transient Logger log = Logger.getLogger(PokerTournamentProcessor.class);

    private PokerTournamentUtil util = new PokerTournamentUtil();

    @Override
    public PlayerInterceptor getPlayerInterceptor(MTTStateSupport state) {
        return new PokerTournamentInterceptor(this);
    }

    @Override
    public PlayerListener getPlayerListener(MTTStateSupport state) {
        return new PokerTournamentListener(this);
    }

    @Override
    public void process(MttRoundReportAction action, MttInstance instance) {
        try {
            MDC.put(MDC_TAG, "Tournament[" + instance.getId() + "]");
            PokerTournament tournament = (PokerTournament) instance.getState().getState();
            injectDependencies(tournament, instance);
            tournament.processRoundReport(action);
        } finally {
            MDC.remove(MDC_TAG);
        }
    }


    @Override
    public void process(MttTablesCreatedAction action, MttInstance instance) {
        log.info("Tables created: " + action + " instance: " + instance);
        try {
            MDC.put(MDC_TAG, "Tournament[" + instance.getId() + "]");
            PokerTournament tournament = (PokerTournament) instance.getState().getState();
            injectDependencies(tournament, instance);
            tournament.handleTablesCreated();
        } finally {
            MDC.remove(MDC_TAG);
        }
    }

    @Override
    public void process(MttObjectAction action, MttInstance instance) {
        try {
            MDC.put(MDC_TAG, "Tournament[" + instance.getId() + "]");
            MTTStateSupport state = (MTTStateSupport) instance.getState();
            Object command = action.getAttachment();
            if (command instanceof TournamentTrigger) {
                TournamentTrigger trigger = (TournamentTrigger) command;
                switch (trigger) {
                    case START:
                        log.debug("START TOURNAMENT!");
                        sendRoundStartActionToTables(state, state.getTables());
                        break;
                }
            }
        } finally {
            MDC.remove(MDC_TAG);
        }

    }

    @Override
    public void tournamentCreated(MttInstance mttInstance) {

    }

    public void tournamentDestroyed(MttInstance mttInstance) {
        // TODO Auto-generated method stub

    }

    private void injectDependencies(PokerTournament tournament, MttInstance instance) {
        tournament.injectTransientDependencies(instance, this, util.getStateSupport(instance), instance.getMttNotifier());
    }

}
