package com.cubeia.games.poker.debugger.web;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/table")
@Produces({ MediaType.TEXT_PLAIN })
public class TableResource {
	
    @GET
    @Path("id/{tableId}")
    public String onConnectionEvent(@PathParam("tableId")int tableId) {
    	return "Fetch table "+tableId+" plz!";
    }
    
}
