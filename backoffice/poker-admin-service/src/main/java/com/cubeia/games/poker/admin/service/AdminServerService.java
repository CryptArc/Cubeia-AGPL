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
import com.mysql.jdbc.jdbc2.optional.MysqlConnectionPoolDataSource;
import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppClassLoader;
import org.eclipse.jetty.webapp.WebAppContext;

import org.eclipse.jetty.plus.webapp.EnvConfiguration;
import org.eclipse.jetty.webapp.*;
import org.eclipse.jetty.jndi.InitialContextFactory;
import org.eclipse.jetty.plus.jndi.Resource;

//import org.eclipse.xml.XmlConfiguration;
import org.eclipse.jetty.plus.webapp.EnvConfiguration;
import org.eclipse.jetty.plus.webapp.PlusConfiguration;
import javax.sql.DataSource;

public class AdminServerService implements AdminServerContract, Service {

    //TODO retrieve port-no from config file
    public static final int WAR_PORT = 18088;
    //TODO retrieve war name from dependency or config
    public static final String WAR_FILE = "poker-admin.war";
    
    private static final Logger log = Logger.getLogger(AdminServerService.class);
    
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
        log.debug("PokerAdminService START (V15) on port: " + WAR_PORT);
        try {
            Server server = new Server(WAR_PORT);

            WebAppContext context = createWebAppContext(WAR_FILE, "/");
            server.setHandler(context);
            context.setServer(server);

            //Standard method, using jetty-env.xml
            EnvConfiguration envConfiguration = new EnvConfiguration();// {        
            URL url = new File("src/test/resources/firebase/conf/poker-admin-jetty-env.xml").toURI().toURL();
            envConfiguration.setJettyEnvXml(url);
            log.debug("jetty-env url:" + url );

            //apply configuration
            //this fails with javax.naming.NameAlreadyBoundException exception
            //the same code works, if started standalon (not in a SAR)
            context.setConfigurations(new Configuration[]{ envConfiguration});
            
              //although "alreay bound", looking up the datasource entry manually, fails too
//            InitialContext ic = new InitialContext()
//            DataSource myds = (DataSource)ic.lookup("java:comp/env/jdbc/pokerDS");   
            
            
            
              //Attemp to create a new entry manually, fails at new Resource
//            MysqlConnectionPoolDataSource ds = new MysqlConnectionPoolDataSource();
//            ds.setDatabaseName("poker");
//            ds.setUser("poker");
//            ds.setPassword("poker");
//            Resource resource = new Resource("java:comp/env/pokerDS", ds);
//            server.setAttribute("pokerDS", resource);
                      
//            End jetty-env
        
            server.start();

        } catch (Exception ex) {
            log.debug(ex, ex);
        }
       
    }

    @Override
    public void stop() {
    }
}
