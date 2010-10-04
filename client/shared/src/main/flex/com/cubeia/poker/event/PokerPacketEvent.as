package com.cubeia.poker.event
{
	import com.cubeia.firebase.io.ProtocolObject;
	
	public class PokerPacketEvent extends PokerEvent
	{
		public static const POKER_PACKET_EVENT:String = "poker_packet_event";
		
		public function PokerPacketEvent(type:String, _tableid:int, _protocolObject:ProtocolObject)
		{
			super(type, _tableid);
			protocolObject = _protocolObject;
		}
		
		public var protocolObject:ProtocolObject;
		
	}
}