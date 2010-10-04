package com.cubeia.multitable.events
{
	import flash.events.Event;
	import flash.utils.ByteArray;
	
	public class GamePacketDataEvent extends Event
	{
		public static const GAME_PACKET_DATA_EVENT:String = "game_packet_data_event";
		
		public var packetData:ByteArray;
		
		public function GamePacketDataEvent(_packetData:ByteArray)
		{
			super(GAME_PACKET_DATA_EVENT);
			packetData = _packetData; 
		}
	}
}