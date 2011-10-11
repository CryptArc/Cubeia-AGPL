package com.cubeia.games.poker.debugger.cache;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import se.jadestone.dicearena.game.poker.network.protocol.Enums.PlayerTableStatus;
import se.jadestone.dicearena.game.poker.network.protocol.PlayerPokerStatus;
import se.jadestone.dicearena.game.poker.network.protocol.ProtocolObjectFactory;

import com.cubeia.firebase.api.action.GameAction;
import com.cubeia.firebase.api.action.GameDataAction;
import com.cubeia.firebase.io.ProtocolObject;
import com.cubeia.firebase.io.StyxSerializer;
import com.cubeia.games.poker.services.HandDebuggerContract;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * This class will leak memory if used in production. Previous events are never
 * cleared from lists so if many tables are created (over time) then this map
 * will grow unbounded.
 *
 * @author Fredrik Johansson, Cubeia Ltd
 */
@Singleton
public class TableStringCache implements TableEventCache<String> {

	StyxSerializer serializer = new StyxSerializer(new ProtocolObjectFactory());
	
	ConcurrentMap<Integer, List<String>> events = new ConcurrentHashMap<Integer, List<String>>();
	
	/** Caches last hand */
	ConcurrentMap<Integer, List<String>> previousEvents = new ConcurrentHashMap<Integer, List<String>>();
	
	SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
	
	@Inject HandDebuggerContract handDebugger;
	
	@Override
	public void addPublicAction(int tableId, GameAction action) {
		addToCache(tableId, action);
	}

	@Override
	public void addPrivateAction(int tableId, int playerId, GameAction action) {
		addToCache(tableId, action);
	}

	@Override
	public void clearTable(int tableId) {
		List<String> removed = events.remove(tableId);
		previousEvents.put(tableId, removed);
	}

	@Override
	public List<String> getEvents(int tableId) {
		return events.get(tableId);
	}
	
	@Override
	public List<String> getPreviousEvents(int tableId) {
		return previousEvents.get(tableId);
	}
	
	private void addToCache(int tableId, GameAction action) {
		if (action instanceof GameDataAction) {
			GameDataAction gameDataAction = (GameDataAction) action;
			addGameDataAction(tableId, gameDataAction);
		}
	}

	private void addGameDataAction(int tableId, GameDataAction action) {
		ProtocolObject protocol = unpack(action);
		events.putIfAbsent(tableId, new ArrayList<String>());
		events.get(tableId).add(sdf.format(new Date()) +" - "+ protocol.toString());
		
		checkJoinedPlayer(action.getPlayerId(), action.getTableId(), protocol);
	}
	
	private void checkJoinedPlayer(int playerId, int tableId, ProtocolObject protocol) {
		if (protocol instanceof PlayerPokerStatus) {
			PlayerPokerStatus playerStatus = (PlayerPokerStatus) protocol;
			if (playerStatus.status.equals(PlayerTableStatus.NORMAL)) {
				// New/Reconnected player - send HTTP link
				handDebugger.sendHttpLink(tableId, playerId);
			}
		}
	}

	private ProtocolObject unpack(GameDataAction action) {
		try {
			return serializer.unpack(action.getData());
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

}
