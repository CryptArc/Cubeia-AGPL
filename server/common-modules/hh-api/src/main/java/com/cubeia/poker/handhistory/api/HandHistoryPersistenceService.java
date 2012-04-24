package com.cubeia.poker.handhistory.api;

import com.cubeia.firebase.api.service.Contract;

/**
 * A Firebase service contract for a hand history persister. This should be 
 * implemented and deployed by integration layers. 
 * 
 * @author Lars J. Nilsson
 */
public interface HandHistoryPersistenceService extends Contract, HandHistoryPersister { }
