package com.cubeia.backend.firebase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
import com.cubeia.firebase.api.server.SystemException;
import com.cubeia.firebase.api.service.Service;
import com.cubeia.firebase.api.service.ServiceContext;

public class CashGamesBackendMock implements CashGamesBackendContract, Service {
    
    private Logger log = LoggerFactory.getLogger(CashGamesBackendMock.class);

    @Override
    public void announceTable(AnnounceTableRequest request, AnnounceTableCallback callback) {
        log.warn("not implemented!");
    }

    @Override
    public void closeTable(CloseTableRequest request) {
        log.warn("not implemented!");
    }

    @Override
    public void openSession(OpenSessionRequest request, OpenSessionCallback callback) {
        log.warn("not implemented!");
    }

    @Override
    public void closeSession(CloseSessionRequest request) {
        log.warn("not implemented!");
    }

    @Override
    public void reserve(ReserveRequest request, ReserveCallback callback) {
        log.warn("not implemented!");
    }

    @Override
    public BatchHandResponse batchHand(BatchHandRequest request) {
        log.warn("not implemented!");
        return null;
    }

    @Override
    public long getMainAccountBalance(int playerId) {
        log.warn("not implemented!");
        return 0;
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
