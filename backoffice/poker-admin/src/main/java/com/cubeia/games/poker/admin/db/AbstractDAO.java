package com.cubeia.games.poker.admin.db;

import java.util.List;

import com.cubeia.games.poker.tournament.configuration.ScheduledTournamentConfiguration;
import com.cubeia.games.poker.tournament.configuration.SitAndGoConfiguration;
import org.apache.log4j.Logger;
import org.springframework.orm.jpa.support.JpaDaoSupport;
import org.springframework.transaction.annotation.Transactional;

import com.cubeia.games.poker.tournament.configuration.TournamentConfiguration;


/**
 * Base class for handling persistence.
 *
 * @author Fredrik Johansson, Cubeia Ltd
 */
@Transactional
public class AbstractDAO extends JpaDaoSupport implements AdminDAO {

    @SuppressWarnings("unused")
    private static transient Logger log = Logger.getLogger(AbstractDAO.class);

    /* (non-Javadoc)
     * @see com.cubeia.games.poker.admin.db.AdminDAO#getItem(java.lang.Class, java.lang.Integer)
     */
    @SuppressWarnings("unchecked")
    public <T> T getItem(Class<T> class1, Integer id) {
        return (T) getJpaTemplate().find(class1, id);
    }
    
    /* (non-Javadoc)
     * @see com.cubeia.games.poker.admin.db.AdminDAO#persist(java.lang.Object)
     */
    public void persist(Object entity) {
        getJpaTemplate().persist(entity);
    }

    public void save(Object entity) {
        getJpaTemplate().merge(entity);
    }

    @Override
    public List<SitAndGoConfiguration> getSitAndGoConfigurations() {
        return getJpaTemplate().find("from SitAndGoConfiguration");
    }

    @Override
    public List<ScheduledTournamentConfiguration> getScheduledTournamentConfigurations() {
        return getJpaTemplate().find("from ScheduledTournamentConfiguration");
    }


}
