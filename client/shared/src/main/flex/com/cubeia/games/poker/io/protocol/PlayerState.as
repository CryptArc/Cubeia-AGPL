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

    public class PlayerState implements ProtocolObject {
        public static const CLASSID:int = 4;

        public function classId():int {
            return PlayerState.CLASSID;
        }

        public var player:int;
        public var openCards:Array = new Array();
        public var hiddenCards:int;
        public var balance:int;

        public function save():ByteArray
        {
            var buffer:ByteArray = new ByteArray();
            var ps:PacketOutputStream = new PacketOutputStream(buffer);
            ps.saveInt(player);
            ps.saveInt(openCards.length);
            var i:int;
            for( i = 0; i != openCards.length; i ++)
            {
                var _tmp_openCards:ByteArray? = openCards[i].save();
                ps.saveArray(_tmp_openCards);
            }
            ps.saveInt(hiddenCards);
            ps.saveInt(balance);
            return buffer;
        }

        public function load(buffer:ByteArray):void 
        {
            var ps:PacketInputStream = new PacketInputStream(buffer);
            player = ps.loadInt();
            var i:int;
            var openCardsCount:int = ps.loadInt();
            openCards = new Array();
            for( i = 0; i < openCardsCount; i ++) {
                var _tmp1:GameCard  = new GameCard();
                _tmp1.load(buffer);
                openCards[i] = _tmp1;
            }
            hiddenCards = ps.loadInt();
            balance = ps.loadInt();
        }
        

        public function toString():String
        {
            var result:String = "PlayerState :";
            result += " player["+player+"]" ;
            result += " open_cards["+openCards+"]" ;
            result += " hidden_cards["+hiddenCards+"]" ;
            result += " balance["+balance+"]" ;
            return result;
        }

    }
}

// I AM AUTO-GENERATED, DON'T CHECK ME INTO SUBVERSION (or else...)

