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

package com.cubeia.games.poker.adapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;

import se.jadestone.dicearena.game.poker.network.protocol.BestHand;
import se.jadestone.dicearena.game.poker.network.protocol.CardToDeal;
import se.jadestone.dicearena.game.poker.network.protocol.DealPrivateCards;
import se.jadestone.dicearena.game.poker.network.protocol.DealPublicCards;
import se.jadestone.dicearena.game.poker.network.protocol.Enums;
import se.jadestone.dicearena.game.poker.network.protocol.Enums.ActionType;
import se.jadestone.dicearena.game.poker.network.protocol.Enums.PotType;
import se.jadestone.dicearena.game.poker.network.protocol.Enums.Rank;
import se.jadestone.dicearena.game.poker.network.protocol.Enums.Suit;
import se.jadestone.dicearena.game.poker.network.protocol.ExposePrivateCards;
import se.jadestone.dicearena.game.poker.network.protocol.GameCard;
import se.jadestone.dicearena.game.poker.network.protocol.HandEnd;
import se.jadestone.dicearena.game.poker.network.protocol.PerformAction;
import se.jadestone.dicearena.game.poker.network.protocol.PlayerAction;
import se.jadestone.dicearena.game.poker.network.protocol.PlayerBalance;
import se.jadestone.dicearena.game.poker.network.protocol.Pot;
import se.jadestone.dicearena.game.poker.network.protocol.PotTransfer;
import se.jadestone.dicearena.game.poker.network.protocol.PotTransfers;
import se.jadestone.dicearena.game.poker.network.protocol.RequestAction;

import com.cubeia.firebase.api.action.GameDataAction;
import com.cubeia.games.poker.util.ProtocolFactory;
import com.cubeia.poker.action.ActionRequest;
import com.cubeia.poker.action.PokerAction;
import com.cubeia.poker.action.PokerActionType;
import com.cubeia.poker.action.PossibleAction;
import com.cubeia.poker.hand.Card;
import com.cubeia.poker.hand.HandType;
import com.cubeia.poker.model.RatedPlayerHand;
import com.cubeia.poker.player.PokerPlayer;
import com.cubeia.poker.pot.PotTransition;
import com.google.common.annotations.VisibleForTesting;

/**
 * Translates poker-logic internal actions to the styx wire-protocol
 * as defined in poker-protocol.
 *
 * @author Fredrik Johansson, Cubeia Ltd
 */
public class ActionTransformer {
    
    @SuppressWarnings("unused")
    private static transient Logger log = Logger.getLogger(ActionTransformer.class);
    
    private static AtomicInteger sequenceCounter = new AtomicInteger();
	
	public static RequestAction transform(ActionRequest request) {
		RequestAction packet = new RequestAction();
		packet.timeToAct = (int)request.getTimeToAct();
		packet.player = request.getPlayerId();
		packet.seq = getNextSequence();
		
		List<PlayerAction> allowed = new LinkedList<PlayerAction>();
		for (PossibleAction option : request.getOptions()) {
			PlayerAction playerOption = createPlayerAction(option.getActionType());
			// FIXME: Casting to integer here since Flash does not support long values!
			playerOption.minAmount = (int)option.getMinAmount();
			playerOption.maxAmount = (int)option.getMaxAmount();
			allowed.add(playerOption);
		}
		packet.allowedActions = allowed;
		
		return packet;
	}
	
	public static PerformAction transform(PokerAction pokerAction, PokerPlayer pokerPlayer) {
		PerformAction packet = new PerformAction();
		PlayerAction action = createPlayerAction(pokerAction.getActionType());
		packet.action = action;
		// FIXME: Flash does not support longs...
		packet.betAmount = (int) pokerAction.getBetAmount();
		packet.raiseAmount = (int) pokerAction.getRaiseAmount();
		packet.stackAmount = (int) pokerPlayer.getBetStack();
		packet.player = pokerAction.getPlayerId();
		packet.timeout = pokerAction.isTimeout();
		packet.balance = (int)pokerPlayer.getBalance();
		return packet;
	}
	
