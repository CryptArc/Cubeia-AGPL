package com.cubeia.backend.cashgame;

import com.cubeia.backend.cashgame.callback.AnnounceTableCallback;
import com.cubeia.backend.cashgame.callback.OpenSessionCallback;
import com.cubeia.backend.cashgame.callback.ReserveCallback;
import com.cubeia.backend.cashgame.dto.AnnounceTableRequest;
import com.cubeia.backend.cashgame.dto.BatchHandRequest;
import com.cubeia.backend.cashgame.dto.BatchHandResponse;
import com.cubeia.backend.cashgame.dto.CloseSessionRequest;
import com.cubeia.backend.cashgame.dto.CloseTableRequest;
import com.cubeia.backend.cashgame.dto.OpenSessionRequest;
import com.cubeia.backend.cashgame.dto.ReserveRequest;
import com.cubeia.backend.cashgame.exceptions.BatchHandFailedException;
import com.cubeia.backend.cashgame.exceptions.CloseSessionFailedException;
import com.cubeia.backend.cashgame.exceptions.CloseTableFailedException;
import com.cubeia.backend.cashgame.exceptions.GetBalanceFailedException;

public interface CashGamesBackend {

    /**
     * Async call to announce table. 
     * The table will recieve a 
     * @param request
     * @param callback
     */
	void announceTable(AnnounceTableRequest request, AnnounceTableCallback callback);

	void closeTable(CloseTableRequest request) throws CloseTableFailedException;

	void openSession(OpenSessionRequest request, OpenSessionCallback callback);

	void closeSession(CloseSessionRequest request) throws CloseSessionFailedException;

	void reserve(ReserveRequest request, ReserveCallback callback);

	BatchHandResponse batchHand(BatchHandRequest request) throws BatchHandFailedException;

	long getMainAccountBalance(int playerId) throws GetBalanceFailedException;
}
