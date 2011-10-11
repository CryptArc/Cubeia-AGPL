package com.cubeia.games.poker.debugger.web;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.cubeia.games.poker.debugger.cache.TableEventCache;
import com.cubeia.games.poker.debugger.json.HandEvent;
import com.cubeia.games.poker.debugger.json.HandHistory;
import com.google.inject.Inject;

@Path("/table")
@Produces({ MediaType.APPLICATION_JSON })
public class TableResource {
	
	@Inject TableEventCache<String> cache;
	
    @GET
    @Path("id/{tableId}")
    public HandHistory getHandHistory(@PathParam("tableId")int tableId) {
    	List<String> events = cache.getEvents(tableId);
    	HandHistory handHistory = new HandHistory();
    	
    	if (events != null) {
	    	for (String entry : events) {
	    		HandEvent event = new HandEvent();
	    		event.description = entry;
	    		handHistory.events.add(event);
	    	}
	    	return handHistory;
	    	
    	} else {
    		return new HandHistory();
    	}
    }
    
}
