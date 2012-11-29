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

public class Table implements Serializable {

    private static final long serialVersionUID = -2964211759066901960L;

    private int tableId;
    private String tableIntegrationId;
    private String tableName;
    private int seats;

    public int getTableId() {
        return tableId;
    }

    public void setTableId(int tableId) {
        this.tableId = tableId;
    }

    public String getTableIntegrationId() {
        return tableIntegrationId;
    }

    public void setTableIntegrationId(String tableIntegrationId) {
        this.tableIntegrationId = tableIntegrationId;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    @Override
    public String toString() {
        return "Table [tableId=" + tableId + ", tableIntegrationId="
               + tableIntegrationId + ", tableName=" + tableName + ", seats="
               + seats + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + seats;
        result = prime * result + tableId;
        result = prime
                 * result
                 + ((tableIntegrationId == null) ? 0 : tableIntegrationId
                .hashCode());
        result = prime * result
                 + ((tableName == null) ? 0 : tableName.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Table other = (Table) obj;
        if (seats != other.seats) {
            return false;
        }
        if (tableId != other.tableId) {
            return false;
        }
        if (tableIntegrationId == null) {
            if (other.tableIntegrationId != null) {
                return false;
            }
        } else if (!tableIntegrationId.equals(other.tableIntegrationId)) {
            return false;
        }
        if (tableName == null) {
            if (other.tableName != null) {
                return false;
            }
        } else if (!tableName.equals(other.tableName)) {
            return false;
        }
        return true;
    }
}
