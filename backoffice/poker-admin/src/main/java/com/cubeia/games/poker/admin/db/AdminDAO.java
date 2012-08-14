package com.cubeia.games.poker.admin.db;

import java.util.List;

import com.cubeia.games.poker.tournament.configuration.ScheduledTournamentConfiguration;
import com.cubeia.games.poker.tournament.configuration.SitAndGoConfiguration;
import com.cubeia.games.poker.tournament.configuration.TournamentConfiguration;

public interface AdminDAO {

    public abstract <T> T getItem(Class<T> class1, Integer id);

    public abstract void persist(Object entity);

    public abstract void save(Object entity);

    /**
     * Gets a list of all tournaments.
     *
     * @return null if not found
     */
    public List<SitAndGoConfiguration> getSitAndGoConfigurations();

    public List<ScheduledTournamentConfiguration> getScheduledTournamentConfigurations();

}