package com.cubeia.games.poker.admin.wicket.search;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonDeserialize;

@JsonIgnoreProperties(ignoreUnknown = true)
class Transaction implements Serializable {
	private static final long serialVersionUID = -6036782208928311686L;
	
	@SuppressWarnings("serial")
    @JsonIgnoreProperties(ignoreUnknown = true)
	public static class Entry implements Serializable {
		@JsonProperty("id")
		private Long entryId;
		private Long amount;
		private Account account;
		public Long getEntryId() {
			return entryId;
		}
		public void setEntryId(Long entryId) {
			this.entryId = entryId;
		}
		public Long getAmount() {
			return amount;
		}
		public void setAmount(Long amount) {
			this.amount = amount;
		}
		public Account getAccount() {
			return account;
		}
		public void setAccount(Account account) {
			this.account = account;
		}
		@Override
		public String toString() {
			return "Entry [entryId=" + entryId + ", amount=" + amount
					+ ", account=" + account + "]";
		}
	}
	
	@JsonProperty("id")
    private Long transactionId;

	private Date timestamp;
    private String comment;
    private String externalId;

    private List<Entry> entries;
    
    @JsonDeserialize(keyAs = String.class, contentAs = Attribute.class)
    private Map<String, Attribute> attributes;

	public Long getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(Long transactionId) {
		this.transactionId = transactionId;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getExternalId() {
		return externalId;
	}

	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}

	public List<Entry> getEntries() {
		return entries;
	}

	public void setEntries(List<Entry> entries) {
		this.entries = entries;
	}

	public Map<String, Attribute> getAttributes() {
		return attributes;
	}

	public void setAttributes(Map<String, Attribute> attributes) {
		this.attributes = attributes;
	}

	@Override
	public String toString() {
		return "Transaction [transactionId=" + transactionId + ", timestamp="
				+ timestamp + ", comment=" + comment + ", externalId="
				+ externalId + ", entries=" + entries + ", attributes="
				+ attributes + "]";
	}
}