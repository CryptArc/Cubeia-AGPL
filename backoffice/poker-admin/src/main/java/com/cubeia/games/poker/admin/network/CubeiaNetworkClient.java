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

package com.cubeia.games.poker.admin.network;

import com.cubeia.backoffice.wallet.api.dto.Currency;
import com.cubeia.backoffice.wallet.api.dto.CurrencyListResult;
import com.cubeia.backoffice.wallet.client.WalletServiceClientHTTP;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

public class CubeiaNetworkClient implements NetworkClient {

    private WalletServiceClientHTTP walletClient;

    @Override
    public List<String> getCurrencies() {
        List<String> currencies = newArrayList();
        CurrencyListResult supportedCurrencies = walletClient.getSupportedCurrencies();
        for (Currency currency : supportedCurrencies.getCurrencies()) {
            currencies.add(currency.getCode());
        }
        return currencies;
    }

    public void setWalletClient(WalletServiceClientHTTP walletClient) {
        this.walletClient = walletClient;
    }
}
