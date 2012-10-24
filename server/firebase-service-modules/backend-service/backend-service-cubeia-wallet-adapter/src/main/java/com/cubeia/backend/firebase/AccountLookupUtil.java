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

package com.cubeia.backend.firebase;

import static com.cubeia.backoffice.wallet.api.dto.Account.AccountType.STATIC_ACCOUNT;
import static com.cubeia.backoffice.wallet.api.dto.Account.AccountType.SYSTEM_ACCOUNT;
import static java.util.Arrays.asList;

import com.cubeia.backoffice.wallet.api.dto.Account.AccountStatus;
import com.cubeia.backoffice.wallet.api.dto.AccountQueryResult;
import com.cubeia.backoffice.wallet.api.dto.request.ListAccountsRequest;
import com.cubeia.firebase.api.server.SystemException;
import com.cubeia.network.wallet.firebase.api.WalletServiceContract;

public class AccountLookupUtil {

    public long lookupRakeAccountId(WalletServiceContract walletService) throws SystemException {
        ListAccountsRequest request = new ListAccountsRequest();
        request.setLimit(1);
        request.setStatus(AccountStatus.OPEN);
        request.setTypes(asList(SYSTEM_ACCOUNT));
        request.setUserId(CashGamesBackendAdapter.RAKE_ACCOUNT_USER_ID);
        AccountQueryResult accounts = walletService.listAccounts(request);
        if (accounts.getAccounts() == null || accounts.getAccounts().size() < 1) {
            throw new SystemException("Error getting rake account. Looked for account matching: " + request);
        }
        return accounts.getAccounts().iterator().next().getId();
    }

    public long lookupMainAccountIdForPLayer(WalletServiceContract walletService, long playerId) {
        ListAccountsRequest request = new ListAccountsRequest();
        request.setLimit(1);
        request.setStatus(AccountStatus.OPEN);
        request.setTypes(asList(STATIC_ACCOUNT));
        request.setUserId(playerId);
        AccountQueryResult accounts = walletService.listAccounts(request);
        if (accounts.getAccounts() == null || accounts.getAccounts().size() < 1) {
            return -1;
        }
        return accounts.getAccounts().iterator().next().getId();
    }
}
