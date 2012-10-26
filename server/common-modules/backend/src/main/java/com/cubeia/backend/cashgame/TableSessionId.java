package com.cubeia.backend.cashgame;

import java.io.Serializable;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

public class TableSessionId implements Serializable {

	private static final long serialVersionUID = 6504610701772006286L;
	
	/**
	 * Firebase player ID. 
	 */
	public final int playerId;
	
	/**
	 * Table ID, should never be null.
	 */
	public final TableId tableId;
	
	/**
	 * @param playerId Firebase player ID, mandatory
	 * @param tableId Table ID, must not be null
	 */
	public TableSessionId(int playerId, TableId tableId) {
		this.playerId = playerId;
		this.tableId = tableId;
	}
	
	@Override
	public boolean equals(Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj);
	}
	
	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
