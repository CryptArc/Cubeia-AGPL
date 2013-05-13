package com.cubeia.game.poker.bot.ai.impl;

import static com.cubeia.games.poker.io.protocol.Enums.ActionType.BET;
import static com.cubeia.games.poker.io.protocol.Enums.ActionType.CALL;
import static com.cubeia.games.poker.io.protocol.Enums.ActionType.CHECK;
import static com.cubeia.games.poker.io.protocol.Enums.ActionType.FOLD;
import static com.cubeia.games.poker.io.protocol.Enums.ActionType.RAISE;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.cubeia.firebase.bot.ai.AbstractAI;
import com.cubeia.game.poker.bot.ai.GameState;
import com.cubeia.game.poker.bot.ai.PokerAI;
import com.cubeia.games.poker.io.protocol.Enums.ActionType;
import com.cubeia.games.poker.io.protocol.PerformAction;
import com.cubeia.games.poker.io.protocol.PlayerAction;
import com.cubeia.games.poker.io.protocol.RequestAction;
import com.cubeia.poker.hand.Card;
import com.cubeia.poker.hand.Hand;
import com.cubeia.poker.hand.HandStrength;
import com.cubeia.poker.variant.texasholdem.TexasHoldemHandCalculator;

public class SimpleAI implements PokerAI {

	private AbstractAI bot;

	private static Random rng = new Random();

	private TexasHoldemHandCalculator handCalculator = new TexasHoldemHandCalculator();

	private SimpleAiCalculator aiCalculator = new SimpleAiCalculator();
	
	enum Strategy {
		WEAK,
		NEUTRAL,
		STRONG
	}

	public SimpleAI() {}

	@Override
	public void setBot(AbstractAI bot) {
		this.bot = bot;
	}

	@Override
	public PerformAction onActionRequest(RequestAction request, GameState state) {
		HandStrength handStrength = getHandStrength(state);

		bot.getBot().logInfo("Simple AI. Hand "+handStrength.getHandType()+", Phase: "+state.getPhase()+", Pot Size: "+request.currentPotSize);

		PerformAction response = new PerformAction();
		response.seq = request.seq;
		response.player = bot.getBot().getPid();

		PlayerAction playerAction = null;

		// Always post blinds
		for (PlayerAction action : request.allowedActions) {
			switch (action.type) {
			case BIG_BLIND:
			case SMALL_BLIND:
			case ANTE:
				playerAction = action;
				break;
			default:
			}
		}

		if (playerAction != null) {
			// Blind or Ante
			response.action = playerAction;
			return response;
		}

		// We need to act
		Strategy strategy = aiCalculator.getStrategy(request, state, handStrength);
		bot.getBot().logInfo("I got "+handStrength.getHandType().name()+" on "+state.getPhase()+" so I am feeling "+strategy);
		
		BigDecimal betAmount = BigDecimal.ZERO;
		
		if (strategy == Strategy.WEAK) {
			if (hasPlayerAction(CHECK, request)) {
				playerAction = new PlayerAction(CHECK, "0", "0");
			} else {
				playerAction = new PlayerAction(FOLD, "0", "0");
			}
		}
		
		if (strategy == Strategy.NEUTRAL) {
			if (hasPlayerAction(CALL, request)) {
				playerAction = getPlayerAction(CALL, request);
				betAmount = new BigDecimal(playerAction.minAmount);
				
			} else if (hasPlayerAction(BET, request)) {
				playerAction = getPlayerAction(BET, request);
				betAmount = new BigDecimal(playerAction.minAmount);
				
			} else if (hasPlayerAction(CHECK, request)) {
				playerAction = getPlayerAction(CHECK, request);
				
			} else {
				playerAction = getPlayerAction(FOLD, request);
				
			}
		}
		
		if (strategy == Strategy.STRONG) {
			if (hasPlayerAction(RAISE, request)) {
				playerAction = getPlayerAction(RAISE, request);
				betAmount = new BigDecimal(playerAction.minAmount);
				
			} else if (hasPlayerAction(BET, request)) {
				playerAction = getPlayerAction(BET, request);
				betAmount = new BigDecimal(playerAction.minAmount);
				
			} else if (hasPlayerAction(CALL, request)) {
				playerAction = getPlayerAction(CALL, request);
				betAmount = new BigDecimal(playerAction.minAmount);
				
			} else if (hasPlayerAction(CHECK, request)) {
				playerAction = getPlayerAction(CHECK, request);
				
			} else {
				playerAction = getPlayerAction(FOLD, request);
				
			}
		}

        response.action = playerAction;
        response.betAmount =  betAmount.toPlainString();
        
        bot.getBot().logInfo("    Action: "+playerAction.type+", Bet: "+betAmount+" (TODO: always min bet now)");
        
		return response;
	}

	
	private PlayerAction getPlayerAction(ActionType type, RequestAction request) {
		for (PlayerAction action : request.allowedActions) {
			if (action.type == type) {
				return action;
			}
		}
		return null;
	}
	
	private boolean hasPlayerAction(ActionType type, RequestAction request) {
		return getPlayerAction(type, request) != null;
	}
	
	public HandStrength getHandStrength(GameState state) {
		List<Card> cards = new ArrayList<Card>(state.getPrivateCards());
		cards.addAll(state.getCommunityCards());
		Hand hand = new Hand(cards);
		return handCalculator.getHandStrength(hand);
	}
}
