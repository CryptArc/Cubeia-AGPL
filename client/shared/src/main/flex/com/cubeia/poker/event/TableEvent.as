package com.cubeia.poker.event
{
	public class TableEvent extends PokerEvent
	{
		public static const JOIN_TABLE_REQUEST:String = "table_join_table";
		public static const LEAVE_TABLE_REQUEST:String = "table_leave_table";
				
		public var seatIndex:int = -1;
		
		public function TableEvent(type:String, tableId:int, seatIndex:int = -1)
		{
			super(type, tableId);
			this.seatIndex = seatIndex;
		}

	}
}