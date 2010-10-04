package com.cubeia.poker.table.model
{
	/**
	 * This is a seat at the table.
	 * A table can have many seats, a seat can only have one player.
	 * 
	 * NOTE: If you seat a player, you will have to unseat him before
	 * seating a new player to the same seat.
	 */
	public class Seat
	{
		/** Seat id. This is local to the table */
		public var id:int;
		
		/** The id of my table */
		public var tableId:int = -1;
		
		[Bindable]
		public var mySeat:Boolean = false;
		
		/**
		 * Don't set this directly, use seatPlayer instead!
		 */
		[Bindable]
		public var player:Player;
		
		public function Seat(id:int) {
			this.id = id;
		}
		
		public function isFree():Boolean {
			return player == null;
		}
		
		public function seatPlayer(player:Player):void {
			if (this.player == null) {
				this.player = player;
				this.player.seatId = id;
			} else if (this.player.id != player.id) {
				throw new Error("You tried to seat a player in an occupied seat. Seated["+this.player.id+"] Supplied["+player.id+"]");	
			}
		}
		
		public function unseatPlayer(player:Player):void {
			if (this.player.id == player.id) {
				player.seatId = -1;
				this.player = null;
			} else {
				throw new Error("You tried to unseat a different player. Seated["+this.player.id+"] Supplied["+player.id+"]");	
			}
		}

	}
}