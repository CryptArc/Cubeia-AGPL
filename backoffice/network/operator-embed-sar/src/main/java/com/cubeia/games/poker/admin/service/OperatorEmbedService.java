/**
 * Copyright (C) 2013 Cubeia Ltd <info@cubeia.com>
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

package com.cubeia.games.poker.admin.service;

import com.cubeia.firebase.api.server.SystemException;
import com.cubeia.firebase.api.service.Service;
import com.cubeia.firebase.api.service.ServiceContext;
import java.io.File;
import java.net.URISyntaxException;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppClassLoader;
import org.eclipse.jetty.webapp.WebAppContext;

public class OperatorEmbedService implements OperatorEmbedContract, Service {

    //TODO retrieve port-no from config file
    public static final int WAR_PORT = 9092;
    //TODO retrieve war name from dependency or config
    public static final String WAR_FILE = "operator-service-rest-1.4.0-RC2-SNAPSHOT.war";
    public static final String WAR_PATH = "/operator-service-rest";    
    
    private static final Logger log = Logger.getLogger(OperatorEmbedService.class);
    
    /** 
     * Takes a relative to the cwd (current work dir, poker-uar) path and 
     * creates the absolute path to the war file.
     * <p>
     * TODO: this needs to be changed to use either java or firebase mechanics
     * to determine cwd and the war location in a more elegant (ideally 
     * generic) way.
     *
     * @param war the relative path to the war
     * @return the absolute path to the war
     */
    public String getWarPath(String war) {
        File file;
        String sarRoot;
        try {
            file = new File(getClass().getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
            sarRoot = file.getParent();
        } catch (URISyntaxException ex) {
            log.debug(null, ex);
            sarRoot = "";
        }
        String libDir = FilenameUtils.concat(sarRoot, "META-INF/lib");
        String warPath = FilenameUtils.concat(libDir, war);
        log.debug("warPath : " + warPath);    
        return warPath;
    }
    

    /**
     * Creates a WebAppContext which can be added to the server
     * 
     * <p>
     * TODO: error handling
     *
     * @param war - full path to the war file
     * @param contextPath - context path to mount the war
     * @return the webapp context 
     */
    public WebAppContext createWebAppContext(String war, String contextPath) {
        WebAppContext webapp = new WebAppContext();
        webapp.setContextPath(contextPath);

        try {
            //http://wiki.eclipse.org/Jetty/Reference/Jetty_Classloading#Starting_Jetty_with_a_Custom_ClassLoader
            webapp.setClassLoader(new WebAppClassLoader(this.getClass().getClassLoader(), webapp));
        }catch(Exception ex) {
            log.debug(ex, ex);
        }

        webapp.setWar(getWarPath(war));    
        return webapp;
    }
    
        
    @Override
    public void init(ServiceContext con) throws SystemException {
    }

    @Override
    public void destroy() {
    }

    @Override
    public void start() {
        log.debug("OperatorEmbedService START (on port: " + WAR_PORT);
        try {
            Server server = new Server(WAR_PORT);

            WebAppContext context = createWebAppContext(WAR_FILE, WAR_PATH);
            server.setHandler(context);
            context.setServer(server);
            
            //ATTN: do not use jetty-env.xml here to provice the datasource
            //datasource is provided by firebase, using this additional file
            //within uar/.../firebase/conf/game/deploy/pokerDS-ds.xml
            //which makes it available on jndi as pokerDS
        
            server.start();

        } catch (Exception ex) {
            log.debug(ex, ex);
        }
       
    }

    @Override
    public void stop() {
    }
}
