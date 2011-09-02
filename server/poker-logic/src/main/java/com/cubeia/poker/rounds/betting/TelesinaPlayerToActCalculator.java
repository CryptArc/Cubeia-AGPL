package com.cubeia.poker.rounds.betting;

import java.util.List;
import java.util.SortedMap;

import com.cubeia.poker.player.PokerPlayer;
import com.cubeia.poker.util.PokerUtils;

public class TelesinaPlayerToActCalculator implements PlayerToActCalculator {

    @Override
    public PokerPlayer getFirstPlayerToAct(int dealerButtonSeatId, SortedMap<Integer, PokerPlayer> seatingMap) {
        return getNextPlayerToAct(dealerButtonSeatId, seatingMap);
    }

    @Override
    public PokerPlayer getNextPlayerToAct(int lastActedSeatId, SortedMap<Integer, PokerPlayer> seatingMap) {
        PokerPlayer next = null;

        List<PokerPlayer> players = PokerUtils.unwrapList(seatingMap, lastActedSeatId + 1);
        for (PokerPlayer player : players) {
            if (!player.hasFolded() && !player.hasActed() && !player.isSittingOut() && !player.isAllIn()) {
                next = player;
                break;
            }
        }
        return next;
    }

}
