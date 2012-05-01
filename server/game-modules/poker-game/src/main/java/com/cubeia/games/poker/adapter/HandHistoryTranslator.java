package com.cubeia.games.poker.adapter;

import com.cubeia.poker.action.PokerAction;
import com.cubeia.poker.action.PokerActionType;
import com.cubeia.poker.hand.Card;
import com.cubeia.poker.hand.Rank;
import com.cubeia.poker.hand.Suit;
import com.cubeia.poker.handhistory.api.*;
import com.cubeia.poker.player.PokerPlayer;
import com.cubeia.poker.pot.Pot;
import com.cubeia.poker.result.Result;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class HandHistoryTranslator {

    private HandHistoryTranslator() {
    }

    public static PlayerAction translate(PokerAction action) {
        PlayerAction a = new PlayerAction(action.getPlayerId());
        a.setAction(translate(action.getActionType()));
        if (action.getBetAmount() != -1) {
            a.setAmount(Amount.bet(action.getBetAmount()));
        } else if (action.getRaiseAmount() != -1) {
            a.setAmount(Amount.raise(action.getRaiseAmount()));
        } else if (action.getStackAmount() != -1) {
            a.setAmount(Amount.stack(action.getStackAmount()));
        }
        a.setTimout(action.isTimeout());
        return a;
    }

    public static List<GamePot> translate(Collection<Pot> pots) {
        List<GamePot> list = new ArrayList<GamePot>(pots.size());
        for (Pot p : pots) {
            list.add(translate(p));
        }
        return list;
    }

    public static GamePot translate(Pot pot) {
        GamePot p = new GamePot(pot.getId());
        for (PokerPlayer player : pot.getPotContributors().keySet()) {
            p.getPlayers().add(player.getId());
        }
        p.setPotSize(pot.getPotSize());
        return p;
    }

    public static List<GameCard> translateCards(Collection<Card> cards) {
        List<GameCard> list = new ArrayList<GameCard>(cards.size());
        for (Card c : cards) {
            list.add(translate(c));
        }
        return list;
    }

    public static HandResult translate(int playerId, Result result) {
        return new HandResult(playerId, result.getNetResult(), result.getWinningsIncludingOwnBets(), -1, calculateTotalBet(result)); // NB: Rake is set outside this method
    }

    public static PlayerAction.Type translate(PokerActionType type) {
        return PlayerAction.Type.values()[type.ordinal()];
    }

    public static GameCard translate(Card card) {
        return new GameCard(translate(card.getSuit()), translate(card.getRank()));
    }

    public static GameCard.Rank translate(Rank rank) {
        return GameCard.Rank.values()[rank.ordinal()];
    }

    public static GameCard.Suit translate(Suit suit) {
        return GameCard.Suit.values()[suit.ordinal()];
    }


    // --- PRIVATE METHODS --- //

    private static long calculateTotalBet(Result result) {
        return result.getWinningsIncludingOwnBets() - result.getNetResult();
    }
}
