package com.cubeia.game.poker.bot.ai.impl;

import java.math.BigDecimal;

import com.cubeia.firebase.bot.ai.AbstractAI;
import com.cubeia.game.poker.bot.ai.GameState;
import com.cubeia.game.poker.bot.ai.PokerAI;
import com.cubeia.games.poker.io.protocol.Enums.ActionType;
import com.cubeia.games.poker.io.protocol.PerformAction;
import com.cubeia.games.poker.io.protocol.PlayerAction;
import com.cubeia.games.poker.io.protocol.RequestAction;

public class FoldAI implements PokerAI {

	private AbstractAI bot;
	
	public FoldAI() {}
	
	@Override
	public void setBot(AbstractAI bot) {
		this.bot = bot;
	}
	
	@Override
	public PerformAction onActionRequest(RequestAction request, GameState state) {
        PerformAction response = new PerformAction();
        response.seq = request.seq;
        response.player = bot.getBot().getPid();

        PlayerAction playerAction = new PlayerAction(ActionType.FOLD, "0", "0");
        
        // Always post blinds
        for (PlayerAction action : request.allowedActions) {
            switch (action.type) {
                case BIG_BLIND:
                case SMALL_BLIND:
                case ANTE:
                	playerAction = action;
                	break;
                
                default:
                	break;
            }
        }
        
        
        response.action = playerAction;
        BigDecimal betAmount = BigDecimal.ZERO;
        response.betAmount =  betAmount.toPlainString();
        
        return response;
	}

}
