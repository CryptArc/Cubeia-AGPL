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

package com.cubeia.games.poker.handler;

import static com.cubeia.games.poker.io.protocol.Enums.ActionType.DISCARD;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import com.cubeia.firebase.api.action.GameDataAction;
import com.cubeia.games.poker.io.protocol.BestHand;
import com.cubeia.games.poker.io.protocol.CardToDeal;
import com.cubeia.games.poker.io.protocol.DealPrivateCards;
import com.cubeia.games.poker.io.protocol.DealPublicCards;
import com.cubeia.games.poker.io.protocol.Enums;
import com.cubeia.games.poker.io.protocol.Enums.ActionType;
import com.cubeia.games.poker.io.protocol.Enums.PotType;
import com.cubeia.games.poker.io.protocol.Enums.Rank;
import com.cubeia.games.poker.io.protocol.Enums.Suit;
import com.cubeia.games.poker.io.protocol.ExposePrivateCards;
import com.cubeia.games.poker.io.protocol.GameCard;
import com.cubeia.games.poker.io.protocol.HandEnd;
import com.cubeia.games.poker.io.protocol.PerformAction;
import com.cubeia.games.poker.io.protocol.PlayerAction;
import com.cubeia.games.poker.io.protocol.PlayerBalance;
import com.cubeia.games.poker.io.protocol.Pot;
import com.cubeia.games.poker.io.protocol.PotTransfer;
import com.cubeia.games.poker.io.protocol.PotTransfers;
import com.cubeia.games.poker.io.protocol.RequestAction;
import com.cubeia.games.poker.util.ProtocolFactory;
import com.cubeia.poker.action.ActionRequest;
import com.cubeia.poker.action.DiscardAction;
import com.cubeia.poker.action.DiscardRequest;
import com.cubeia.poker.action.PokerAction;
import com.cubeia.poker.action.PokerActionType;
import com.cubeia.poker.action.PossibleAction;
import com.cubeia.poker.hand.Card;
import com.cubeia.poker.hand.ExposeCardsHolder;
import com.cubeia.poker.hand.ExposedCards;
import com.cubeia.poker.hand.HandType;
import com.cubeia.poker.model.RatedPlayerHand;
import com.cubeia.poker.player.PokerPlayer;
import com.cubeia.poker.pot.PotTransition;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Range;
import com.google.common.primitives.Ints;

/**
 * Translates poker-logic internal actions to the styx wire-protocol
 * as defined in poker-protocol.
 *
 * @author Fredrik Johansson, Cubeia Ltd
 */
public class ActionTransformer {

    public RequestAction transform(ActionRequest request, int sequenceNumber) {
        RequestAction packet = new RequestAction();
        packet.timeToAct = (int) request.getTimeToAct();
        packet.player = request.getPlayerId();
        packet.seq = sequenceNumber;
        packet.currentPotSize = (int) request.getTotalPotSize();

        List<PlayerAction> allowed = new LinkedList<PlayerAction>();
        for (PossibleAction option : request.getOptions()) {
            if (option instanceof DiscardRequest) {
                DiscardRequest discardRequest = (DiscardRequest) option;
                PlayerAction playerOption = createPlayerAction(PokerActionType.DISCARD);
                Range<Integer> cardsToDiscard = discardRequest.getCardsToDiscard();
                // TODO: Consider if this is an ugly hack or if it's OK. (Using minAmount and maxAmount to describe the range of number of cards to discard)
                playerOption.minAmount = cardsToDiscard.lowerEndpoint();
                playerOption.maxAmount =  cardsToDiscard.upperEndpoint();
                allowed.add(playerOption);
            } else {
                PlayerAction playerOption = createPlayerAction(option.getActionType());
                // FIXME: Casting to integer here since Flash does not support long values!
                playerOption.minAmount = (int) option.getMinAmount();
                playerOption.maxAmount = (int) option.getMaxAmount();
                allowed.add(playerOption);
            }
        }
        packet.allowedActions = allowed;

        return packet;
    }

    public PerformAction transform(PokerAction pokerAction, PokerPlayer pokerPlayer) {
        PerformAction packet = new PerformAction();
        packet.action = createPlayerAction(pokerAction.getActionType());
        // FIXME: Flash does not support longs...
        packet.betAmount = (int) pokerAction.getBetAmount();
        packet.raiseAmount = (int) pokerAction.getRaiseAmount();
        packet.stackAmount = (int) pokerPlayer.getBetStack();
        packet.player = pokerAction.getPlayerId();
        packet.timeout = pokerAction.isTimeout();
        return packet;
    }

    public PokerActionType transform(ActionType actionType) {
        return PokerActionType.valueOf(actionType.name());
    }

