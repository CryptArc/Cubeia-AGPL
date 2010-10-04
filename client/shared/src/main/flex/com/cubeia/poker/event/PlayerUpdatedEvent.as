package com.cubeia.poker.event
{
	import com.cubeia.model.PokerPlayerInfo;
	
	import flash.events.Event;

	public class PlayerUpdatedEvent extends PokerEvent
	{
		public static const PLAYER_UPDATE_EVENT:String = "player_updated_event";
		
		public var player:PokerPlayerInfo;
		
		/**
		 * 
		 * Dispatch: Should this even propagate over the local connection? Defaults to true
		 */
		public function PlayerUpdatedEvent(_player:PokerPlayerInfo)
		{
			player = _player
			super(PLAYER_UPDATE_EVENT);
		}
		
	}
}