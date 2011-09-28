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

public interface CashGamesBackend {

	void announceTable(AnnounceTableRequest request, AnnounceTableCallback callback);

	void closeTable(CloseTableRequest request);

	void openSession(OpenSessionRequest request, OpenSessionCallback callback);

	void closeSession(CloseSessionRequest request);

	void reserve(ReserveRequest request, ReserveCallback callback);

	BatchHandResponse batchHand(BatchHandRequest request);

	long getMainAccountBalance(int playerId);
}
