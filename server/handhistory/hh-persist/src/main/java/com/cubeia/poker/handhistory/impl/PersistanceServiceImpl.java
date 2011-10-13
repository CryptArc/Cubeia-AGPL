package com.cubeia.poker.handhistory.impl;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;

import com.cubeia.firebase.guice.inject.Log4j;
import com.cubeia.games.poker.handhistory.HandHistoryPersistenceService;
import com.cubeia.poker.handhistory.api.DeckInfo;
import com.cubeia.poker.handhistory.api.HandHistoryEvent;
import com.cubeia.poker.handhistory.api.HistoricHand;
import com.cubeia.poker.handhistory.api.Player;
import com.cubeia.poker.handhistory.api.Results;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

// TODO Persist on each event to support fail-over
// TODO Real hand from database to support fail-over
public class PersistanceServiceImpl implements HandHistoryPersistenceService {

	@Log4j
	private Logger log;
	
	private Map<Integer, HistoricHand> cache = new ConcurrentHashMap<Integer, HistoricHand>();
	
	@Override
	public void startHand(int tableId, String handId, List<Player> seats) {
		log.debug("Start hand on table: " + tableId);
		if(cache.containsKey(tableId)) {
			log.warn("Starting new hand, but cache is not empty, for table: " + tableId);
		}
		HistoricHand hand = new HistoricHand(tableId, handId);
		hand.setStartTime(new DateTime().getMillis());
		hand.getSeats().addAll(seats);
		cache.put(tableId, hand);
	}

	@Override
	public void reportEvent(int tableId, HandHistoryEvent event) {
		log.debug("Event on table " + tableId + ": " + event.getType());
		HistoricHand hand = getCurrent(tableId);
		hand.getEvents().add(event);
	}

	@Override
	public void reportDeckInfo(int tableId, DeckInfo deckInfo) {
		log.debug("Deck info on table " + tableId + ": " + deckInfo);
		HistoricHand hand = getCurrent(tableId);
		hand.setDeckInfo(deckInfo);
	}

	@Override
	public void reportResults(int tableId, Results res) {
		log.debug("Result on table " + tableId + " resported");
		HistoricHand hand = getCurrent(tableId);
		hand.setResults(res);
	}

	@Override
	public void stopHand(int tableId) {
		// TODO Persist hand here...
		HistoricHand hand = getCurrent(tableId);
		hand.setEndTime(new DateTime().getMillis());
		
		GsonBuilder b = new GsonBuilder();
		b.registerTypeAdapter(HandHistoryEvent.class, new EventSerializer());
		b.setPrettyPrinting();
		log.info("Hand history: " + b.create().toJson(hand));
		
		cache.remove(tableId);
	}

	@Override
	public void cancelHand(int tableId) {
		// TODO Report this?
		log.debug("Hand cancelled on table: " + tableId);
		cache.remove(tableId);
	}


	// --- PRIVATE METHODS --- //

	private HistoricHand getCurrent(int tableId) {
		if(!cache.containsKey(tableId)) {
			// TODO Read from database...
			throw new RuntimeException("Current hand for table " + tableId + " not found!");
		} else {
			return cache.get(tableId);
		}
	}
	
	// --- PRIVATE CLASSES --- //
	
	private static class EventSerializer implements JsonSerializer<HandHistoryEvent> {
		
		@Override
		public JsonElement serialize(HandHistoryEvent src, Type typeOfSrc, JsonSerializationContext context) {
			Class<? extends HandHistoryEvent> cl = src.getClass();
			return context.serialize(src, cl);
		}
	}
}
