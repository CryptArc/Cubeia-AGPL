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

package com.cubeia.poker.tournament.history.api;

import com.google.code.morphia.annotations.Embedded;

import java.io.Serializable;
import java.math.BigDecimal;

@Embedded
public class PlayerPosition implements Serializable {
    private int playerId;
    private int position;
    private BigDecimal payoutInCents;

    public PlayerPosition() {
    }

    public PlayerPosition(int playerId, int position, BigDecimal payoutInCents) {
        this.playerId = playerId;
        this.position = position;
        this.payoutInCents = payoutInCents;
    }

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public BigDecimal getPayoutInCents() {
        return payoutInCents;
    }

    public void setPayoutInCents(BigDecimal payoutInCents) {
        this.payoutInCents = payoutInCents;
    }
}
