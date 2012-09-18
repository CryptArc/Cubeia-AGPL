package com.cubeia.games.poker.admin.service.history;

import com.cubeia.poker.handhistory.api.HistoricHand;

import java.util.Date;
import java.util.List;

public interface HandHistoryService {

    public List<HistoricHand> findHandHistory(Integer playerId, Date fromDate, Date toDate);

    HistoricHand findById(String handId);
}
