package com.cubeia.games.poker.tournament.activator;

public interface PokerTournamentActivator {

    void checkInstancesNow();

    void shutdownTournament(int mttInstanceId);

    void startTournament(int mttInstanceId);
    
    void destroyTournament(int mttInstanceId);

}