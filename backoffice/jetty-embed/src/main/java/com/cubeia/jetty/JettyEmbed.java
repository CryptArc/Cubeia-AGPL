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

package com.cubeia.jetty;

import java.io.File;
import java.io.FileFilter;
import java.net.URISyntaxException;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppClassLoader;
import org.eclipse.jetty.webapp.WebAppContext;

public class JettyEmbed  {
    
    public final Object caller;
    public final int port;
    public final String warFile;
    public final String warContextPath;

    public JettyEmbed(Object caller, int port, String warFile, String warContextPath) {
        this.caller = caller;
        this.port = port;
        this.warFile = warFile;
        this.warContextPath = warContextPath;   
    }
    
    private static final Logger log = Logger.getLogger(JettyEmbed.class);
    
    public String finalFileName(String libDir) {
        File dir = new File(libDir);
        FileFilter fileFilter = new WildcardFileFilter(warFile);
        File[] files = dir.listFiles(fileFilter);
        log.debug("warFile : " + warFile + " - finalFileName: " + files[0].getName());
        return files[0].getName();
    }
    
    /** 
     * Takes a relative to the cwd (current work dir, poker-uar) path and 
     * creates the absolute path to the war file.
     * <p>
     * TODO: this needs to be changed to use either java or firebase mechanics
     * to determine cwd and the war location in a more elegant (ideally 
     * generic) way.
     *
     * @return the absolute path to the war
     */
    public String getWarPath() {
        File file;
        String sarRoot;
        try {
            file = new File(caller.getClass().getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
            sarRoot = file.getParent();
        } catch (URISyntaxException ex) {
            log.debug(null, ex);
            sarRoot = "";
        }
        String libDir = FilenameUtils.concat(sarRoot, "META-INF/lib");
        String filename = this.finalFileName(libDir);
        String warPath = FilenameUtils.concat(libDir, filename);
        log.debug("warPath : " + warPath);    
        return warPath;
    }
    

    /**
     * Creates a WebAppContext which can be added to the server
     *
     * @return the webapp context 
     */
    public WebAppContext createWebAppContext() {
        WebAppContext webapp = new WebAppContext();
        webapp.setContextPath(warContextPath);

        try {
            //http://wiki.eclipse.org/Jetty/Reference/Jetty_Classloading#Starting_Jetty_with_a_Custom_ClassLoader
            webapp.setClassLoader(new WebAppClassLoader(this.getClass().getClassLoader(), webapp));
        }catch(Exception ex) {
            log.debug(ex, ex);
        }

        webapp.setWar(getWarPath());    
        return webapp;
    }
    
    
    public Server start() {
        log.debug(caller.getClass().getCanonicalName() + " - on embedded jetty on port: " + port);
        try {
            Server server = new Server(port);

            WebAppContext context = createWebAppContext();
            server.setHandler(context);
            context.setServer(server);
            
            //ATTN: do not use jetty-env.xml here to provice the datasource
            //datasource is provided by firebase, using this additional file
            //within uar/.../firebase/conf/game/deploy/pokerDS-ds.xml
            //which makes it available on jndi as pokerDS
        
            server.start();
            return server;

        } catch (Exception ex) {
            log.debug(ex, ex);
            return null;
        }
       
    }

    public void stop() {
    }
}
