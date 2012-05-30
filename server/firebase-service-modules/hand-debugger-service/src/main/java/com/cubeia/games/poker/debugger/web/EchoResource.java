/**
 * Copyright (C) 2010 Cubeia Ltd <info@cubeia.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

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
