package com.cubeia.games.poker.admin.db;

import java.util.List;

import com.cubeia.games.poker.tournament.configuration.TournamentConfiguration;

public interface AdminDAO {

    @SuppressWarnings("unchecked")
    public abstract <T> T getItem(Class<TournamentConfiguration> class1, Integer id);

    public abstract void persist(Object entity);

    /**
     * Gets a list of all tournaments.
     *
     * @return null if not found
     */
    @SuppressWarnings("unchecked")
    public abstract List<TournamentConfiguration> getAllTournaments();

}