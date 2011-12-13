package com.cubeia.game.poker.bot;

import org.apache.log4j.Logger;

import com.cubeia.firebase.api.util.ParameterUtil;
import com.cubeia.firebase.bot.BotGroupConfig;
import com.cubeia.firebase.bot.model.Table;

public class DiceArenaGroupConfig implements BotGroupConfig {

	private static transient Logger log = Logger.getLogger(DiceArenaGroupConfig.class);
	
	public String createBotScreenName(int id) {
		return "Bot_" + id;
	}
	
	public String createLobbyBotScreenName(int id) {
		return createBotScreenName(id);
	}
	
	public boolean isTableJoinable(Table table) {
		for (String key : table.getAttributes().keySet()) {
			if(key.equals("VISIBLE_IN_LOBBY")) {
				int visible = ParameterUtil.convertAsInt(table.getAttributes().get(key)).getValue().intValue();
				return (visible == 1);
			}
		}
		log.debug("Not joinable table: "+table);
		return false;
	}
}
