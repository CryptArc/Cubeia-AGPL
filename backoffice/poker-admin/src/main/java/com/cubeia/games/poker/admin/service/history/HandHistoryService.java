package com.cubeia.games.poker.admin.service.history;

import com.cubeia.poker.handhistory.api.HistoricHand;

import java.util.Date;
import java.util.List;

public interface HandHistoryService {

    public List<HistoricHand> findHandHistory(int playerId, Date fromDate, Date toDate);

}
