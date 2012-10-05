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

package com.cubeia.poker.handhistory.api;

import java.io.Serializable;

public class HandIdentification implements Serializable {

    private static final long serialVersionUID = -3874240518028155212L;
	
    private int tableId;
    private String tableIntegrationId;
    private String handId;

    public HandIdentification() {
    }

    public HandIdentification(int tableId, String tableIntegrationId, String handId) {
        this.tableId = tableId;
        this.tableIntegrationId = tableIntegrationId;
        this.handId = handId;
    }

    public String getHandId() {
        return handId;
    }

    public int getTableId() {
        return tableId;
    }

    public String getTableIntegrationId() {
        return tableIntegrationId;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((handId == null) ? 0 : handId.hashCode());
        result = prime * result + tableId;
        result = prime
                * result
                + ((tableIntegrationId == null) ? 0 : tableIntegrationId
                .hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        HandIdentification other = (HandIdentification) obj;
        if (handId == null) {
            if (other.handId != null)
                return false;
        } else if (!handId.equals(other.handId))
            return false;
        if (tableId != other.tableId)
            return false;
        if (tableIntegrationId == null) {
            if (other.tableIntegrationId != null)
                return false;
        } else if (!tableIntegrationId.equals(other.tableIntegrationId))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "HandIdentification [tableId=" + tableId
                + ", tableIntegrationId=" + tableIntegrationId + ", handId="
                + handId + "]";
    }
}
