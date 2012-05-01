package com.cubeia.backend.cashgame;

import com.cubeia.backend.cashgame.dto.*;
import com.cubeia.backend.cashgame.exceptions.*;

public interface SynchronousCashGamesBackend {

    String generateHandId();

    boolean isSystemShuttingDown();

    AnnounceTableResponse announceTable(AnnounceTableRequest request) throws AnnounceTableFailedException;

    // void closeTable(CloseTableRequest request) throws CloseTableFailedException;

    OpenSessionResponse openSession(OpenSessionRequest request) throws OpenSessionFailedException;

    void closeSession(CloseSessionRequest request) throws CloseSessionFailedException;

    ReserveResponse reserve(ReserveRequest request) throws ReserveFailedException;

    BatchHandResponse batchHand(BatchHandRequest request) throws BatchHandFailedException;

    long getMainAccountBalance(int playerId) throws GetBalanceFailedException;

    BalanceUpdate getSessionBalance(PlayerSessionId sessionId) throws GetBalanceFailedException;

    AllowJoinResponse allowJoinTable(int playerId);
}
