package com.cubeia.poker.rounds.dealing;

public interface Dealer {

    void dealCommunityCards();

    void dealExposedPocketCards();

    void dealInitialPocketCards();

    void exposeShowdownCards();
}
