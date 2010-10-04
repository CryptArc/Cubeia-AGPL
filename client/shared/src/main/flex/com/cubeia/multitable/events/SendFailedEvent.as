package com.cubeia.multitable.events
{
	import flash.events.Event;

	public class SendFailedEvent extends Event
	{
		public static const SEND_FAILED_EVENT:String = "send_failed_event";
		
		public function SendFailedEvent(_tableid:int)
		{
			tableid = _tableid;
			super(SEND_FAILED_EVENT);
		}

		public var tableid:int;
		
	}
}