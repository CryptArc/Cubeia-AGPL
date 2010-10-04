package com.cubeia.poker.lobby.component.table
{
	import flash.events.Event;

	public class OpenTableEvent extends Event
	{
		public static const OPEN_TABLE_EVENT:String = "open_table_event";
		
		public function OpenTableEvent()
		{
			super(OPEN_TABLE_EVENT); 
		}
		
	}
}