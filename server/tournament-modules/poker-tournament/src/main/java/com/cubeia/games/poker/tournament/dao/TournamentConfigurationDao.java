package com.cubeia.games.poker.tournament.dao;

import com.cubeia.games.poker.tournament.configuration.ScheduledTournamentConfiguration;
import com.cubeia.games.poker.tournament.configuration.SitAndGoConfiguration;
import com.google.inject.Inject;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.Collection;
import java.util.List;

public class TournamentConfigurationDao {

    protected EntityManager entityManager;

    @Inject
    public TournamentConfigurationDao(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public void saveInNewTransaction(ScheduledTournamentConfiguration object) {
        entityManager.getTransaction().begin();
        save(object);
        entityManager.getTransaction().commit();
    }

    public void save(ScheduledTournamentConfiguration object) {
        entityManager.persist(object);
    }

    public List<ScheduledTournamentConfiguration> getScheduledTournamentConfigurations() {
        return entityManager.createQuery("from ScheduledTournamentConfiguration").getResultList();
    }

    public Collection<SitAndGoConfiguration> getSitAndGoConfigurations() {
        return entityManager.createQuery("from SitAndGoConfiguration").getResultList();
    }
}