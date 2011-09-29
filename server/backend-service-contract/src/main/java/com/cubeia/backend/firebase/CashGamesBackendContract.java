package com.cubeia.backend.firebase;

import com.cubeia.backend.cashgame.dto.AnnounceTableFailedResponse;
import com.cubeia.backend.cashgame.dto.AnnounceTableRequest;
import com.cubeia.backend.cashgame.dto.AnnounceTableResponse;
import com.cubeia.backend.cashgame.dto.BatchHandRequest;
import com.cubeia.backend.cashgame.dto.BatchHandResponse;
import com.cubeia.backend.cashgame.dto.CloseSessionRequest;
import com.cubeia.backend.cashgame.dto.CloseTableRequest;
import com.cubeia.backend.cashgame.dto.OpenSessionFailedResponse;
import com.cubeia.backend.cashgame.dto.OpenSessionRequest;
import com.cubeia.backend.cashgame.dto.OpenSessionResponse;
import com.cubeia.backend.cashgame.dto.ReserveFailedResponse;
import com.cubeia.backend.cashgame.dto.ReserveRequest;
import com.cubeia.backend.cashgame.dto.ReserveResponse;
import com.cubeia.backend.cashgame.exceptions.BatchHandFailedException;
import com.cubeia.backend.cashgame.exceptions.CloseSessionFailedException;
import com.cubeia.backend.cashgame.exceptions.CloseTableFailedException;
import com.cubeia.backend.cashgame.exceptions.GetBalanceFailedException;
import com.cubeia.firebase.api.action.GameObjectAction;
import com.cubeia.firebase.api.service.Contract;

public interface CashGamesBackendContract extends Contract {

    /**
     * Announce a table. 
     * This method is asynchronous and will send a {@link GameObjectAction}
     * containing a {@link AnnounceTableResponse} or {@link AnnounceTableFailedResponse} to the table
     * when done.
     * @param gameId game id
     * @param tableId table id
     * @param request request
     */
    @Async
    void announceTable(int gameId, int tableId, AnnounceTableRequest request);

    void closeTable(CloseTableRequest request) throws CloseTableFailedException;

    /**
     * Open a table session. 
     * This method is asynchronous and will send a {@link GameObjectAction}
     * containing a {@link OpenSessionResponse} or {@link OpenSessionFailedResponse} to the table 
     * when done.
     * @param gameId game id
     * @param request request
     */
    @Async
    void openSession(int gameId, OpenSessionRequest request);

    void closeSession(CloseSessionRequest request) throws CloseSessionFailedException;

    /**
     * Reserve money (transaction from main account to table session account). 
     * This method is asynchronous and will send a {@link GameObjectAction}
     * containing a {@link ReserveResponse} or {@link ReserveFailedResponse} to the table
     * when done.
     * @param gameId game id
     * @param tableId table id
     * @param request request
     */
    @Async
    void reserve(int gameId, int tableId, ReserveRequest request);

    BatchHandResponse batchHand(BatchHandRequest request) throws BatchHandFailedException;

    long getMainAccountBalance(int playerId) throws GetBalanceFailedException;
    
    
}
