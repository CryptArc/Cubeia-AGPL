// I AM AUTO-GENERATED, DON'T CHECK ME INTO SUBVERSION (or else...)
package com.cubeia.games.poker.io.protocol {

    import com.cubeia.firebase.io.PacketInputStream;
    import com.cubeia.firebase.io.PacketOutputStream;
    import com.cubeia.firebase.io.ProtocolObject;
  
    import flash.utils.ByteArray;

    public class AamsSessionInfoPacket implements ProtocolObject {
        public static const CLASSID:int = 23;

        public function classId():int {
            return AamsSessionInfoPacket.CLASSID;
        }

        public var aamsTournamentId:String;
        public var aamsTournamentSessionId:String;

        public function save():ByteArray
        {
            var buffer:ByteArray = new ByteArray();
            var ps:PacketOutputStream = new PacketOutputStream(buffer);
            ps.saveString(aamsTournamentId);
            ps.saveString(aamsTournamentSessionId);
            return buffer;
        }

        public function load(buffer:ByteArray):void 
        {
            var ps:PacketInputStream = new PacketInputStream(buffer);
            aamsTournamentId = ps.loadString();
            aamsTournamentSessionId = ps.loadString();
        }
        

        public function toString():String
        {
            var result:String = "AamsSessionInfoPacket :";
            result += " aams_tournament_id["+aamsTournamentId+"]" ;
            result += " aams_tournament_session_id["+aamsTournamentSessionId+"]" ;
            return result;
        }

    }
}

// I AM AUTO-GENERATED, DON'T CHECK ME INTO SUBVERSION (or else...)

