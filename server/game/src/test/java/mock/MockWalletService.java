/**
 * 
 */
package mock;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.cubeia.network.wallet.firebase.api.WalletServiceContract;
import com.cubeia.network.wallet.firebase.domain.BalanceRes;
import com.cubeia.network.wallet.firebase.domain.ResultEntry;
import com.cubeia.network.wallet.firebase.domain.RoundResultRes;

public class MockWalletService implements WalletServiceContract {
	
	public void deposit(BigDecimal amount, int licenseeId,long sessionId, String comment) {}

	public void endSession(long sessionId) {}

	public BalanceRes getBalance(long sessionId) {
		return null;
	}

	public Long startSession(int licenseeId,
			int userId, int tableId, int gameId, String userName) {
		return null;
	}

	public void withdraw(BigDecimal amount, int licenseeId, long sessionId, String comment) {}

	public RoundResultRes roundResult(long type, long contextId, long subContextId, 
	    Collection<ResultEntry> results, String description) {
		return null;
	}

    public Set<Long> getAllSessions() {
        return new HashSet<Long>();
    }
}