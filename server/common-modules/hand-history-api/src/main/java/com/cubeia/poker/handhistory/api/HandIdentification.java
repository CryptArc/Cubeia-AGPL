package com.cubeia.poker.handhistory.api;

public class HandIdentification {

    private final int tableId;
    private final String tableIntegrationId;
    private final String handId;

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
