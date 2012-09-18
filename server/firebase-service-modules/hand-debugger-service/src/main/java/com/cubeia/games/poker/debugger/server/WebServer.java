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

package com.cubeia.games.poker.debugger.server;

import com.cubeia.games.poker.debugger.guice.GuiceConfig;
import com.cubeia.games.poker.debugger.web.EmptyServlet;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.servlet.GuiceFilter;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;

import javax.servlet.DispatcherType;
import java.net.URL;
import java.util.EnumSet;

@Singleton
public class WebServer implements Runnable {

    @Inject
    GuiceConfig guice;
    private Server server;

    Thread thread;

    public void start() {
        thread = new Thread(this);
        thread.setContextClassLoader(Thread.currentThread().getContextClassLoader());
        thread.setDaemon(true);
        thread.start();
    }

    public void run() {
        try {
            server = new Server(19091);

            // Static resources
            URL url = getClass().getResource("/html/base_index_file.html");
            String resource = url.toString().replaceAll("base_index_file.html", "");

            ResourceHandler resource_handler = new ResourceHandler();
            resource_handler.setDirectoriesListed(true);
            resource_handler.setResourceBase(resource);

            // Dynamic content
            ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
            context.setClassLoader(getClass().getClassLoader());
            context.setContextPath("/api");
            context.addEventListener(guice);

            FilterHolder filterHolder = new FilterHolder(GuiceFilter.class);
            context.addFilter(filterHolder, "/*", EnumSet.allOf(DispatcherType.class));
            context.addServlet(EmptyServlet.class, "/*");

            // Add all handlers to server
            HandlerList handlers = new HandlerList();
            handlers.setHandlers(new Handler[]{resource_handler, context, new DefaultHandler()});
            server.setHandler(handlers);

            server.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        try {
            server.stop();
        } catch (Exception e) {
        }
    }
}
