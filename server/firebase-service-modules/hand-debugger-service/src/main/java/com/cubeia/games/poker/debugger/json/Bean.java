package com.cubeia.games.poker.debugger.json;

import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@JsonSerialize(include = Inclusion.NON_NULL)
public class Bean {

    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
