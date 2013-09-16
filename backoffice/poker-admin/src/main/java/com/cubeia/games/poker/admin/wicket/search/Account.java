package com.cubeia.games.poker.admin.wicket.search;

import java.io.Serializable;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonDeserialize;

@JsonIgnoreProperties(ignoreUnknown = true)
class Account implements Serializable {
	private static final long serialVersionUID = -6036782208928311686L;
	
	@JsonProperty("id")
    private Long accountId;
    private Long userId;
    private Long walletId;
    private String name;
    private String status;
    private String type;
    private Long created;
    private Long closed;
    private String currencyCode;
    private int fractionalDigits;

    @JsonDeserialize(keyAs = String.class, contentAs = Attribute.class)
    private Map<String, Attribute> attributes;
    
	public Long getAccountId() {
		return accountId;
	}
	
	public void setAccountId(Long accountId) {
		this.accountId = accountId;
	}

	public Long getUserId() {
		return userId;
	}
	
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	
	public Long getWalletId() {
		return walletId;
	}
	
	public void setWalletId(Long walletId) {
		this.walletId = walletId;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getStatus() {
		return status;
	}
	
	public void setStatus(String status) {
		this.status = status;
	}
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public Long getCreated() {
		return created;
	}
	
	public void setCreated(Long created) {
		this.created = created;
	}
	
	public Long getClosed() {
		return closed;
	}
	
	public void setClosed(Long closed) {
		this.closed = closed;
	}
	
	public String getCurrencyCode() {
		return currencyCode;
	}
	
	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}
	
	public int getFractionalDigits() {
		return fractionalDigits;
	}

	public void setFractionalDigits(int fractionalDigits) {
		this.fractionalDigits = fractionalDigits;
	}
	
	public Map<String, Attribute> getAttributes() {
		return attributes;
	}
	
	public void setAttributes(Map<String, Attribute> attributes) {
		this.attributes = attributes;
	}
	
	
	@Override
	public String toString() {
		return "Account [accountId=" + accountId + ", userId=" + userId
				+ ", walletId=" + walletId + ", name=" + name + ", status="
				+ status + ", type=" + type + ", created=" + created
				+ ", closed=" + closed + ", currencyCode=" + currencyCode
				+ ", fractionalDigits=" + fractionalDigits + ", attributes="
				+ attributes + "]";
	}
    
}