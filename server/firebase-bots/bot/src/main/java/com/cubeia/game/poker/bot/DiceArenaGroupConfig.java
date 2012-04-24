package com.cubeia.game.poker.bot;

import org.apache.log4j.Logger;

import com.cubeia.firebase.api.util.ParameterUtil;
import com.cubeia.firebase.bot.BotGroupConfig;
import com.cubeia.firebase.bot.model.Table;

public class DiceArenaGroupConfig implements BotGroupConfig {

	private static final int DA_MIN_LENGTH = 6;
	private static final int DA_MAX_LENGTH = 12;
	
	private static transient Logger log = Logger.getLogger(DiceArenaGroupConfig.class);
	
	public String createBotScreenName(int id) {
		String s = "Bot_" + id;
		// Must be 6 characters...
		while(s.length() < DA_MIN_LENGTH) {
			s += "_";
		}
		// Must be less than 12
		while(s.length() > DA_MAX_LENGTH) {
			s = s.substring(1);
		}
		return s;
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
