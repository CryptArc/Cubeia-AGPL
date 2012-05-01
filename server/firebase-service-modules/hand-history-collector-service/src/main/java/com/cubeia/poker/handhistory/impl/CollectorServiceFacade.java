package com.cubeia.poker.handhistory.impl;

import com.cubeia.firebase.guice.service.Configuration;
import com.cubeia.firebase.guice.service.ContractsConfig;
import com.cubeia.firebase.guice.service.GuiceService;
import com.cubeia.poker.handhistory.api.*;

import java.util.List;

public class CollectorServiceFacade extends GuiceService implements HandHistoryCollectorService {

    @Override
    public Configuration getConfigurationHelp() {
        return new Configuration() {

            @Override
            public ContractsConfig getServiceContract() {
                return new ContractsConfig(CollectorServiceImpl.class, HandHistoryCollectorService.class);
            }
        };
    }


    @Override
    public void reportEvent(int tableId, HandHistoryEvent event) {
        g().reportEvent(tableId, event);
    }

    @Override
    public void startHand(HandIdentification id, List<Player> seats) {
        g().startHand(id, seats);
    }

    @Override
    public void stopHand(int tableId) {
        g().stopHand(tableId);
    }

    @Override
    public void cancelHand(int tableId) {
        g().cancelHand(tableId);
    }

    @Override
    public void reportDeckInfo(int tableId, DeckInfo deckInfo) {
        g().reportDeckInfo(tableId, deckInfo);
    }

    @Override
    public void reportResults(int tableId, Results res) {
        g().reportResults(tableId, res);
    }

    // --- PRIVATE METHODS --- //

    private HandHistoryCollectorService g() {
        return guice(HandHistoryCollectorService.class);
    }
}