	public static PokerActionType transform(ActionType protocol) {
		PokerActionType type;
		switch(protocol) {
			case FOLD:
				type = PokerActionType.FOLD;
				break;
				
			case CHECK:
				type = PokerActionType.CHECK;
				break;
				
			case CALL:
				type = PokerActionType.CALL;
				break;
				
			case BET:
				type = PokerActionType.BET;
				break;
				
			case BIG_BLIND:
				type = PokerActionType.BIG_BLIND;
				break;
				
			case SMALL_BLIND:
				type = PokerActionType.SMALL_BLIND;
				break;
				
			case RAISE:
				type = PokerActionType.RAISE;
				break;
				
			case DECLINE_ENTRY_BET:
				type = PokerActionType.DECLINE_ENTRY_BET;
				break;
				
			case ANTE:
			    type = PokerActionType.ANTE;
				break;
				
			default:
			    throw new UnsupportedOperationException("unsupported action type: " + protocol.name());
		}
		return type;
	}
	
	@VisibleForTesting
	protected static PlayerAction createPlayerAction(PokerActionType actionType) {
		PlayerAction action = new PlayerAction();
		switch(actionType) {
			case FOLD:
				action.type = ActionType.FOLD;
				break;
				
			case CHECK:
				action.type = ActionType.CHECK;
				break;
				
			case CALL:
				action.type = ActionType.CALL;
				break;
				
			case BET:
				action.type = ActionType.BET;
				break;
				
			case BIG_BLIND:
				action.type = ActionType.BIG_BLIND;
				break;
				
			case SMALL_BLIND:
				action.type = ActionType.SMALL_BLIND;
				break;
				
			case RAISE:
				action.type = ActionType.RAISE;
				break;
			
			case DECLINE_ENTRY_BET:
			    action.type = ActionType.DECLINE_ENTRY_BET;
				break;
			    
			case ANTE:
			    action.type = ActionType.ANTE;
			    break;
				
			default:
                throw new UnsupportedOperationException("unsupported action type: " + actionType.name());
		}
		
		return action;
	}

	/**
	 * 
	 * @param playerId, the player receiving the cards
	 * @param cards, the cards to be dealt
	 * @param hidden, true if the suit and rank should be of type HIDDEN only
	 * @return
	 */
	public static DealPrivateCards createPrivateCardsPacket(int playerId, List<Card> cards, boolean hidden) {
		DealPrivateCards packet = new DealPrivateCards();
		packet.cards = new LinkedList<CardToDeal>();
		for (Card card : cards) {
			GameCard gCard = new GameCard();
			gCard.cardId = card.getId(); 
			
			if (!hidden) {
				gCard.rank = convertRankToProtocolEnum(card.getRank());    
				gCard.suit = convertSuitToProtocolEnum(card.getSuit());    
			} else {
				gCard.rank = Enums.Rank.HIDDEN;
				gCard.suit = Enums.Suit.HIDDEN;
			}
			
			CardToDeal deal = new CardToDeal();
			deal.player = playerId;
			deal.card = gCard;
			packet.cards.add(deal);
		}
		return packet;
	}
	
	public static Rank convertRankToProtocolEnum(com.cubeia.poker.hand.Rank rank) {
	    return Enums.Rank.values()[rank.ordinal()];
	}
	
    public static Suit convertSuitToProtocolEnum(com.cubeia.poker.hand.Suit suit) {
        return Enums.Suit.values()[suit.ordinal()];
    }
    
    public static Enums.HandType convertHandTypeToEnum(HandType handType) {
        return Enums.HandType.values()[handType.ordinal()];
    }
    
