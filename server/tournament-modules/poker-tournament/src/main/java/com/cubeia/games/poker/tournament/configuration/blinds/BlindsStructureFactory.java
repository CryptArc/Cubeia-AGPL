package com.cubeia.games.poker.tournament.configuration.blinds;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

public class BlindsStructureFactory {

    public static BlindsStructure createDefaultBlindsStructure() {
        List<BlindsLevel> blindsLevelList = createBlindsLevels();
        return new BlindsStructure(60 * 1000, blindsLevelList);
    }

    private static List<BlindsLevel> createBlindsLevels() {
        List<BlindsLevel> levels = newArrayList();
        int smallBlind = 10;
        int bigBlind = 20;
        int ante = 0;
        for (int i = 0; i < 20; i++) {
            levels.add(new BlindsLevel(smallBlind, bigBlind, ante));
            smallBlind *= 2;
            bigBlind *= 2;
        }
        return levels;
    }
}
