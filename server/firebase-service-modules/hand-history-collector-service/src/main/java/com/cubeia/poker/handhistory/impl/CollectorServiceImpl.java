package com.cubeia.poker.handhistory.impl;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.cubeia.poker.handhistory.api.HandHistoryCollectorService;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;

import com.cubeia.firebase.api.service.ServiceContext;
import com.cubeia.firebase.guice.inject.Log4j;
import com.cubeia.poker.handhistory.api.DeckInfo;
import com.cubeia.poker.handhistory.api.HandHistoryEvent;
import com.cubeia.poker.handhistory.api.HandHistoryPersistenceService;
import com.cubeia.poker.handhistory.api.HandHistoryPersister;
import com.cubeia.poker.handhistory.api.HandIdentification;
import com.cubeia.poker.handhistory.api.HistoricHand;
import com.cubeia.poker.handhistory.api.Player;
import com.cubeia.poker.handhistory.api.Results;
import com.google.inject.Inject;

/**
 * This is the collector implementation. It caches hands in a map
 * and will use an optional hand history persister service to
 * persist the result when the hand is ended. If no persister service
 * is deployed it will write the hand to the logs in JSON format on
 * DEBUG level.
 * 
 * @author Lars J. Nilsson
 */

// TODO Reap dead hands?
// TODO Persist on each event to support fail-over?
// TODO Read hand from database (if not found) to support fail-over

public class CollectorServiceImpl implements HandHistoryCollectorService {

	@Log4j
	private Logger log;
	
	private Map<Integer, HistoricHand> cache = new ConcurrentHashMap<Integer, HistoricHand>();
	
	@Inject
	private ServiceContext context;
	
	@Inject
	private JsonHandHistoryLogger jsonPersist;
	
	@Override
	public void startHand(HandIdentification id, List<Player> seats) {
		log.debug("Start hand on table: " + id.getTableId());
		if(cache.containsKey(id.getTableId())) {
			log.warn("Starting new hand, but cache is not empty, for table: " + id.getTableId());
		}
		HistoricHand hand = new HistoricHand(id);
		hand.setStartTime(new DateTime().getMillis());
		hand.getSeats().addAll(seats);
		cache.put(id.getTableId(), hand);
	}

	@Override
	public void reportEvent(int tableId, HandHistoryEvent event) {
		HistoricHand hand = getCurrent(tableId);
		hand.getEvents().add(event);
	}

	@Override
	public void reportDeckInfo(int tableId, DeckInfo deckInfo) {
		HistoricHand hand = getCurrent(tableId);
		hand.setDeckInfo(deckInfo);
	}

	@Override
	public void reportResults(int tableId, Results res) {
		HistoricHand hand = getCurrent(tableId);
		hand.setResults(res);
	}

	@Override
	public void stopHand(int tableId) {
		HistoricHand hand = getCurrent(tableId);
		hand.setEndTime(new DateTime().getMillis());
		getPersister().persist(hand);
		cache.remove(tableId);
	}

	@Override
	public void cancelHand(int tableId) {
		// TODO Report this?
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
	
	private HandHistoryPersister getPersister() {
		HandHistoryPersistenceService service = context.getParentRegistry().getServiceInstance(HandHistoryPersistenceService.class);
		if(service != null) {
			return service;
		} else {
			return jsonPersist;
		}
	}
}
