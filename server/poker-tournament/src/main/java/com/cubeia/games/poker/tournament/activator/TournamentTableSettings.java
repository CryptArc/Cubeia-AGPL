package com.cubeia.games.poker.tournament.activator;

import java.io.Serializable;

import com.cubeia.poker.timing.TimingFactory;
import com.cubeia.poker.timing.TimingProfile;

public class TournamentTableSettings implements Serializable {
    
    /** version id */
    private static final long serialVersionUID = 1L;
    
    private TimingProfile timingProfile = TimingFactory.getRegistry().getDefaultTimingProfile();

    public TimingProfile getTimingProfile() {
        return timingProfile;
    }

    public void setTimingProfile(TimingProfile timingProfile) {
        this.timingProfile = timingProfile;
    }
    
}
