package com.cubeia.games.poker.admin.service.history;

import com.cubeia.poker.handhistory.api.HistoricHand;

import java.util.Collection;
import java.util.List;

public interface HandHistoryService {

    public List<HistoricHand> findHandHistoryByPlayerId(int playerId);

}
