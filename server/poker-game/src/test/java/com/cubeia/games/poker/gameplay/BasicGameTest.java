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

package com.cubeia.games.poker.gameplay;

import java.math.BigDecimal;

import mock.MockServiceRegistry;
import mock.MockWalletService;

import org.junit.Test;

import com.cubeia.firebase.api.game.context.GameContext;
import com.cubeia.firebase.api.service.ServiceRegistry;
import com.cubeia.firebase.api.util.ResourceLocator;
import com.cubeia.games.poker.PokerGame;
import com.cubeia.network.wallet.firebase.api.WalletServiceContract;

public class BasicGameTest {

	private MockWalletService mockWalletService = new MockWalletService() {
		@Override
		public Long startSession(int licenseeId, int userId, int tableId, int gameId, String userName) {
			return 1337l;
		}
		
		@Override
		public void withdraw(BigDecimal amount, int licenseeId, long sessionId, String comment) {}
	};
	
	@Test
	public void testBasic() throws Exception {
		PokerGame game = new PokerGame();
		
		game.init(new GameContext() {
			public ResourceLocator getResourceLocator() {return null;}
			public ServiceRegistry getServices() {
				MockServiceRegistry mockServiceRegistry = new MockServiceRegistry();
				mockServiceRegistry.addService(WalletServiceContract.class, mockWalletService);
				return mockServiceRegistry;
			}
		});
	}
	
}
