package com.cubeia.games.poker.debugger.web;

import com.cubeia.games.poker.debugger.json.Bean;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

@Path("/")
@Consumes({MediaType.TEXT_HTML, MediaType.APPLICATION_JSON})
@Produces({MediaType.TEXT_HTML})
public class EchoResource {

    @GET
    @Path("echo")
    public Response echo() {
        return Response.status(Status.OK).entity("Hello there. I am the Poker Hand Debugger").build();
    }

    @GET
    @Path("hello")
    public String hello() {
        return "Hello there. I am the string Poker Hand Debugger";
    }

    @GET
    @Path("bean")
    @Produces({MediaType.APPLICATION_JSON})
    public Bean bean() {
        Bean bean = new Bean();
        bean.setMessage("Hello there. I am the BEAN");
        return bean;
    }

}
