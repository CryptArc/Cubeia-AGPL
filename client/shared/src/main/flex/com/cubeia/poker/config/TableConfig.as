package com.cubeia.poker.config
{
	public class TableConfig
	{
		public var numberOfSeats:int;
		
		public var seatConfig:Array;
		
		public function TableConfig(_numberOfSeats:int)
		{
			numberOfSeats = _numberOfSeats;
			seatConfig = new Array(numberOfSeats);
		}
		
		public function addSeatConfig(pos:int, _seatConfig:SeatConfig):void
		{
			seatConfig[pos] = _seatConfig;
		}

		public function getSeatConfig(pos:int):SeatConfig
		{
			return seatConfig[pos];
		}

	}
	
}