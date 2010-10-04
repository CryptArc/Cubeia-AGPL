package com.cubeia.poker.table.model
{
	import com.cubeia.games.poker.io.protocol.GameCard;
	import com.cubeia.games.poker.io.protocol.RankEnum;
	import com.cubeia.games.poker.io.protocol.SuitEnum;
	import com.cubeia.model.PokerPlayerInfo;
	
	import mx.collections.ArrayCollection;
	
	public class Player
	{
		public static const STATUS_SITOUT:String = "STATUS_SITOUT";
		public static const STATUS_WAITING_TO_ACT:String = "STATUS_WAITING_TO_ACT";
		public static const STATUS_ACTED:String = "STATUS_ACTED";
		public static const STATUS_ALLIN:String = "STATUS_ALLIN";
		
		/** Unique ID */
		public var id:int;
		
		[Bindable]
		public var screenname:String;
		
		[Bindable]
		public var status:String = STATUS_SITOUT;
		
		/** Avatar image */
		[Bindable]
		public var imageUrl:String;
		
		public var seatId:int;
		
		[Bindable]
		public var balance:Number;
		
		[Bindable]
		public var betStack:Number;
		
		[Bindable]
		public var pocketCards:ArrayCollection = new ArrayCollection(new Array(2));
		
		/** time available before timeout */
		[Bindable]
		public var timeToAct:int = 0;
		
		public static function fromPlayerInfo(info:PokerPlayerInfo):Player
		{
			var player:Player = new Player(info.id);
			player.screenname = info.name;
			player.imageUrl = info.imageUrl;
			return player;
		}
		
		public function Player(id:int)
		{
			this.id = id;
		}

		public function removePocketCards():void {
			pocketCards.setItemAt(null, 0);
			pocketCards.setItemAt(null, 1);
		}
		
	}
}