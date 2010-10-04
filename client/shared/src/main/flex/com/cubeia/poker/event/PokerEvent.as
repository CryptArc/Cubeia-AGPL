package com.cubeia.poker.event
{
	import flash.events.Event;

	public class PokerEvent extends Event
	{
		public var tableid:int;
		
		public function PokerEvent(type:String, _tableid:int = -1)
		{
			super(type);
			tableid = _tableid;
		}
		
	}
}