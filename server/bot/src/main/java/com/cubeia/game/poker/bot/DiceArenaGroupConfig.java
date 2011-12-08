package com.cubeia.game.poker.bot;

import com.cubeia.firebase.api.util.ParameterUtil;
import com.cubeia.firebase.bot.BotGroupConfig;
import com.cubeia.firebase.bot.model.Table;

public class DiceArenaGroupConfig implements BotGroupConfig {

	public String createBotScreenName(int id) {
		return "Bot_" + id;
	}
	
	public String createLobbyBotScreenName(int id) {
		return createBotScreenName(id);
	}
	
	public boolean isTableJoinable(Table table) {
		for (String key : table.getAttributes().keySet()) {
			if(key.equals("VISIBLE_IN_LOBBY")) {
				return (ParameterUtil.convertAsInt(table.getAttributes().get(key)).getValue().intValue() == 1);
			}
		}
		return false;
	}
}
