package com.cubeia.games.poker.tournament.configuration.blinds;

import org.apache.log4j.Logger;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public class BlindsStructure implements Serializable {

    private long timePerLevel;

    private List<BlindsLevel> blindsLevels;

    private BlindsLevel currentLevel;

    private final Iterator<BlindsLevel> blindsLevelIterator;

    private static final Logger log = Logger.getLogger(BlindsStructure.class);

    public BlindsStructure(long millisPerLevel, List<BlindsLevel> blindsLevels) {
        checkNotNull(blindsLevels, "List of blinds levels can't be null");
        checkArgument(millisPerLevel > 0, "Time per level must be > 0");
        checkArgument(!blindsLevels.isEmpty(), "List of blinds levels can't be empty.");

        this.timePerLevel = millisPerLevel;
        this.blindsLevels = blindsLevels;
        blindsLevelIterator = blindsLevels.iterator();
        currentLevel = blindsLevelIterator.next();
    }

    public long getTimeToNextLevel() {
        return timePerLevel;
    }

    public BlindsLevel getCurrentLevel() {
        return currentLevel;
    }

    public void increaseLevel() {
        log.debug("Increasing blinds level.");
        if (blindsLevelIterator.hasNext()) {
            currentLevel = blindsLevelIterator.next();
            log.debug("Blinds level is now: " + currentLevel);
        } else {
            log.warn("No more blinds levels, staying on level " + currentLevel);
        }
    }
}
