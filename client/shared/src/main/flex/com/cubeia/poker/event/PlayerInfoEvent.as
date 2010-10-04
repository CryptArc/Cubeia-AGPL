package com.cubeia.poker.event
{
	import com.cubeia.model.PokerPlayerInfo;
	
	import flash.events.Event;
	
	import mx.rpc.remoting.RemoteObject;
	
	[RemoteClass(alias="PlayerInfoEvent")]
	public class PlayerInfoEvent extends PokerEvent
	{
		public static const PLAYER_INFO_REQUEST:String = "PLAYER_INFO_REQUEST";
		public static const PLAYER_INFO_RESPONSE:String = "PLAYER_INFO_RESPONSE";
		
		public var player:PokerPlayerInfo;
		
		public function PlayerInfoEvent(type:String = "", tableId:int = -1)
		{
			super(type, tableId);
		}
		
		public override function toString():String
		{
			var result:String = "PlayerInfoEvent :";
			result += " type["+type+"]" ;
			result += " tableId["+tableid+"]" ;
			result += " player["+player+"]" ;
			return result;
		}
	}
}