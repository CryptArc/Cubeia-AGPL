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

package com.cubeia.poker.handhistory.api;

import java.util.ArrayList;
import java.util.List;

public class ShowDownSummary extends HandHistoryEvent {

    private static final long serialVersionUID = 7280436558262252904L;

    private List<PlayerBestHand> playerBestHand = new ArrayList<PlayerBestHand>();

    public ShowDownSummary() {
    }

    public List<PlayerBestHand> getPlayerBestHand() {
        return playerBestHand;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        ShowDownSummary that = (ShowDownSummary) o;

        if (playerBestHand != null ? !playerBestHand.equals(that.playerBestHand) : that.playerBestHand != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (playerBestHand != null ? playerBestHand.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ShowDownSummary{" +
                "playerBestHand=" + playerBestHand +
                '}';
    }
}
