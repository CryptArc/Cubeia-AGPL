package com.cubeia.poker.event
{
	import flash.events.Event;
	import mx.rpc.remoting.RemoteObject;

	[RemoteClass(alias="PokerLobbyEvent")]
	public class PokerLobbyEvent extends PokerEvent
	{
		public static var LOGGED_IN:String = "lobby_logged_in";
		public static var VIEW_CHANGED:String = "lobby_view_changed";
	
		public static var RING_GAME:String = "RING_GAME";
		public static var SIT_AND_GO:String = "SIT_AND_GO";
		public static var TOURNAMENT:String = "TOURNAMENT";

		public static var views:Array = [RING_GAME, SIT_AND_GO, TOURNAMENT];
		
		public var newView:String;
		public var oldView:String;
		
		public function PokerLobbyEvent(type:String = "", bubbles:Boolean=false, cancelable:Boolean=false)
		{
			super(type);
		}
		
	}
}