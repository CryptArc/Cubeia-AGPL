/**
 * Copyright (C) 2010 Cubeia Ltd <info@cubeia.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.cubeia.games.poker.debugger;

import com.cubeia.firebase.api.action.GameAction;
import com.cubeia.firebase.api.action.TableChatAction;
import com.cubeia.firebase.api.action.service.ServiceAction;
import com.cubeia.firebase.api.server.SystemException;
import com.cubeia.firebase.api.service.Service;
import com.cubeia.firebase.api.service.ServiceContext;
import com.cubeia.firebase.api.service.ServiceRouter;
import com.cubeia.games.poker.debugger.cache.TableEventCache;
import com.cubeia.games.poker.debugger.cache.TablePlayerInfoCache;
import com.cubeia.games.poker.debugger.guice.GuiceConfig;
import com.cubeia.games.poker.debugger.server.WebServer;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Singleton
public class HandDebuggerImpl implements HandDebuggerContract, Service {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    @Inject
    GuiceConfig guice;

    @Inject
    WebServer server;

    @Inject
    TableEventCache tableEventCache;

    @Inject
    TablePlayerInfoCache playerInfoCache;

    private ServiceRouter router;

    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    @Override
    public void destroy() { }
    
	@Override
	public void init(ServiceContext con) throws SystemException { }
	
	@Override
	public void stop() { }
    
    @Override
    public void start() {
        server.start();
    }

    @Override
    public void addPublicAction(int tableId, GameAction action) {
        tableEventCache.addPublicAction(tableId, action);
    }

    @Override
    public void addPrivateAction(int tableId, int playerId, GameAction action) {
        tableEventCache.addPrivateAction(tableId, playerId, action);
    }

    @Override
    public void updatePlayerInfo(int tableId, int playerId, String name, boolean isSittingIn, long balance, long betstack) {
        playerInfoCache.updatePlayerInfo(tableId, playerId, name, isSittingIn, balance, betstack);
    }

    @Override
    public void clearTable(int tableId) {
        tableEventCache.clearTable(tableId);
    }

    @Override
    public void sendHttpLink(int tableId, int playerId) {
        if (router != null) {
            scheduler.schedule(new URLSender(tableId, playerId), 2, TimeUnit.SECONDS);
        }
    }

    private String getLocalIP() {
        String address = null;
        try {
            InetAddress ownIp = InetAddress.getLocalHost();
            address = ownIp.getHostAddress();
        } catch (UnknownHostException e) {
        }
        return address == null ? "localhost" : address;
    }

    @Override
    public void setRouter(ServiceRouter router) {
        this.router = router;
    }

    @Override
    public void onAction(ServiceAction e) {
    }


    private class URLSender implements Runnable {

        private final int tableId;
        private final int playerId;

        public URLSender(int tableId, int playerId) {
            this.tableId = tableId;
            this.playerId = playerId;
        }

        @Override
        public void run() {
            String myAddress = getLocalIP();
            String url = "http://" + myAddress + ":9091/table.html?tableid=" + tableId;
            String message = "Hand debugger is available at " + url;
            TableChatAction chat = new TableChatAction(playerId, tableId, message);
            log.info("Sending hand debugger info for table[{}] to player[{}]: " + message, tableId, playerId);
            router.dispatchToPlayer(playerId, chat);
        }

    }
}
