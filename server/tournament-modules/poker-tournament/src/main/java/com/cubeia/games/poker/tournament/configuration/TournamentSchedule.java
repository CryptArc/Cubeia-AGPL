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

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.quartz.Trigger;

import java.util.Date;

public class TournamentSchedule {

    private static final Logger log = Logger.getLogger(TournamentSchedule.class);

    private Trigger schedule;

    private int minutesInAnnounced;

    private int minutesInRegistering;

    private int minutesVisibleAfterFinished;

    public TournamentSchedule(Trigger schedule, int minutesInAnnounced, int minutesInRegistering, int minutesVisibleAfterFinished) {
        log.debug("Created tournament schedule. Start date: " + schedule.getStartTime() + " End date: " + schedule.getEndTime());
        this.schedule = schedule;
        this.minutesInAnnounced = minutesInAnnounced;
        this.minutesInRegistering = minutesInRegistering;
        this.minutesVisibleAfterFinished = minutesVisibleAfterFinished;
    }

    public int getMinutesInAnnounced() {
        return minutesInAnnounced;
    }

    public int getMinutesInRegistering() {
        return minutesInRegistering;
    }

    public int getMinutesVisibleAfterFinished() {
        return minutesVisibleAfterFinished;
    }

    public DateTime getNextAnnounceTime(DateTime now) {
        log.debug("Getting next announce time after " + now + ". Start date = " + schedule.getStartTime());
        DateTime nextStartTime = getNextStartTime(now);
        log.debug("Next startTime: " + nextStartTime);
        if (nextStartTime == null) {
            return null;
        } else {
            DateTime nextAnnounceTime = new DateTime(nextStartTime).minusMinutes(minutesInRegistering).minusMinutes(minutesInAnnounced);
            log.debug("Next announce time: " + nextAnnounceTime);
            return nextAnnounceTime;
        }
    }

    public DateTime getNextStartTime(DateTime now) {
        Date nextStartTime = schedule.getFireTimeAfter(now.toDate());
        if (nextStartTime == null) {
            return null;
        } else {
            return new DateTime(nextStartTime);
        }
    }
}
