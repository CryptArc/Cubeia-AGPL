package com.cubeia.backend.firebase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cubeia.backend.cashgame.AllowJoinResponse;
import com.cubeia.backend.cashgame.PlayerSessionId;
import com.cubeia.backend.cashgame.PlayerSessionIdImpl;
import com.cubeia.backend.cashgame.TableIdImpl;
import com.cubeia.backend.cashgame.callback.AnnounceTableCallback;
import com.cubeia.backend.cashgame.callback.OpenSessionCallback;
import com.cubeia.backend.cashgame.callback.ReserveCallback;
import com.cubeia.backend.cashgame.dto.AnnounceTableRequest;
import com.cubeia.backend.cashgame.dto.AnnounceTableResponse;
import com.cubeia.backend.cashgame.dto.BalanceUpdate;
import com.cubeia.backend.cashgame.dto.BatchHandRequest;
import com.cubeia.backend.cashgame.dto.BatchHandResponse;
import com.cubeia.backend.cashgame.dto.CloseSessionRequest;
import com.cubeia.backend.cashgame.dto.HandResult;
import com.cubeia.backend.cashgame.dto.OpenSessionRequest;
import com.cubeia.backend.cashgame.dto.OpenSessionResponse;
import com.cubeia.backend.cashgame.dto.ReserveFailedResponse;
import com.cubeia.backend.cashgame.dto.ReserveRequest;
import com.cubeia.backend.cashgame.dto.ReserveResponse;
import com.cubeia.backend.cashgame.exceptions.BatchHandFailedException;
import com.cubeia.backend.cashgame.exceptions.GetBalanceFailedException;
import com.cubeia.backend.firebase.impl.FirebaseCallbackFactoryImpl;
import com.cubeia.firebase.api.action.service.ServiceAction;
import com.cubeia.firebase.api.server.SystemException;
import com.cubeia.firebase.api.service.RoutableService;
import com.cubeia.firebase.api.service.Service;
import com.cubeia.firebase.api.service.ServiceContext;
import com.cubeia.firebase.api.service.ServiceRouter;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;

public class CashGamesBackendMock implements CashGamesBackendContract, Service, RoutableService {
    
    private Logger log = LoggerFactory.getLogger(CashGamesBackendMock.class);
    
    private final AtomicInteger idSequence = new AtomicInteger(0);

    private final Multimap<PlayerSessionId, Integer> sessionTransactions = 
        Multimaps.<PlayerSessionId, Integer>synchronizedListMultimap(LinkedListMultimap.<PlayerSessionId, Integer>create());

    private ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    
    private ServiceRouter router;
    
    @Override
    public String generateHandId() {
    	return "" + System.currentTimeMillis();
    }
    
    @Override
    public boolean isSystemShuttingDown() {
    	return false;
    }
    
    private int nextId() {
        return idSequence.incrementAndGet();
    }
    
    @Override
    public AllowJoinResponse allowJoinTable(int playerId) {
    	return new AllowJoinResponse(true, -1);
    }
    
    @Override
    public FirebaseCallbackFactory getCallbackFactory() {
        return new FirebaseCallbackFactoryImpl(router);
    }
    
    @Override
    public void announceTable(AnnounceTableRequest request, final AnnounceTableCallback callback) {
        final AnnounceTableResponse response = new AnnounceTableResponse(new TableIdImpl());
        response.setProperty(MARKET_TABLE_REFERENCE_KEY, "MOCK-TABLE-ID-" + System.currentTimeMillis());
        
        // Dirty mokkie fix as we cannot run this in the same thread as the participant runs in
        long mockDelay = (long) (Math.random() * 2000);
        
        executor.schedule(new Runnable() {
            @Override public void run() { callback.requestSucceded(response); }
        }, mockDelay, TimeUnit.MILLISECONDS);
    }

    /*@Override
    public void closeTable(CloseTableRequest request) {
        log.debug("table removed");
    }*/

    @Override
    public void openSession(OpenSessionRequest request, OpenSessionCallback callback) {
        PlayerSessionId sessionId = new PlayerSessionIdImpl(request.playerId);
        sessionTransactions.put(sessionId, 0);
        
        OpenSessionResponse response = new OpenSessionResponse(sessionId, Collections.<String, String>emptyMap());
        log.debug("new session opened, tId = {}, pId = {}, sId = {}", 
            new Object[] {request.tableId, request.playerId, response.sessionId});
        log.debug("currently open sessions: {}", sessionTransactions.size());
        callback.requestSucceded(response);
        
        printDiagnostics();        
    }

    @Override
    public void closeSession(CloseSessionRequest request) {
        PlayerSessionId sid = request.playerSessionId;
        
        if (!sessionTransactions.containsKey(sid)) {
            log.error("error closing session {}: not found", sid);
        } else {
            int closingBalance = getBalance(sid);
            sessionTransactions.removeAll(sid);
            log.debug("closed session {} with balance: {}", sid, closingBalance);
        }
        
        printDiagnostics();
    }

