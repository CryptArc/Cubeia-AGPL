package com.cubeia.poker.event
{
	import com.cubeia.model.TableInfo;
	
	import flash.events.Event;

	public class LobbyTableEvent extends PokerEvent
	{
		public static var SELECTED:String = "table_selected";

		public var table:TableInfo;
		
		public function LobbyTableEvent(type:String, _tableid:int = -1 )
		{
			super(type, _tableid);
		}
		
	}
}