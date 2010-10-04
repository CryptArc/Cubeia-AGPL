package com.cubeia.games.poker.persistence;

import javax.sql.DataSource;

import org.apache.log4j.Logger;

import com.cubeia.firebase.api.service.ServiceRegistry;
import com.cubeia.firebase.api.service.datasource.DatasourceServiceContract;

/**
 * Checks if a database has:
 * 1. Deployed definition (poker-ds.xml)
 * 2. A created entity manager
 * 
 * TODO: Since this setup returns false for both premises it might be better to 
 * only check if configuration is deployed.
 *
 * TODO2: Eventually we might want to add some dynamic behavior to this class.
 *
 * @author Fredrik Johansson, Cubeia Ltd
 */
public class Persist {
    
    private static final String DATASOURCE_NAME = "poker";

    private static final transient Logger log = Logger.getLogger(Persist.class);
    
    /** Flag if database connection is available and should be used */
    public static boolean available = true;
    
    /**
     * Check if a database is configured and connected to the system.
     */
    public static void checkDatabaseAvailable(ServiceRegistry serviceRegistry) {
        if (serviceRegistry == null) {
            available = false;
        } else {
            DatasourceServiceContract datasources = serviceRegistry.getServiceInstance(DatasourceServiceContract.class);
            DataSource ds = datasources.getDatasource(DATASOURCE_NAME);
            if (ds == null) {
                log.warn("Will ignore persistence calls since no datasource was deployed for poker (poker-ds.xml).");
                available = false;
            } else {
                log.info("Poker datasource found and will be used.");
            }
        }
    }
    
}
