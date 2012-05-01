package com.cubeia.poker.util;

import com.cubeia.poker.hand.Hand;
import com.cubeia.poker.model.PlayerHand;

import java.util.Comparator;

public class PlayerHandComparator implements Comparator<PlayerHand> {

    private Comparator<Hand> comparator;

    public PlayerHandComparator(Comparator<Hand> comparator) {
        this.comparator = comparator;
    }

    @Override
    public int compare(PlayerHand ph1, PlayerHand ph2) {
        return comparator.compare(ph1.getHand(), ph2.getHand());
    }

}
