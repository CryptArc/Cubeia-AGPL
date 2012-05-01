package com.cubeia.games.poker.debugger.json;

import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement
@JsonSerialize(include = Inclusion.NON_NULL)
public class HandHistory {

    public List<HandEvent> events = new ArrayList<HandEvent>();

}
