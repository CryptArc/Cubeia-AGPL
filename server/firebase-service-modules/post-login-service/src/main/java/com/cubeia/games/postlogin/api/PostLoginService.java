/**
 * Copyright (C) 2012 Cubeia Ltd <info@cubeia.com>
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

package com.cubeia.games.postlogin.api;

import com.cubeia.firebase.api.action.mtt.MttAction;
import com.cubeia.firebase.api.action.mtt.MttObjectAction;
import com.cubeia.firebase.api.action.service.ServiceAction;
import com.cubeia.firebase.api.login.PostLoginProcessor;
import com.cubeia.firebase.api.server.SystemException;
import com.cubeia.firebase.api.service.RoutableService;
import com.cubeia.firebase.api.service.Service;
import com.cubeia.firebase.api.service.ServiceContext;
import com.cubeia.firebase.api.service.ServiceRouter;
import com.cubeia.firebase.api.service.mttplayerreg.TournamentPlayerRegistry;
import com.cubeia.games.poker.tournament.messages.PlayerLeft;
import org.apache.log4j.Logger;

public class PostLoginService implements PostLoginProcessor, Service, RoutableService {

    private static final Logger log = Logger.getLogger(PostLoginService.class);

    private TournamentPlayerRegistry tournamentPlayerRegistry;

    private ServiceRouter router;

    @Override
    public void clientDisconnected(int playerId) {
        log.debug("Player " + playerId + " disconnected.");
        unregisterFromSitAndGoTournaments(playerId);
    }

    @Override
    public void clientLoggedIn(int playerId, String screenName) {

    }

    @Override
    public void clientLoggedOut(int playerId) {
        log.debug("Player " + playerId + " logged out.");
        unregisterFromSitAndGoTournaments(playerId);
    }

    @Override
    public void destroy() {

    }

    @Override
    public void init(ServiceContext context) throws SystemException {
        tournamentPlayerRegistry = context.getParentRegistry().getServiceInstance(TournamentPlayerRegistry.class);
    }

    @Override
    public void setRouter(ServiceRouter router) {
        this.router = router;
    }

    @Override
    public void onAction(ServiceAction e) {

    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }

    private void unregisterFromSitAndGoTournaments(int playerId) {
        int[] tournamentsForPlayer = tournamentPlayerRegistry.getTournamentsForPlayer(playerId);
        for (int tournamentId : tournamentsForPlayer) {
            log.debug("Notifying tournament " + tournamentId + " that player " + playerId + " left.");
            MttAction unregisterAction = new MttObjectAction(tournamentId, new PlayerLeft(playerId));
            router.dispatchToTournament(tournamentId, unregisterAction);
        }
    }
}
