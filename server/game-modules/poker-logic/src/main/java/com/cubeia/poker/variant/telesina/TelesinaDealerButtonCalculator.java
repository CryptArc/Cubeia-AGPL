package com.cubeia.poker.variant.telesina;

import com.cubeia.poker.player.PokerPlayer;
import com.google.common.collect.Iterators;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.SortedMap;

public class TelesinaDealerButtonCalculator {

    public int getNextDealerSeat(SortedMap<Integer, PokerPlayer> currentSeatingMap, int currentDealerSeatId, boolean wasHandCancelled) {

        // no players seated the dealerbutton should not move
        if (currentSeatingMap.isEmpty()) {
            return currentDealerSeatId;
        }

        // cancelled hand should not move dealerbutton
        if (wasHandCancelled) {
            return currentDealerSeatId;
        }

        // one player will make never ending loop
        if (currentSeatingMap.size() == 1) {
            return currentSeatingMap.firstKey();
        }

        ArrayList<Integer> seatList = new ArrayList<Integer>(currentSeatingMap.keySet());
        if (!seatList.contains(currentDealerSeatId)) {
            seatList.add(currentDealerSeatId);
        }
        Collections.sort(seatList);

        Iterator<Integer> seatIterator = Iterators.cycle(seatList);

        // wind ahead until
        int seat = seatIterator.next();

        while (seat < currentDealerSeatId) {
            seat = seatIterator.next();
        }
        return seatIterator.next();


    }

}
