package com.cubeia.model
{
	[RemoteClass(alias="PokerPlayerInfo")]
	public class PokerPlayerInfo
	{
		[Bindable]
		public var id:int;
		
		[Bindable]
		public var name:String;
		
		[Bindable]
		public var imageUrl:String;
		
	}
}