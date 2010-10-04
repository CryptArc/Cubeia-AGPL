package com.cubeia.games.poker.persistence.mock;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceException;

import org.apache.log4j.Logger;

import com.cubeia.firebase.api.service.persistence.PublicPersistenceService;

public class MockPersistenceService implements PublicPersistenceService {
    
    private final transient Logger log = Logger.getLogger(this.getClass());
    
    public EntityManager getEntityManager(String name) {
        Map<String, String> conf = new HashMap<String, String>();
        conf.put("javax.persistence.transactionType", "RESOURCE_LOCAL");
        conf.put("javax.persistence.jtaDataSource", null);
        conf.put("javax.persistence.nonJtaDataSource", null);
        conf.put("hibernate.dialect", "org.hibernate.dialect.MySQL5Dialect");
        conf.put("hibernate.hbm2ddl.auto", "update");
        conf.put("hibernate.connection.url", "jdbc:mysql://localhost:3306/poker");
        conf.put("hibernate.connection.driver_class", "com.mysql.jdbc.Driver");
        conf.put("hibernate.connection.username", "root");
        conf.put("hibernate.connection.password", "root");
        
        try {
            EntityManagerFactory emf = Persistence.createEntityManagerFactory(name, conf);
            return emf.createEntityManager();
        } catch (PersistenceException e) {
            log.info(e,e);
        }
        return null;
        
    }
    
    public boolean isReady(String arg0) {
        return true;
    }

    public EntityManager getEntityManager(String name,
            boolean suppressJoinTransaction) {
        return getEntityManager(name);
    }
}
