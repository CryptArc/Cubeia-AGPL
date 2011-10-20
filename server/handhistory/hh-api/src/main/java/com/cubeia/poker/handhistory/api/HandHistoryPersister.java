package com.cubeia.poker.handhistory.api;

public interface HandHistoryPersister {
	
	public HistoricHand retreive(HandIdentification id);

	public void persist(HistoricHand hand);
	
}
