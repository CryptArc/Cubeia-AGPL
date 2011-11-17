package com.cubeia.backend.cashgame;

import com.cubeia.backend.cashgame.dto.AnnounceTableRequest;
import com.cubeia.backend.cashgame.dto.AnnounceTableResponse;
import com.cubeia.backend.cashgame.dto.BalanceUpdate;
import com.cubeia.backend.cashgame.dto.BatchHandRequest;
import com.cubeia.backend.cashgame.dto.BatchHandResponse;
import com.cubeia.backend.cashgame.dto.CloseSessionRequest;
import com.cubeia.backend.cashgame.dto.OpenSessionRequest;
import com.cubeia.backend.cashgame.dto.OpenSessionResponse;
import com.cubeia.backend.cashgame.dto.ReserveRequest;
import com.cubeia.backend.cashgame.dto.ReserveResponse;
import com.cubeia.backend.cashgame.exceptions.AnnounceTableFailedException;
import com.cubeia.backend.cashgame.exceptions.BatchHandFailedException;
import com.cubeia.backend.cashgame.exceptions.CloseSessionFailedException;
import com.cubeia.backend.cashgame.exceptions.GetBalanceFailedException;
import com.cubeia.backend.cashgame.exceptions.OpenSessionFailedException;
import com.cubeia.backend.cashgame.exceptions.ReserveFailedException;

public interface SynchronousCashGamesBackend {
	
	String generateHandId();
	
	AnnounceTableResponse announceTable(AnnounceTableRequest request) throws AnnounceTableFailedException;

	// void closeTable(CloseTableRequest request) throws CloseTableFailedException;

	OpenSessionResponse openSession(OpenSessionRequest request) throws OpenSessionFailedException;

	void closeSession(CloseSessionRequest request) throws CloseSessionFailedException;

	ReserveResponse reserve(ReserveRequest request) throws ReserveFailedException;

	BatchHandResponse batchHand(BatchHandRequest request) throws BatchHandFailedException;

	long getMainAccountBalance(int playerId) throws GetBalanceFailedException;

	BalanceUpdate getSessionBalance(PlayerSessionId sessionId) throws GetBalanceFailedException;
}
