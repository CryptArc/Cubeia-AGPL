package com.cubeia.backend.firebase;

import static com.cubeia.backoffice.wallet.api.dto.Account.AccountType.SYSTEM_ACCOUNT;
import static java.util.Arrays.asList;

import com.cubeia.backoffice.wallet.api.dto.Account.AccountStatus;
import com.cubeia.backoffice.wallet.api.dto.AccountQueryResult;
import com.cubeia.backoffice.wallet.api.dto.request.ListAccountsRequest;
import com.cubeia.firebase.api.server.SystemException;
import com.cubeia.network.wallet.firebase.api.WalletServiceContract;

public class AccountLookupUtil {

    /**
     * Lookup the id of the rake account.
     * @param walletService wallet service
     * @return rake account
     * @throws SystemException if the rake account is not found
     */
    public long lookupRakeAccountId(WalletServiceContract walletService) throws SystemException {
        ListAccountsRequest lar = new ListAccountsRequest();
        lar.setLimit(1);
        lar.setStatus(AccountStatus.OPEN);
        lar.setTypes(asList(SYSTEM_ACCOUNT));
        lar.setUserId(CashGamesBackendAdapter.RAKE_ACCOUNT_USER_ID);
        AccountQueryResult accounts = walletService.listAccounts(lar);
        if (accounts.getAccounts().size() < 1) {
            throw new SystemException("Error getting rake account. Looked for account matching: " + lar);
        } 
        return accounts.getAccounts().iterator().next().getId();
    }

}
