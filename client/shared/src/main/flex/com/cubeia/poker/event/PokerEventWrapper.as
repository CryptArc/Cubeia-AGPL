package com.cubeia.poker.event
{
	import flash.events.Event;

	/**
	 * Wraps an PokerEvent for distribution over the message bus
	 * This event should be contained within the message bus
	 */
	public class PokerEventWrapper extends Event
	{
		public static const POKER_EVENT_WRAPPER:String = "poker_event_wrapper";
	
		public var pokerEvent:PokerEvent; 
		 
		public function PokerEventWrapper(_pokerEvent:PokerEvent)
		{
			pokerEvent = _pokerEvent;
			super(POKER_EVENT_WRAPPER);
		}
		
	}
}