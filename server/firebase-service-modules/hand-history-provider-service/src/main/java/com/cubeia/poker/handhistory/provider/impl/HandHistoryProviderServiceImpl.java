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

package com.cubeia.poker.handhistory.provider.impl;

import com.cubeia.firebase.api.action.service.ClientServiceAction;
import com.cubeia.firebase.api.action.service.ServiceAction;
import com.cubeia.firebase.api.server.SystemException;
import com.cubeia.firebase.api.service.RoutableService;
import com.cubeia.firebase.api.service.Service;
import com.cubeia.firebase.api.service.ServiceContext;
import com.cubeia.firebase.api.service.ServiceRouter;
import com.cubeia.poker.handhistory.provider.api.HandHistoryProviderService;
import org.apache.log4j.Logger;

public class HandHistoryProviderServiceImpl implements HandHistoryProviderService, Service, RoutableService {

    private static final Logger log = Logger.getLogger(HandHistoryProviderServiceImpl.class);

    private ServiceRouter router;

    @Override
    public String getHandHistory(String handId) {
        return "{id:'someHandId'}";
    }

    @Override
    public void setRouter(ServiceRouter router) {
        this.router = router;
    }

    @Override
    public void onAction(ServiceAction e) {
        String handId = String.valueOf(e.getData());
        log.info("Action received. HandId: : " + handId);
        ServiceAction action = new ClientServiceAction(e.getPlayerId(), -1, getHandHistory(handId).getBytes());
        router.dispatchToPlayer(e.getPlayerId(), action);
    }

    @Override
    public void init(ServiceContext con) throws SystemException {
        log.debug("HandHistoryProviderService STARTED! ");
    }

    @Override
    public void destroy() { }

    @Override
    public void start() { }

    @Override
    public void stop() { }

}
