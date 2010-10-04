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

// I AM AUTO-GENERATED, DON'T CHECK ME INTO SUBVERSION (or else...)
package com.cubeia.games.poker.io.protocol {

    import com.cubeia.firebase.io.PacketInputStream;
    import com.cubeia.firebase.io.PacketOutputStream;
    import com.cubeia.firebase.io.ProtocolObject;
  
    import flash.utils.ByteArray;

    public final class HandTypeEnum
    {
        public static const UNKNOWN:int = 0;
        public static const HIGH_CARD:int = 1;
        public static const PAIR:int = 2;
        public static const TWO_PAIR:int = 3;
        public static const THREE_OF_A_KIND:int = 4;
        public static const STRAIGHT:int = 5;
        public static const FLUSH:int = 6;
        public static const FULL_HOUSE:int = 7;
        public static const FOUR_OF_A_KIND:int = 8;
        public static const STRAIGHT_FLUSH:int = 9;

        public static function makeHandTypeEnum(value:int):int  {
            switch(value) {
                case 0: return HandTypeEnum.UNKNOWN;
                case 1: return HandTypeEnum.HIGH_CARD;
                case 2: return HandTypeEnum.PAIR;
                case 3: return HandTypeEnum.TWO_PAIR;
                case 4: return HandTypeEnum.THREE_OF_A_KIND;
                case 5: return HandTypeEnum.STRAIGHT;
                case 6: return HandTypeEnum.FLUSH;
                case 7: return HandTypeEnum.FULL_HOUSE;
                case 8: return HandTypeEnum.FOUR_OF_A_KIND;
                case 9: return HandTypeEnum.STRAIGHT_FLUSH;
            }
            return -1;
        }

}

// I AM AUTO-GENERATED, DON'T CHECK ME INTO SUBVERSION (or else...)

    }
