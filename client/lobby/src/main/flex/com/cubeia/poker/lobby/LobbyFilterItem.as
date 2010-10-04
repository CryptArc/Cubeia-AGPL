package com.cubeia.poker.lobby
{
	import mx.controls.CheckBox;

	public class LobbyFilterItem extends CheckBox
	{
		[Inspectable]
		public var gameId:int;

		[Inspectable]
		public var lobbyPath:String;
		
		[Inspectable]
		public var clientFilter:Boolean = false;
		
		[Inspectable]
		public var property1:String;
		
		[Inspectable]
		public var property2:String;
		
		[Inspectable]
		public var operator:String;	
		
		[Inspectable]
		public var value:String;	
		
		[Inspectable]
		public var basePath:String;
		
		[Inspectable]
		public var tableType:String;
		
		[Inspectable]
		public var invert:Boolean;
		
		public function LobbyFilterItem()
		{
			selected = true;
			super();
		}
	}
}