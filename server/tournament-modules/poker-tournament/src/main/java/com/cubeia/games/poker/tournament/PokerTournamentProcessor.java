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

import com.cubeia.backend.cashgame.dto.OpenSessionFailedResponse;
import com.cubeia.backend.cashgame.dto.OpenSessionResponse;
import com.cubeia.backend.firebase.CashGamesBackendService;
import com.cubeia.firebase.api.action.mtt.MttDataAction;
import com.cubeia.firebase.api.action.mtt.MttObjectAction;
import com.cubeia.firebase.api.action.mtt.MttRoundReportAction;
import com.cubeia.firebase.api.action.mtt.MttSeatingFailedAction;
import com.cubeia.firebase.api.action.mtt.MttTablesCreatedAction;
import com.cubeia.firebase.api.mtt.MttInstance;
import com.cubeia.firebase.api.mtt.model.MttRegisterResponse;
import com.cubeia.firebase.api.mtt.model.MttRegistrationRequest;
import com.cubeia.firebase.api.mtt.support.MTTStateSupport;
import com.cubeia.firebase.api.mtt.support.registry.PlayerInterceptor;
import com.cubeia.firebase.api.mtt.support.registry.PlayerListener;
import com.cubeia.firebase.guice.inject.Service;
import com.cubeia.firebase.guice.tournament.TournamentAssist;
import com.cubeia.firebase.guice.tournament.TournamentHandler;
import com.cubeia.firebase.io.ProtocolObject;
import com.cubeia.firebase.io.StyxSerializer;
import com.cubeia.games.poker.common.SystemTime;
import com.cubeia.games.poker.io.protocol.ProtocolObjectFactory;
import com.cubeia.games.poker.io.protocol.RequestBlindsStructure;
import com.cubeia.games.poker.io.protocol.RequestPayoutInfo;
import com.cubeia.games.poker.io.protocol.RequestTournamentLobbyData;
import com.cubeia.games.poker.io.protocol.RequestTournamentPlayerList;
import com.cubeia.games.poker.io.protocol.RequestTournamentTable;
import com.cubeia.games.poker.tournament.lobby.TournamentLobby;
import com.cubeia.games.poker.tournament.messages.CloseTournament;
import com.cubeia.poker.tournament.history.storage.api.TournamentHistoryPersistenceService;
import com.google.common.annotations.VisibleForTesting;
import com.google.inject.Inject;
import org.apache.log4j.Logger;
import org.apache.log4j.MDC;

import java.io.IOException;

public class PokerTournamentProcessor implements TournamentHandler, PlayerInterceptor, PlayerListener {

    // Use %X{pokerid} in the layout pattern to include this information.
    private static final String MDC_TAG = "tableid";

    private static transient Logger log = Logger.getLogger(PokerTournamentProcessor.class);

    private PokerTournamentUtil util = new PokerTournamentUtil();

    @Inject
    private SystemTime dateFetcher;

    /** Used for serializing and de-serializing packets to / from the client, with this protocol: {@link ProtocolObjectFactory}. */
    @Inject
    private StyxSerializer serializer;

    @Inject
    private TournamentAssist support;

    @Service
    private TournamentHistoryPersistenceService historyService;

    @Service
    private CashGamesBackendService backend;

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
        log.debug("Date fetcher: " + dateFetcher);
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
            prepareTournament(instance).handleTablesCreated(action);
        } finally {
            MDC.remove(MDC_TAG);
        }
    }

    @Override
    public void process(MttObjectAction action, MttInstance instance) {
        try {
            MDC.put(MDC_TAG, "Tournament[" + instance.getId() + "]");
            Object object = action.getAttachment();
            PokerTournament tournament = prepareTournament(instance);
            log.debug("Received mtt object action: " + object);
            if (object instanceof TournamentTrigger) {
                TournamentTrigger trigger = (TournamentTrigger) object;
                tournament.handleTrigger(trigger);
            } else if (object instanceof OpenSessionResponse) {
                tournament.handleOpenSessionResponse((OpenSessionResponse) object);
            } else if (object instanceof OpenSessionFailedResponse) {
                tournament.handleOpenSessionResponseFailed((OpenSessionFailedResponse) object);
            } else if (object instanceof CloseTournament) {
                tournament.closeTournament();
            } else {
                log.warn("Unexpected attachment: " + object);
            }
        } finally {
            MDC.remove(MDC_TAG);
        }
    }

    @Override
    public void process(MttDataAction action, MttInstance instance) {
        StyxSerializer serializer = new StyxSerializer(new ProtocolObjectFactory());
        try {
            ProtocolObject packet = serializer.unpack(action.getData());
            int playerId = action.getPlayerId();
            if (packet instanceof RequestTournamentPlayerList) {
                prepareTournamentLobby(instance).sendPlayerListTo(playerId);
            } else if (packet instanceof RequestBlindsStructure) {
                prepareTournamentLobby(instance).sendBlindsStructureTo(playerId);
            } else if (packet instanceof RequestPayoutInfo) {
                prepareTournamentLobby(instance).sendPayoutInfoTo(playerId);
            } else if (packet instanceof RequestTournamentLobbyData) {
                prepareTournamentLobby(instance).sendTournamentLobbyDataTo(playerId);
            } else if (packet instanceof RequestTournamentTable) {
                prepareTournamentLobby(instance).sendTournamentTableTo(playerId);
            }
        } catch (IOException e) {
            log.warn("Failed de-serializing " + action, e);
        }
    }

    @Override
    public void process(MttSeatingFailedAction mttSeatingFailedAction, MttInstance instance) {
        log.error("Seating failed: " + mttSeatingFailedAction);
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
    public void tournamentDestroyed(MttInstance instance) {
        log.debug("Tournament " + instance + " destroyed.");
    }

    @Override
    public MttRegisterResponse register(MttInstance instance, MttRegistrationRequest request) {
        try {
            MDC.put(MDC_TAG, "Tournament[" + instance.getId() + "]");
            return prepareTournament(instance).checkRegistration(request);
        } finally {
            MDC.remove(MDC_TAG);
        }
    }

    @Override
    public MttRegisterResponse unregister(MttInstance instance, int pid) {
        try {
            MDC.put(MDC_TAG, "Tournament[" + instance.getId() + "]");
            return prepareTournament(instance).checkUnregistration(pid);
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
        if (historyService == null) {
            historyService = instance.getServiceRegistry().getServiceInstance(TournamentHistoryPersistenceService.class);
        }
        if (backend == null) {
            backend = instance.getServiceRegistry().getServiceInstance(CashGamesBackendService.class);
        }
        tournament.injectTransientDependencies(instance, support, util.getStateSupport(instance), historyService, backend, dateFetcher);
    }

    private PokerTournament prepareTournament(MttInstance instance) {
        PokerTournament tournament = (PokerTournament) instance.getState().getState();
        injectDependencies(tournament, instance);
        return tournament;
    }

    private TournamentLobby prepareTournamentLobby(MttInstance instance) {
        return new TournamentLobby(instance, serializer, util.getStateSupport(instance), util.getPokerState(instance), dateFetcher);
    }

    public void setHistoryService(TournamentHistoryPersistenceService historyService) {
        this.historyService = historyService;
    }

    public void setBackend(CashGamesBackendService backend) {
        this.backend = backend;
    }

    @VisibleForTesting
    void setDateFetcher(SystemTime dateFetcher) {
        this.dateFetcher = dateFetcher;
    }
}
