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

package com.cubeia.games.poker.admin;

import org.springframework.stereotype.Component;

@Component
public class Configuration {

	private String networkUrl;
	private String searchUrl;
	private String searchClusterName;
	private String walletServiceUrl;
    private String operatorServiceUrl;
    private String userServiceUrl;
    private String backofficeUrl;

	public void setWalletServiceUrl(String walletServiceUrl) {
		this.walletServiceUrl = walletServiceUrl;
	}
	
	public String getWalletServiceUrl() {
		return walletServiceUrl;
	}

    public void setOperatorServiceUrl(String operatorServiceUrl) {
        this.operatorServiceUrl = operatorServiceUrl;
    }

    public String getOperatorServiceUrl() {
        return operatorServiceUrl;
    }

    public String getSearchUrl() {
		return searchUrl;
	}
	
	public void setSearchUrl(String searchUrl) {
		this.searchUrl = searchUrl;
	}
	
	public String getNetworkUrl() {
		return networkUrl;
	}
	
	public void setNetworkUrl(String networkUrl) {
		this.networkUrl = networkUrl;
	}

	public String getSearchClusterName() {
		return searchClusterName;
	}
	
	public void setSearchClusterName(String searchClusterName) {
		this.searchClusterName = searchClusterName;
	}
	
	public String getUserServiceUrl() {
		return userServiceUrl;
	}

	public void setUserServiceUrl(String userServiceUrl) {
		this.userServiceUrl = userServiceUrl;
	}

	public String getBackofficeUrl() {
		return backofficeUrl;
	}

	public void setBackofficeUrl(String backofficeUrl) {
		this.backofficeUrl = backofficeUrl;
	}

	@Override
	public String toString() {
		return "Configuration [networkUrl=" + networkUrl + ", searchUrl="
				+ searchUrl + ", searchClusterName=" + searchClusterName
				+ ", walletServiceUrl=" + walletServiceUrl
				+ ", operatorServiceUrl=" + operatorServiceUrl + "]";
	}
}
