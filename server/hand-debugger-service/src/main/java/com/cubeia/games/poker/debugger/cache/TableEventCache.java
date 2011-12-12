package com.cubeia.games.poker.debugger.cache;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import se.jadestone.dicearena.game.poker.network.protocol.Enums.PlayerTableStatus;
import se.jadestone.dicearena.game.poker.network.protocol.PerformAction;
import se.jadestone.dicearena.game.poker.network.protocol.PlayerBalance;
import se.jadestone.dicearena.game.poker.network.protocol.PlayerPokerStatus;
import se.jadestone.dicearena.game.poker.network.protocol.ProtocolObjectFactory;
import se.jadestone.dicearena.game.poker.network.protocol.RequestAction;

import com.cubeia.firebase.api.action.GameAction;
import com.cubeia.firebase.api.action.GameDataAction;
import com.cubeia.firebase.io.ProtocolObject;
import com.cubeia.firebase.io.StyxSerializer;
import com.cubeia.games.poker.debugger.json.EventType;
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
public class TableEventCache {

	StyxSerializer serializer = new StyxSerializer(new ProtocolObjectFactory());
	
	ConcurrentMap<Integer, List<Event>> events = new ConcurrentHashMap<Integer, List<Event>>();
	
	/** Caches last hand */
	ConcurrentMap<Integer, List<Event>> previousEvents = new ConcurrentHashMap<Integer, List<Event>>();
	
	SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
	
	@Inject HandDebuggerContract handDebugger;
	
	public void addPublicAction(int tableId, GameAction action) {
		addToCache(tableId, action, false);
	}

	public void addPrivateAction(int tableId, int playerId, GameAction action) {
		addToCache(tableId, action, true);
	}

	public void clearTable(int tableId) {
		List<Event> removed = events.remove(tableId);
		previousEvents.put(tableId, removed);
	}

	public List<Event> getEvents(int tableId) {
		return events.get(tableId);
	}
	
	public List<Event> getPreviousEvents(int tableId) {
		return previousEvents.get(tableId);
	}
	
	private void addToCache(int tableId, GameAction action, boolean privateAction) {
		if (action instanceof GameDataAction) {
			GameDataAction gameDataAction = (GameDataAction) action;
			addGameDataAction(tableId, gameDataAction, privateAction);
		}
	}

	private void addGameDataAction(int tableId, GameDataAction action, boolean privateAction) {
		ProtocolObject protocol = unpack(action);
		events.putIfAbsent(tableId, new ArrayList<Event>());
		
		EventType type = getEventType(protocol);
		if (privateAction) {
			type = EventType.hidden;
		}
		Event event = new Event(type, protocol.toString(), sdf.format(new Date()));
		events.get(tableId).add(event);
		
		checkJoinedPlayer(action.getPlayerId(), action.getTableId(), protocol);
	}
	
	

	private void checkJoinedPlayer(int playerId, int tableId, ProtocolObject protocol) {
		if (protocol instanceof PlayerPokerStatus) {
			PlayerPokerStatus playerStatus = (PlayerPokerStatus) protocol;
			if (playerStatus.status.equals(PlayerTableStatus.SITIN)) {
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
	
	private EventType getEventType(ProtocolObject protocol) {
		if (protocol instanceof RequestAction) {
			return EventType.request;
		} if (protocol instanceof PerformAction) {
			return EventType.action;
		} if (protocol instanceof PlayerPokerStatus) {
			return EventType.status;
		} if (protocol instanceof PlayerBalance) {
			return EventType.status;
		} else {
			return EventType.table;
		}
	}

}