	public static DealPublicCards createPublicCardsPacket(List<Card> cards) {
		DealPublicCards packet = new DealPublicCards();
		packet.cards = new LinkedList<GameCard>();
		for (Card card : cards) {
			GameCard gCard = new GameCard();
			
			gCard.rank = Enums.Rank.values()[card.getRank().ordinal()];
			gCard.suit = Enums.Suit.values()[card.getSuit().ordinal()];
			gCard.cardId = card.getId();
			
			packet.cards.add(gCard);
		}
		return packet;
	}
	
	public static ExposePrivateCards createExposeCardsPacket(int playerId, List<Card> cards) {
		ExposePrivateCards packet = new ExposePrivateCards();
		packet.cards = new LinkedList<CardToDeal>();
		for (Card card : cards) {
			GameCard gCard = new GameCard();
			gCard.rank = Enums.Rank.values()[card.getRank().ordinal()];
			gCard.suit = Enums.Suit.values()[card.getSuit().ordinal()];
			gCard.cardId = card.getId();
			
			CardToDeal deal = new CardToDeal(playerId, gCard);
			packet.cards.add( deal);
		}
		return packet;
	}

    public static BestHand createBestHandPacket(int playerId, HandType handType, List<Card> cardsInHand) {
        List<GameCard> gameCards = convertCards(cardsInHand);
        
        BestHand bestHand = new BestHand(playerId, convertHandTypeToEnum(handType), gameCards);
        return bestHand;
    }

    @VisibleForTesting
    protected static List<GameCard> convertCards(List<Card> cardsInHand) {
        List<GameCard> gameCards = new ArrayList<GameCard>();
        for (Card c : cardsInHand) {
            gameCards.add(new GameCard(c.getId(), convertSuitToProtocolEnum(c.getSuit()), convertRankToProtocolEnum(c.getRank())));
        }
        return gameCards;
    }

	public static HandEnd createHandEndPacket(Collection<RatedPlayerHand> hands, PotTransfers potTransfers) {
		HandEnd packet = new HandEnd();
		packet.hands = new LinkedList<BestHand>();

		for (RatedPlayerHand ratedHand : hands) {
			List<GameCard> cards = new ArrayList<GameCard>();

			for (Card card : ratedHand.getBestHandCards()) {
				cards.add(new GameCard(
						card.getId() == null ? -1 : card.getId(), 
						convertSuitToProtocolEnum(card.getSuit()), 
						convertRankToProtocolEnum(card.getRank())));
			}

			BestHand best = new BestHand(ratedHand.getPlayerId(), convertHandTypeToEnum(ratedHand.getBestHandType()), cards);
			
			packet.hands.add(best);
		}
		
        packet.potTransfers = potTransfers;

		return packet;
	}
	
	public static Pot createPotUpdatePacket(int id, long amount) {
	    Pot packet = new Pot();
	    packet.amount = (int) amount;
	    packet.id = (byte) id; 
	    packet.type = id == 0 ? PotType.MAIN : PotType.SIDE; 
	    return packet;
	}
	
	
	public static GameDataAction createPlayerBalanceAction(int balance, int pendingBalance, int playerId, int tableId) {
		return new ProtocolFactory().createGameAction(new PlayerBalance(balance, pendingBalance, playerId), playerId, tableId);
	}
	
	private static int getNextSequence() {
	    int seq = sequenceCounter.incrementAndGet();
	    if (seq < 0) {
	        // This is not thread safe in the respect that we might set
	        // the counter to 0 multiple times. However, this should be
	        // fine since we will not be able to this on the same table.
	        seq = 0;
	        sequenceCounter.set(seq);
	    }
	    return seq;
	}

    public static PotTransfer createPotTransferPacket(PotTransition potTransition) {
        PotTransfer potTransfer = new PotTransfer(
            (byte) potTransition.getPot().getId(),
            potTransition.getPlayer().getId(), 
            (int) potTransition.getAmount());
        return potTransfer;
    }
}	
