/**
 * Copyright (C) 2010 Cubeia Ltd <info@cubeia.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.cubeia.games.poker.tournament.configuration;

import com.cubeia.poker.timing.TimingFactory;
import com.cubeia.poker.timing.TimingProfile;

import java.io.Serializable;

public class TournamentTableSettings implements Serializable {

    private static final long serialVersionUID = 1L;

    private TimingProfile timingProfile = TimingFactory.getRegistry().getDefaultTimingProfile();

    private int anteAmount;
    private int smallBlindAmount;
    private int bigBlindAmount;

    public TournamentTableSettings(int smallBlindAmount, int bigBlindAmount) {
        this.smallBlindAmount = smallBlindAmount;
        this.bigBlindAmount = bigBlindAmount; 
    }

    public TournamentTableSettings(int anteAmount) {
        this.anteAmount = anteAmount;
    }

    public TimingProfile getTimingProfile() {
        return timingProfile;
    }

    public void setTimingProfile(TimingProfile timingProfile) {
        this.timingProfile = timingProfile;
    }

    public int getAnteAmount() {
        return anteAmount;
    }

    public int getSmallBlindAmount() {
        return smallBlindAmount;
    }

    public int getBigBlindAmount() {
        return bigBlindAmount;
    }

    public void setSmallBlindAmount(int smallBlindAmount) {
        this.smallBlindAmount = smallBlindAmount;
    }

    public void setBigBlindAmount(int bigBlindAmount) {
        this.bigBlindAmount = bigBlindAmount;
    }
}
