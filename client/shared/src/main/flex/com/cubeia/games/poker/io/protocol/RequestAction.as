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

    public class RequestAction implements ProtocolObject {
        public static const CLASSID:int = 5;

        public function classId():int {
            return RequestAction.CLASSID;
        }

        public var seq:int;
        public var player:int;
        public var allowedActions:Array = new Array();
        public var timeToAct:int;

        public function save():ByteArray
        {
            var buffer:ByteArray = new ByteArray();
            var ps:PacketOutputStream = new PacketOutputStream(buffer);
            ps.saveInt(seq);
            ps.saveInt(player);
            ps.saveInt(allowedActions.length);
            var i:int;
            for( i = 0; i != allowedActions.length; i ++)
            {
                var _tmp_allowedActions:ByteArray? = allowedActions[i].save();
                ps.saveArray(_tmp_allowedActions);
            }
            ps.saveInt(timeToAct);
            return buffer;
        }

        public function load(buffer:ByteArray):void 
        {
            var ps:PacketInputStream = new PacketInputStream(buffer);
            seq = ps.loadInt();
            player = ps.loadInt();
            var i:int;
            var allowedActionsCount:int = ps.loadInt();
            allowedActions = new Array();
            for( i = 0; i < allowedActionsCount; i ++) {
                var _tmp1:PlayerAction  = new PlayerAction();
                _tmp1.load(buffer);
                allowedActions[i] = _tmp1;
            }
            timeToAct = ps.loadInt();
        }
        

        public function toString():String
        {
            var result:String = "RequestAction :";
            result += " seq["+seq+"]" ;
            result += " player["+player+"]" ;
            result += " allowed_actions["+allowedActions+"]" ;
            result += " time_to_act["+timeToAct+"]" ;
            return result;
        }

    }
}

// I AM AUTO-GENERATED, DON'T CHECK ME INTO SUBVERSION (or else...)

