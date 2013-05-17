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

package com.cubeia.games.poker.war.service;

import com.cubeia.firebase.api.server.SystemException;
import com.cubeia.firebase.api.service.Service;
import com.cubeia.firebase.api.service.ServiceContext;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;
//import org.eclipse.jetty.server.ServerConnector;

public class WarServerService implements WarServerContract, Service {

    private static final Logger log = Logger.getLogger(WarServerService.class);
    
    /** Takes a relative to the cwd (current work dir, poker-uar) path and 
     * creates the absolute path to the war file.
     * \n
     * TODO: this needs to be changed to use either java or firebase mechanics
     * to determine cwd and the war location in a more elegant (ideally 
     * generic) way.
     *
     * @param war the relative path to the war
     * @return the absolute path to the war
     */
    public String getWarPath(String war) {
        String cwd = System.getProperty("user.dir");
        String warPath = FilenameUtils.concat(cwd, war);
        log.debug("warPath: " + warPath);
        return warPath;
    }
         
    @Override
    public void init(ServiceContext con) throws SystemException {
        log.debug("WarService STARTED! ");
    }

    @Override
    public void destroy() {
    }

    @Override
    public void start() {
        log.debug("WarService START");
        // this is "stolen" from the bot-service. 
        ClassLoader originalClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
            //TODO retrieve port-no from config file
            Server server = new Server(19999);
            WebAppContext webapp = new WebAppContext();
            webapp.setContextPath("/");
            
            //TODO try to simplify the class-loader setup, based on next line
            //webapp.setClassLoader(this.getClass().getClassLoader());
            
            //TODO copy client.properties to target/firebase/conf
            
            //TODO obtain the location of the war file
            //the following manually entered location works
            //webapp.setWar("/cb/poker/server/game-modules/poker-uar/target/firebase-run/firebase-1.9.4-CE/work/_services/431332849_43/META-INF/lib/poker-client-web-1.0-SNAPSHOT.war");
            webapp.setWar(getWarPath("../../../client/web/target/poker-client.war"));
            server.setHandler(webapp);
            server.start();       
            //server.join(); // join waits until the thread exits
        } catch (Exception ex) {
            log.debug(ex, ex);
            Thread.currentThread().setContextClassLoader(originalClassLoader);
        }
       
    }

    @Override
    public void stop() {
    }

}