    @Override
    public void reserve(ReserveRequest request, final ReserveCallback callback) {
        int amount = request.amount;
        PlayerSessionId sid = request.playerSessionId;
        
        if (!sessionTransactions.containsKey(sid)) {
            log.error("reserve failed, session not found: sId = " + sid);
            ReserveFailedResponse failResponse = new ReserveFailedResponse(request.playerSessionId, 
                ReserveFailedResponse.ErrorCode.SESSION_NOT_OPEN, "session " + sid + " not open", true);
            callback.requestFailed(failResponse);
            
        } else if (amount == 66  ||  amount == 660  ||  amount == 6600){ // MAGIC FAIL FOR 66 cents BUY-IN 
        	log.error("Failing reserve with {}ms delay for magic amount 66 cents (hardcoded for debug reasons). sId={}", sid);
            final ReserveFailedResponse failResponse = new ReserveFailedResponse(request.playerSessionId, 
                ReserveFailedResponse.ErrorCode.UNSPECIFIED_FAILURE, "Unknown operator error (magic 66-cent ultra-fail)", true);
            long delay = (long) (Math.random() * 2000);
            
            executor.schedule(new Runnable() {
                @Override public void run() { callback.requestFailed(failResponse); }
            }, delay, TimeUnit.MILLISECONDS);
            
        } else {
            sessionTransactions.put(sid, amount);
            int newBalance = getBalance(sid);
            BalanceUpdate balanceUpdate = new BalanceUpdate(request.playerSessionId, newBalance, nextId());
            final ReserveResponse response = new ReserveResponse(balanceUpdate, amount);
            log.debug("reserve successful: sId = {}, amount = {}, new balance = {}", new Object[] {sid, amount, newBalance});
            response.setProperty(MARKET_TABLE_SESSION_REFERENCE_KEY, "MOCK-MARKET-SID-" + sid.hashCode());
            
            long delay = 0;
            
            if (amount == 67  ||  amount == 670  ||  amount == 6700) {
                delay = (long) (5000 + Math.random() * 10000);
                log.info("succeeding reserve with {}ms delay for magic amount 67 cents (hardcoded for debug reasons). sId={}", delay, sid);
            }
            
            executor.schedule(new Runnable() {
                @Override public void run() { callback.requestSucceded(response); }
            }, delay, TimeUnit.MILLISECONDS);
        }
        
        printDiagnostics();
    }

    @Override
    public BatchHandResponse batchHand(BatchHandRequest request) throws BatchHandFailedException{
        int totalBets = 0;
        int totalWins = 0;
        int totalRakes = 0;
        List<BalanceUpdate> resultingBalances = new ArrayList<BalanceUpdate>();
        for (HandResult hr : request.handResults) {
            log.debug("recording hand result: handId = {}, sessionId = {}, bets = {}, wins = {}, rake = {}", 
                new Object[] {request.handId, hr.playerSession, hr.aggregatedBet, hr.win, hr.rake});
            long amount = hr.win - hr.aggregatedBet;
            sessionTransactions.put(hr.playerSession, (int) amount);
            resultingBalances.add(new BalanceUpdate(hr.playerSession, getBalance(hr.playerSession), -1));
            
            totalBets += hr.aggregatedBet;
            totalWins += hr.win;
            totalRakes += hr.rake;
        }
        
        //Sanity check on the sum
        int sum = totalBets - (totalWins + totalRakes);
        if(sum != 0){
          throw new BatchHandFailedException("sanity check failed on batchHand, totalBets: "+totalBets+" "
                  + "totalWins: "+totalWins+" totalRakes: "+totalRakes+" sum:" +sum);
        }else{
          log.debug("sanity check successful on batchHand, totalBets: "+totalBets+" "
                  + "totalWins: "+totalWins+" totalRakes: "+totalRakes+" sum:" +sum);
        }
        
        printDiagnostics();
        return new BatchHandResponse(resultingBalances);
    }

    @Override
    public long getMainAccountBalance(int playerId) {
        log.debug("getMainAccountBalance is not implemented yet! Returning hardcoded value of 1337000");
        return 1337000;
    }

    private int getBalance(PlayerSessionId sid) {
        int balance = 0;
        for (Integer tx : sessionTransactions.get(sid)) {
            balance += tx;
        }
        return balance;
    }
    
    @Override
    public BalanceUpdate getSessionBalance(PlayerSessionId sessionId)
    		throws GetBalanceFailedException {
        printDiagnostics();        
    	return new BalanceUpdate(sessionId, getBalance(sessionId), nextId());
    }
    
    private void printDiagnostics() {
//        log.debug("wallet session transactions: ");
//        for (PlayerSessionId session : sessionTransactions.keys()) {
//            log.debug("{} (balance: {}) -> {}", 
//                new Object[] {session, getBalance(session), sessionTransactions.get(session)});
//        }
//        log.debug("---");
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