    public PokerAction transform(int playerId, PerformAction packet) {
        ActionType actionType = packet.action.type;
        if (actionType == DISCARD) {
            return convertDiscardAction(playerId, packet);
        } else {
            PokerAction converted = new PokerAction(playerId, transform(actionType));
            converted.setBetAmount(packet.betAmount);
            return converted;
        }
    }

    private PokerAction convertDiscardAction(int playerId, PerformAction action) {
        return new DiscardAction(playerId, Ints.asList(action.cardsToDiscard));
    }

    @VisibleForTesting
    protected PlayerAction createPlayerAction(PokerActionType actionType) {
        PlayerAction action = new PlayerAction();
        action.type = fromPokerActionTypeToProtocolActionType(actionType);

        return action;
    }

    public ActionType fromPokerActionTypeToProtocolActionType(PokerActionType actionType) {
        return ActionType.valueOf(actionType.name());
    }

    /**
     * @param playerId, the player receiving the cards
     * @param cards,    the cards to be dealt
     * @param hidden,   true if the suit and rank should be of type HIDDEN only
     */
    public DealPrivateCards createPrivateCardsPacket(int playerId, List<Card> cards, boolean hidden) {
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

    public Rank convertRankToProtocolEnum(com.cubeia.poker.hand.Rank rank) {
        return Enums.Rank.values()[rank.ordinal()];
    }

    public Suit convertSuitToProtocolEnum(com.cubeia.poker.hand.Suit suit) {
        return Enums.Suit.values()[suit.ordinal()];
    }

    public Enums.HandType convertHandTypeToEnum(HandType handType) {
        return Enums.HandType.values()[handType.ordinal()];
    }

    public DealPublicCards createPublicCardsPacket(List<Card> cards) {
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

    public Card convertGameCard(GameCard c) {
        return new Card(c.cardId, com.cubeia.poker.hand.Rank.values()[c.rank.ordinal()], com.cubeia.poker.hand.Suit.values()[c.suit.ordinal()]);
    }

    public ExposePrivateCards createExposeCardsPacket(ExposeCardsHolder holder) {
        ExposePrivateCards packet = new ExposePrivateCards();
        packet.cards = new LinkedList<CardToDeal>();
        for (ExposedCards exposedCards : holder.getExposedCards()) {
            Collection<Card> cards = exposedCards.getCards();
            for (Card card : cards) {
                GameCard gCard = new GameCard();
                gCard.rank = Enums.Rank.values()[card.getRank().ordinal()];
                gCard.suit = Enums.Suit.values()[card.getSuit().ordinal()];
                gCard.cardId = card.getId();

                CardToDeal deal = new CardToDeal(exposedCards.getPlayerId(), gCard);
                packet.cards.add(deal);
            }
        }
        return packet;
    }

    public BestHand createBestHandPacket(int playerId, HandType handType, List<Card> cardsInHand) {
        List<GameCard> gameCards = convertCards(cardsInHand);

        return new BestHand(playerId, convertHandTypeToEnum(handType), gameCards);
    }

    @VisibleForTesting
    protected List<GameCard> convertCards(List<Card> cardsInHand) {
        List<GameCard> gameCards = new ArrayList<GameCard>();
        for (Card c : cardsInHand) {
            gameCards.add(new GameCard(c.getId(), convertSuitToProtocolEnum(c.getSuit()), convertRankToProtocolEnum(c.getRank())));
        }
        return gameCards;
    }

    public HandEnd createHandEndPacket(Collection<RatedPlayerHand> hands, PotTransfers potTransfers, List<Integer> playerIdRevealOrder) {
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

            BestHand best = new BestHand(ratedHand.getPlayerId(), convertHandTypeToEnum(ratedHand.getHandInfo().getHandType()), cards);

            packet.hands.add(best);
        }

        packet.potTransfers = potTransfers;

        int[] playerIdRevealOrderArray = new int[playerIdRevealOrder.size()];
        int index = 0;
        for (Integer playerId : playerIdRevealOrder) {
            playerIdRevealOrderArray[index++] = playerId;
        }
        packet.playerIdRevealOrder = playerIdRevealOrderArray;

        return packet;
    }

    public Pot createPotUpdatePacket(int id, long amount) {
        Pot packet = new Pot();
        packet.amount = (int) amount;
        packet.id = (byte) id;
        packet.type = id == 0 ? PotType.MAIN : PotType.SIDE;
        return packet;
    }


    public GameDataAction createPlayerBalanceAction(int balance, int pendingBalance, int playersContributionToPot, int playerId, int tableId) {
        PlayerBalance packet = new PlayerBalance(balance, pendingBalance, playerId, playersContributionToPot);
        return new ProtocolFactory().createGameAction(packet, playerId, tableId);
    }

    public PotTransfer createPotTransferPacket(PotTransition potTransition) {
        return new PotTransfer(
                (byte) potTransition.getPot().getId(),
                potTransition.getPlayer().getId(),
                (int) potTransition.getAmount());
    }
}	
