package com.cubeia.games.poker.debugger.json;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

@XmlRootElement
@JsonSerialize(include = Inclusion.NON_NULL)
public class HandHistory {
	
	public List<HandEvent> events = new ArrayList<HandEvent>();
	
}
