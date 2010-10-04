package com.cubeia.games.poker.gameplay;

import java.math.BigDecimal;

import junit.framework.TestCase;
import mock.MockServiceRegistry;
import mock.MockWalletService;

import com.cubeia.firebase.api.game.context.GameContext;
import com.cubeia.firebase.api.service.ServiceRegistry;
import com.cubeia.firebase.api.util.ResourceLocator;
import com.cubeia.games.poker.PokerGame;
import com.cubeia.network.wallet.firebase.api.WalletServiceContract;

public class BasicGameTest extends TestCase {

	private MockWalletService mockWalletService = new MockWalletService() {
		@Override
		public Long startSession(int licenseeId, int userId, int tableId, int gameId, String userName) {
			return 1337l;
		}
		
		@Override
		public void withdraw(BigDecimal amount, int licenseeId, long sessionId, String comment) {}
	};
	
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
