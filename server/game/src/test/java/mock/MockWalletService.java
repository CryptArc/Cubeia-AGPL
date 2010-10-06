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