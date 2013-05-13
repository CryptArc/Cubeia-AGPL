package com.cubeia.game.poker.bot.ai;

import java.util.ArrayList;
import java.util.List;

import com.cubeia.firebase.bot.action.Action;
import com.cubeia.firebase.bot.ai.AbstractAI;
import com.cubeia.game.poker.bot.AiProvider;
import com.cubeia.game.poker.bot.PokerBot;
import com.cubeia.game.poker.bot.ai.impl.RandomAI;
import com.cubeia.games.poker.io.protocol.GameCard;
import com.cubeia.games.poker.io.protocol.PerformAction;
import com.cubeia.games.poker.io.protocol.RequestAction;
import com.cubeia.poker.hand.Card;
import com.cubeia.poker.hand.Hand;
import com.cubeia.poker.hand.HandStrength;
import com.cubeia.poker.variant.texasholdem.TexasHoldemHandCalculator;

public class PokerGameHandler {
	
	private GameState state = new GameState();
	
	private AbstractAI bot;
	
	private TexasHoldemHandCalculator handCalculator = new TexasHoldemHandCalculator();
	
	private PokerAI ai;
	
	public PokerGameHandler(AbstractAI bot) {
		this.bot = bot;
		
	}

	@SuppressWarnings("unchecked")
	private void initAi(AbstractAI bot) {
		AiProvider provider = (AiProvider)bot;
		String aiClass = provider.getPokerAi();
		try {
			bot.getBot().logInfo("AI CLASS: "+aiClass);
			Class<PokerAI> forName = (Class<PokerAI>)Class.forName(aiClass);
			ai = forName.newInstance();
			bot.getBot().logInfo("Using AI: "+ai.getClass().getSimpleName());
		} catch (Exception e) {
			bot.getBot().logWarn("Could not create AI class: "+aiClass+". Will use Random AI instead. Error: "+e);
			ai = new RandomAI();
		}
		
		ai.setBot(bot);
	}
	
	/*----------------------------------------------
	 * 
	 * GAME LOGIC METHODS
	 * 
	 *----------------------------------------------*/
	
	public Action onActionRequest(final RequestAction request) {
		if (ai == null) { 
			initAi(bot);
		}
		HandStrength handStrength = getHandStrength();
		bot.getBot().logInfo("I was requested to act. My best hand is: "+handStrength.getHandType());
		
		Action action = new Action(bot.getBot()) {
            public void run() {
                try {
                	PerformAction response = ai.onActionRequest(request, state);
                    // bot.getBot().logInfo("Request("+request+") -> Response("+response+")");
                   bot.getBot().sendGameData(bot.getTable().getId(), bot.getBot().getPid(), response);
                } catch (Throwable th) {
                    th.printStackTrace();
                }
            }
		 };
		 
		 return action;
	}
	
	
	
	/*----------------------------------------------
	 * 
	 * TABLE STATE METHODS
	 * 
	 *----------------------------------------------*/
	
	public void clear() {
		bot.getBot().logDebug("Hand End - Clear poker table state");
		state.clear();
	}

	public void addPrivateCard(GameCard card) {
		state.addPrivateCard(convertGameCard(card));
	}

	public void addCommunityCard(GameCard card) {
		state.addCommunityCard(convertGameCard(card));
	}
	
	private Card convertGameCard(GameCard c) {
        return new Card(c.cardId, com.cubeia.poker.hand.Rank.values()[c.rank.ordinal()], com.cubeia.poker.hand.Suit.values()[c.suit.ordinal()]);
    }
	
	public HandStrength getHandStrength() {
		List<Card> cards = new ArrayList<Card>(state.getPrivateCards());
		cards.addAll(state.getCommunityCards());
		Hand hand = new Hand(cards);
		return handCalculator.getHandStrength(hand);
	}

	
}
