package com.cubeia.games.poker.debugger;

import com.cubeia.firebase.api.action.GameAction;
import com.cubeia.firebase.api.action.TableChatAction;
import com.cubeia.firebase.api.action.service.ServiceAction;
import com.cubeia.firebase.api.service.ServiceRouter;
import com.cubeia.games.poker.debugger.cache.TableEventCache;
import com.cubeia.games.poker.debugger.guice.GuiceConfig;
import com.cubeia.games.poker.debugger.server.WebServer;
import com.cubeia.games.poker.services.HandDebuggerContract;
import com.google.inject.Inject;

public class HandDebuggerImpl implements HandDebuggerContract {
	
	@Inject GuiceConfig guice;
	
	@Inject WebServer server;
	
	@Inject TableEventCache<String> cache;

	private ServiceRouter router;
	
	public void start() {
		server.start();
	}

	@Override
	public void addPublicAction(int tableId, GameAction action) {
		cache.addPublicAction(tableId, action);
	}

	@Override
	public void addPrivateAction(int tableId, int playerId, GameAction action) {
		cache.addPrivateAction(tableId, playerId, action);
	}

	@Override
	public void clearTable(int tableId) {
		cache.clearTable(tableId);
	}

	@Override
	public void sendHttpLink(int tableId, int playerId) {
		if (router != null) {
			String url = "http://localhost:9091/table/html?tableId="+tableId;
			TableChatAction chat = new TableChatAction(playerId, tableId, 
					"You can check the hand history at: <a href='event:"+url+"' target='_blank'>"+url+"</a>");
	        router.dispatchToPlayer(playerId, chat);
		}
	}

	@Override
	public void setRouter(ServiceRouter router) {
		this.router = router;}

	@Override
	public void onAction(ServiceAction e) {}
}
