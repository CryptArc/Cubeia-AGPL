package com.cubeia.poker.table.cards
{
	import com.cubeia.games.poker.io.protocol.GameCard;
	
	public class WrappedGameCard extends GameCard
	{
		public var hidden:Boolean;
		
		public function WrappedGameCard(card:GameCard, _hidden:Boolean = false)
		{
			if ( card != null ) {
				rank = card.rank;
				suit = card.suit;
			}
			hidden = _hidden;
			super();
		}
	}
}