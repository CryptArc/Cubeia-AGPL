package com.cubeia.poker.lobby
{
	import flash.events.Event;

	public class LobbyFilterEvent extends Event
	{
		public static const LOBBY_FILTER_EVENT:String = "lobby_filter_event";
		
		public var lobbyFilterItem:LobbyFilterItem;
		
		public function LobbyFilterEvent(item:LobbyFilterItem)
		{
			super(LOBBY_FILTER_EVENT);
			lobbyFilterItem = item;
		}
		
	}
}