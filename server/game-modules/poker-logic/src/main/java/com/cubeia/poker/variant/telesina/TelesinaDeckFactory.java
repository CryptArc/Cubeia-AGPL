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

package com.cubeia.poker.variant.telesina;

import java.util.Random;

public class TelesinaDeckFactory {

    public TelesinaDeck createNewDeck(Random rng, int tableSize) {
        return new TelesinaDeck(new TelesinaDeckUtil(), rng, tableSize);
    }

    //TODO: remove this code once GLI has used the rig deck feature
    public TelesinaDeck createNewRiggedDeck(int tableSize, String riggedDeck) {
        return new TelesinaRiggedDeck(new TelesinaDeckUtil(), tableSize, riggedDeck);
    }

}
