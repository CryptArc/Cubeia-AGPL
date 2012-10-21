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

package com.cubeia.poker.handhistory.storage;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.cubeia.firebase.api.server.SystemException;
import com.cubeia.firebase.api.service.Service;
import com.cubeia.firebase.api.service.ServiceContext;
import com.cubeia.firebase.api.service.ServiceRegistry;
import com.cubeia.game.poker.config.api.HandHistoryConfig;
import com.cubeia.game.poker.config.api.PokerConfigurationService;
import com.cubeia.poker.handhistory.api.HandHistoryPersistenceService;
import com.cubeia.poker.handhistory.api.HistoricHand;
import com.cubeia.util.threads.SafeRunnable;

/**
 * A database based implementation of the hand history persistence service, which stores the hand
 * history in a MongoDB database.
 */
public class DatabaseStorageService implements HandHistoryPersistenceService, Service {

    private MongoPersister mongoPersister;
	private HandHistoryConfig configuration;
	private JsonPoster jsonPoster;
	
	private ExecutorService exec = Executors.newCachedThreadPool();

    @Override
    public void init(ServiceContext context) throws SystemException {
    	ServiceRegistry registry = context.getParentRegistry();
    	PokerConfigurationService configService = registry.getServiceInstance(PokerConfigurationService.class);
        configuration = configService.getHandHistoryConfig();
    	mongoPersister = new MongoPersister(context); // TODO Switch config method?
    	jsonPoster = new JsonPoster(configuration);
    }

    @Override
    public void start() {
        mongoPersister.start();
    }

    @Override
    public void persist(HistoricHand hand) {
    	exec(mongoPersister, hand);
    	exec(jsonPoster, hand);
    }

	@Override
    public void stop() {
    	mongoPersister.stop();
    }

    @Override
    public void destroy() { }
    
    
    // --- PRIVATE METHODS --- //
    
    private void exec(final HandHistoryPersistenceService service, final HistoricHand hand) {
		this.exec.submit(new SafeRunnable() {
			
			@Override
			protected void innerRun() {
				service.persist(hand);
			}
		});
	}
}
