package com.cubeia.games.poker.debugger.json;

import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

@XmlRootElement
@JsonSerialize(include = Inclusion.NON_NULL)
public class HandEvent {
	
	public EventType type = EventType.unknown;
	
	public String description;
	
}
