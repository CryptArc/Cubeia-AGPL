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

import com.cubeia.backoffice.accounting.api.Money;
import com.cubeia.backoffice.wallet.api.dto.Account;
import com.cubeia.backoffice.wallet.api.dto.AccountQueryResult;
import com.cubeia.backoffice.wallet.api.dto.SessionBalance;
import com.cubeia.backoffice.wallet.api.dto.report.TransactionRequest;
import com.cubeia.backoffice.wallet.api.dto.report.TransactionResult;
import com.cubeia.backoffice.wallet.api.dto.request.ListAccountsRequest;
import com.cubeia.network.wallet.firebase.api.WalletServiceContract;
import com.cubeia.network.wallet.firebase.domain.ResultEntry;
import com.cubeia.network.wallet.firebase.domain.RoundResultResponse;

public class MockWalletService implements WalletServiceContract {
	
	public void deposit(BigDecimal amount, int licenseeId,long sessionId, String comment) {}

	public void endSession(long sessionId) {}

//	public BalanceRes getBalance(long sessionId) {
//		return null;
//	}

	public Long startSession(int licenseeId,
			int userId, int tableId, int gameId, String userName) {
		return null;
	}

	public void withdraw(BigDecimal amount, int licenseeId, long sessionId, String comment) {}

//	public RoundResultRes roundResult(long type, long contextId, long subContextId, 
//	    Collection<ResultEntry> results, String description) {
//		return null;
//	}

    public Set<Long> getAllSessions() {
        return new HashSet<Long>();
    }

	@Override
	public void deposit(Money arg0, int arg1, long arg2, String arg3) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public TransactionResult doTransaction(TransactionRequest arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Money endSessionAndDepositAll(int arg0, long arg1, String arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Account getAccountById(long arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AccountQueryResult listAccounts(ListAccountsRequest arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long startSession(String arg0, int arg1, int arg2, int arg3,
			int arg4, String arg5) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void withdraw(Money arg0, int arg1, long arg2, String arg3) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public SessionBalance getBalance(long arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RoundResultResponse roundResult(long arg0, long arg1, long arg2,
			Collection<ResultEntry> arg3, String arg4) {
		// TODO Auto-generated method stub
		return null;
	}
}