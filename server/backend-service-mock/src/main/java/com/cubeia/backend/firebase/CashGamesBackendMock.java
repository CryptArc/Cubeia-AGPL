package com.cubeia.backend.firebase;

import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cubeia.backend.cashgame.PlayerSessionId;
import com.cubeia.backend.cashgame.TableId;
import com.cubeia.backend.cashgame.dto.AnnounceTableRequest;
import com.cubeia.backend.cashgame.dto.AnnounceTableResponse;
import com.cubeia.backend.cashgame.dto.BalanceUpdate;
import com.cubeia.backend.cashgame.dto.BatchHandRequest;
import com.cubeia.backend.cashgame.dto.BatchHandResponse;
import com.cubeia.backend.cashgame.dto.CloseSessionRequest;
import com.cubeia.backend.cashgame.dto.CloseTableRequest;
import com.cubeia.backend.cashgame.dto.OpenSessionRequest;
import com.cubeia.backend.cashgame.dto.OpenSessionResponse;
import com.cubeia.backend.cashgame.dto.ReserveFailedResponse;
import com.cubeia.backend.cashgame.dto.ReserveRequest;
import com.cubeia.backend.cashgame.dto.ReserveResponse;
import com.cubeia.firebase.api.action.GameObjectAction;
import com.cubeia.firebase.api.action.service.ServiceAction;
import com.cubeia.firebase.api.server.SystemException;
import com.cubeia.firebase.api.service.RoutableService;
import com.cubeia.firebase.api.service.Service;
import com.cubeia.firebase.api.service.ServiceContext;
import com.cubeia.firebase.api.service.ServiceRouter;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;

public class CashGamesBackendMock implements CashGamesBackendContract, Service, RoutableService {
    
    private Logger log = LoggerFactory.getLogger(CashGamesBackendMock.class);
    
    private final AtomicInteger idSequence = new AtomicInteger(0);

    private final Multimap<Long, Integer> sessionTransactions = 
        Multimaps.<Long, Integer>synchronizedListMultimap(ArrayListMultimap.<Long, Integer>create());

    private ServiceRouter router;
    
    private int nextId() {
        return idSequence.incrementAndGet();
    }
    
    @Override
    public void announceTable(int gameId, int tableId, AnnounceTableRequest request) {
        AnnounceTableResponse response = new AnnounceTableResponse(new TableId(nextId()));
        log.debug("new table approved, tId = {}", response.tableId.id);
        sendToTable(gameId, tableId, response);
    }

    @Override
    public void closeTable(CloseTableRequest request) {
        log.debug("table removed: {}", request.tableId.id);
    }

    @Override
    public void openSession(int gameId, int tableId, OpenSessionRequest request) {
        long sid = nextId();
        
        sessionTransactions.put(sid, 0);
        
        OpenSessionResponse response = new OpenSessionResponse(new PlayerSessionId(100000 + sid, request.playerId), 
            Collections.<String, String>emptyMap());
        log.debug("new session opened, tId = {}, pId = {}, sId = {}", 
            new Object[] {request.tableId.id, request.playerId, response.sessionId.getSessionId()});
        sendToTable(gameId, tableId, response);
    }

    @Override
    public void closeSession(CloseSessionRequest request) {
        log.warn("not implemented!");
        
        PlayerSessionId sid = request.playerSessionId;
        
        if (!sessionTransactions.containsKey(sid)) {
            log.error("error closing session {}: not found", sid);
        } else {
            sessionTransactions.removeAll(sid);
        }
    }

    @Override
    public void reserve(int gameId, int tableId, ReserveRequest request) {
        int amount = request.amount;
        long sid = request.playerSessionId.getSessionId();
        
        if (sessionTransactions.containsKey(sid)) {
            log.error("reserve failed, session not found: sId = " + sid);
            ReserveFailedResponse failResponse = new ReserveFailedResponse(request.playerSessionId, 
                ReserveFailedResponse.ErrorCode.A, "session " + sid + " not open");
            sendToTable(gameId, tableId, failResponse);
        } else {
            sessionTransactions.put(sid, amount);
            int newBalance = getBalance(sid);
            BalanceUpdate balanceUpdate = new BalanceUpdate(request.playerSessionId, newBalance, nextId());
            ReserveResponse response = new ReserveResponse(balanceUpdate, amount);
            log.debug("reserve successful: sId = {}, amount = {}, new balance = {}" + sid, amount, newBalance);
            sendToTable(gameId, tableId, response);
        }
    }

    @Override
    public BatchHandResponse batchHand(BatchHandRequest request) {
        log.warn("not implemented!");
        return new BatchHandResponse();
    }

    @Override
    public long getMainAccountBalance(int playerId) {
        log.warn("not implemented!");
        return 500000;
    }

    private int getBalance(long sid) {
        int balance = 0;
        for (Integer tx : sessionTransactions.get(sid)) {
            balance += tx;
        }
        
        return balance;
    }
    
    private void sendToTable(int gameId, int tableId, Object object) {
        GameObjectAction action = new GameObjectAction(tableId);
        action.setAttachment(object);
        router.dispatchToGame(gameId, action);
    }
    
    @Override
    public void setRouter(ServiceRouter router) {
        this.router = router;
    }
    
    @Override
    public void onAction(ServiceAction e) {
        // nothing should arrive here
    }
    
    @Override
    public void init(ServiceContext con) throws SystemException {
        log.debug("service init");
    }

    @Override
    public void destroy() {
        log.debug("service destroy");
    }

    @Override
    public void start() {
        log.debug("service start");
    }

    @Override
    public void stop() {
        log.debug("service stop");
    }

}
