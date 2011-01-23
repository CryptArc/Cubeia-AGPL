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

    public final class ActionTypeEnum
    {
        public static const SMALL_BLIND:int = 0;
        public static const BIG_BLIND:int = 1;
        public static const CALL:int = 2;
        public static const CHECK:int = 3;
        public static const BET:int = 4;
        public static const RAISE:int = 5;
        public static const FOLD:int = 6;
        public static const DECLINE_ENTRY_BET:int = 7;

        public static function makeActionTypeEnum(value:int):int  {
            switch(value) {
                case 0: return ActionTypeEnum.SMALL_BLIND;
                case 1: return ActionTypeEnum.BIG_BLIND;
                case 2: return ActionTypeEnum.CALL;
                case 3: return ActionTypeEnum.CHECK;
                case 4: return ActionTypeEnum.BET;
                case 5: return ActionTypeEnum.RAISE;
                case 6: return ActionTypeEnum.FOLD;
                case 7: return ActionTypeEnum.DECLINE_ENTRY_BET;
            }
            return -1;
        }

}

// I AM AUTO-GENERATED, DON'T CHECK ME INTO SUBVERSION (or else...)

    }
