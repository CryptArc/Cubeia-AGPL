package com.cubeia.games.poker.debugger.web;

import com.cubeia.games.poker.debugger.cache.Event;
import com.cubeia.games.poker.debugger.cache.TableEventCache;
import com.cubeia.games.poker.debugger.json.HandEvent;
import com.cubeia.games.poker.debugger.json.HandHistory;
import com.google.inject.Inject;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/table")
@Produces({MediaType.APPLICATION_JSON})
public class TableResource {

    @Inject
    TableEventCache cache;

    @GET
    @Path("id/{tableId}")
    public HandHistory getHandHistory(@PathParam("tableId") int tableId) {
        List<Event> events = cache.getEvents(tableId);
        return createHandHistory(events);
    }

    @GET
    @Path("previous/id/{tableId}")
    public HandHistory getPreviousHandHistory(@PathParam("tableId") int tableId) {
        List<Event> events = cache.getPreviousEvents(tableId);
        return createHandHistory(events);
    }

    private HandHistory createHandHistory(List<Event> events) {
        HandHistory handHistory = new HandHistory();
        if (events != null) {
            for (Event entry : events) {
                HandEvent event = new HandEvent();
                event.description = entry.toString();
                event.type = entry.getType();
                handHistory.events.add(event);
            }
            return handHistory;

        } else {
            return new HandHistory();
        }
    }
}
