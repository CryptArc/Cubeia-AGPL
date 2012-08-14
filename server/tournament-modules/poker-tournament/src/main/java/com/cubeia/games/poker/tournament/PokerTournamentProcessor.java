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
import com.cubeia.firebase.api.mtt.model.MttRegisterResponse;
import com.cubeia.firebase.api.mtt.model.MttRegistrationRequest;
import com.cubeia.firebase.api.mtt.support.MTTStateSupport;
import com.cubeia.firebase.api.mtt.support.registry.PlayerInterceptor;
import com.cubeia.firebase.api.mtt.support.registry.PlayerListener;
import com.cubeia.firebase.guice.tournament.TournamentAssist;
import com.cubeia.firebase.guice.tournament.TournamentHandler;
import com.google.inject.Inject;
import org.apache.log4j.Logger;
import org.apache.log4j.MDC;

public class PokerTournamentProcessor implements TournamentHandler, PlayerInterceptor, PlayerListener {

    // Use %X{pokerid} in the layout pattern to include this information.
    private static final String MDC_TAG = "tableid";

    private static transient Logger log = Logger.getLogger(PokerTournamentProcessor.class);

    private PokerTournamentUtil util = new PokerTournamentUtil();

    @Inject
    private TournamentAssist support;

    @Override
    public PlayerInterceptor getPlayerInterceptor(MTTStateSupport state) {
        return this;
    }

    @Override
    public PlayerListener getPlayerListener(MTTStateSupport state) {
        return this;
    }

    @Override
    public void process(MttRoundReportAction action, MttInstance instance) {
        try {
            MDC.put(MDC_TAG, "Tournament[" + instance.getId() + "]");
            prepareTournament(instance).processRoundReport(action);
        } finally {
            MDC.remove(MDC_TAG);
        }
    }


    @Override
    public void process(MttTablesCreatedAction action, MttInstance instance) {
        log.info("Tables created: " + action + " instance: " + instance);
        try {
            MDC.put(MDC_TAG, "Tournament[" + instance.getId() + "]");
            prepareTournament(instance).handleTablesCreated();
        } finally {
            MDC.remove(MDC_TAG);
        }
    }

    @Override
    public void process(MttObjectAction action, MttInstance instance) {
        try {
            MDC.put(MDC_TAG, "Tournament[" + instance.getId() + "]");
            Object command = action.getAttachment();
            if (command instanceof TournamentTrigger) {
                TournamentTrigger trigger = (TournamentTrigger) command;
                prepareTournament(instance).handleTrigger(trigger);
            }
        } finally {
            MDC.remove(MDC_TAG);
        }

    }

    @Override
    public void tournamentCreated(MttInstance instance) {
        log.info("Tournament created: " + instance);
        try {
            MDC.put(MDC_TAG, "Tournament[" + instance.getId() + "]");
            prepareTournament(instance).tournamentCreated();
        } finally {
            MDC.remove(MDC_TAG);
        }
    }

    @Override
    public void tournamentDestroyed(MttInstance mttInstance) {
        log.debug("Tournament " + mttInstance + " destroyed.");
    }

    @Override
    public MttRegisterResponse register(MttInstance instance, MttRegistrationRequest request) {
        try {
            MDC.put(MDC_TAG, "Tournament[" + instance.getId() + "]");
            return prepareTournament(instance).register(request);
        } finally {
            MDC.remove(MDC_TAG);
        }
    }

    @Override
    public MttRegisterResponse unregister(MttInstance instance, int pid) {
        try {
            MDC.put(MDC_TAG, "Tournament[" + instance.getId() + "]");
            return prepareTournament(instance).unregister(pid);
        } finally {
            MDC.remove(MDC_TAG);
        }
    }

    @Override
    public void playerRegistered(MttInstance instance, MttRegistrationRequest request) {
        try {
            MDC.put(MDC_TAG, "Tournament[" + instance.getId() + "]");
            prepareTournament(instance).playerRegistered(request);
        } finally {
            MDC.remove(MDC_TAG);
        }
    }

    @Override
    public void playerUnregistered(MttInstance instance, int pid) {
        try {
            MDC.put(MDC_TAG, "Tournament[" + instance.getId() + "]");
            prepareTournament(instance).playerUnregistered(pid);
        } finally {
            MDC.remove(MDC_TAG);
        }
    }

    public void setSupport(TournamentAssist support) {
        this.support = support;
    }

    private void injectDependencies(PokerTournament tournament, MttInstance instance) {
        tournament.injectTransientDependencies(instance, support, util.getStateSupport(instance), instance.getMttNotifier());
    }

    private PokerTournament prepareTournament(MttInstance instance) {
        PokerTournament tournament = (PokerTournament) instance.getState().getState();
        injectDependencies(tournament, instance);
        return tournament;
    }
}
