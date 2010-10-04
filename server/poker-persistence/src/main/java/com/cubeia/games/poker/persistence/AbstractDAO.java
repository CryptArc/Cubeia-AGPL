package com.cubeia.games.poker.persistence;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.apache.log4j.Logger;

import com.cubeia.firebase.api.service.ServiceRegistry;
import com.cubeia.firebase.api.service.persistence.PublicPersistenceService;

/**
 * Base class for handling persistence.
 *
 * @author Fredrik Johansson, Cubeia Ltd
 */
public class AbstractDAO {

    private static transient Logger log = Logger.getLogger(AbstractDAO.class);
    
    protected static boolean persist = true; 
    
    protected static Map<String, Boolean> persistCustom = new ConcurrentHashMap<String, Boolean>();
    
    protected EntityManager em;

    /**
     * Will use EntityManager named 'poker'.
     * @param registry
     */
    public AbstractDAO(ServiceRegistry registry) {
        if (persist) {
            PublicPersistenceService service = registry.getServiceInstance(PublicPersistenceService.class);
            em = service.getEntityManager("poker");
            if (em == null) {
                log.info("Will ignore persistence calls since no entity-manager was found for 'poker'.");
                persist = false;
            }
        }
    }
    
    /**
     * Will use specified EntityManager.
     * 
     * @param registry
     * @param entityManager
     */
    public AbstractDAO(ServiceRegistry registry, String entityManager) {
        if (!persistCustom.containsKey(entityManager) || persistCustom.get(entityManager).booleanValue()) {
            PublicPersistenceService service = registry.getServiceInstance(PublicPersistenceService.class);
            em = service.getEntityManager(entityManager);
            if (em == null) {
                log.info("Will ignore persistence calls since no entity-manager was found for '"+entityManager+"'.");
                persistCustom.put(entityManager, new Boolean(false));
            }
        }
    }

    public void persist(Object entity) {
        if (em != null) {
            EntityTransaction tx = null;
            try {
                tx = em.getTransaction();
                tx.begin();
                log.debug("OMG OMG OMG !!! Persist: "+entity);
                em.persist(entity);
                
                tx.commit();
                
            } catch (RuntimeException e) {
                if ( tx != null && tx.isActive() ) tx.rollback();
                throw e; // or display error message
                
            }  finally {
                em.close();
            }
        }
    }

}
