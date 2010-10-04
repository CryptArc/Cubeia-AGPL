package com.cubeia.games.poker.persistence.tournament;

import java.util.List;

import com.cubeia.firebase.api.service.ServiceRegistry;
import com.cubeia.games.poker.persistence.AbstractDAO;
import com.cubeia.games.poker.persistence.tournament.model.TournamentConfiguration;

public class TournamentActivatorDAO extends AbstractDAO {

    public TournamentActivatorDAO(ServiceRegistry registry) {
        super(registry);
    }

    @SuppressWarnings("unchecked")
    public List<TournamentConfiguration> readAll() {
        return em.createQuery("from TournamentConfiguration").getResultList();
    }
    
}
