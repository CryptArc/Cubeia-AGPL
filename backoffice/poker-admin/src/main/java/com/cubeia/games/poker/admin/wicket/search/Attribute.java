package com.cubeia.games.poker.admin.wicket.search;

import java.io.Serializable;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@SuppressWarnings("serial")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Attribute implements Serializable {
    private String key;
	private String value;
	
    public Attribute() {}
	
	public Attribute(String key, String value) {
        super();
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }
	
	public void setKey(String key) {
        this.key = key;
    }
	
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	@Override
    public String toString() {
        return "Attribute [key=" + key + ", value=" + value + "]";
    }
}