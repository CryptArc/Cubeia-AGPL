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

import org.joda.time.DateTime;
import org.junit.Test;
import org.quartz.CronTrigger;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static org.quartz.CronScheduleBuilder.dailyAtHourAndMinute;
import static org.quartz.TriggerBuilder.newTrigger;

public class TournamentScheduleTest {

    @Test
    public void testNextAnnounceTime() {
        CronTrigger schedule = newTrigger().withSchedule(dailyAtHourAndMinute(14, 30))
                .startAt(new DateTime(2011, 7, 5, 9, 0, 0).toDate())
                .endAt(new DateTime(2012, 7, 5, 9, 0, 0).toDate()).build();
        TournamentSchedule tournamentSchedule = new TournamentSchedule(schedule, 10, 20, 30);

        DateTime nextAnnounceTime = tournamentSchedule.getNextAnnounceTime(new DateTime(2012, 6, 2, 9, 0, 0));
        assertEquals(2, nextAnnounceTime.getDayOfMonth());
        assertEquals(14, nextAnnounceTime.getHourOfDay());
        assertEquals(0, nextAnnounceTime.getMinuteOfHour());
    }

    @Test
    public void testNoMoreTournamentsAfterEndDate() {
        CronTrigger schedule = newTrigger().withSchedule(dailyAtHourAndMinute(14, 30))
                .startAt(new DateTime(2012, 6, 5, 9, 0, 0).toDate())
                .endAt(new DateTime(2012, 7, 5, 9, 0, 0).toDate()).build();
        TournamentSchedule tournamentSchedule = new TournamentSchedule(schedule, 10, 20, 30);

        DateTime nextAnnounceTime = tournamentSchedule.getNextAnnounceTime(new DateTime(2012, 7, 9, 9, 0, 0));
        assertNull("Should be null, but was " + nextAnnounceTime, nextAnnounceTime);
    }
}
