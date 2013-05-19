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
import java.io.File;
import java.io.FileInputStream;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;
import org.eclipse.jetty.plus.webapp.EnvConfiguration;
import org.eclipse.jetty.webapp.WebAppClassLoader;
//import org.eclipse.jetty.server.ServerConnector;

public class WarServerService implements WarServerContract, Service {

    private static final Logger log = Logger.getLogger(WarServerService.class);
     
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
        String cwd = System.getProperty("user.dir");
        String warPath = FilenameUtils.concat(cwd, war);
        log.debug("warPath: " + warPath);
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

        //TODO copy client.properties to target/firebase/conf

        //TODO obtain the location of the war file
        //"lib/poker-client-web-1.0-SNAPSHOT.war" should be obtained by code, 
        //instead of giving manually the relative path
        //the following manually entered location works fine
        webapp.setWar(getWarPath(war));    
        return webapp;
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
        try {
            //TODO retrieve port-no from config file
            Server server = new Server(19999);

            WebAppContext client = createWebAppContext("../../../client/web/target/poker-client.war", "/");
            server.setHandler(client);
            server.start();
                        
            /* after nearly 2 days of trial&error, disabling for now
            //TODO this has still problems with the jndi datasource configured in jetty-env.xml
            //     closest solution within: https://gist.github.com/armhold/1539302
            Server adminServer = new Server(19998);
            WebAppContext admin = createWebAppContext("../../../backoffice/poker-admin/target/poker-admin.war", "/");
            
            //System.setProperty("java.naming.factory.url.pkgs", "org.eclipse.jetty.jndi");
            //System.setProperty("java.naming.factory.initial", "org.eclipse.jetty.jndi.InitialContextFactory");
            EnvConfiguration  configuration = new EnvConfiguration();
            configuration.setJettyEnvXml(new File("../../../backoffice/poker-admin/src/test/resources/jetty-env.xml").toURI().toURL());
            configuration.configure(admin);
            adminServer.setHandler(admin);
//!!! server start is disabled
            //adminServer.start();
            */
        } catch (Exception ex) {
            log.debug(ex, ex);
        }
       
    }

    @Override
    public void stop() {
    }

}
