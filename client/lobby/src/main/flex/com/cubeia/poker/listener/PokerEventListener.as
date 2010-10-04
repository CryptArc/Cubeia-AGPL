package com.cubeia.poker.listener
{
	import com.cubeia.firebase.events.PacketEvent;
	import com.cubeia.firebase.io.protocol.SeatInfoPacket;
	import com.cubeia.firebase.io.protocol.TableQueryResponsePacket;
	import com.cubeia.poker.event.PokerEventDispatcher;
	import com.cubeia.poker.event.TableEvent;
	import com.cubeia.util.players.PlayerRegistry;
	
	/**
	 * Listener for local and global Poker Events
	 */
	public class PokerEventListener
	{
		public function PokerEventListener()
		{
			// PokerLobby.firebaseClient.addEventListener(PacketEvent.PACKET_RECEIVED, onFirebasePacket);
		}

		
		public function onFirebasePacket(event:PacketEvent):void {
			if (event.getObject().classId() == TableQueryResponsePacket.CLASSID) {
				handleTableQueryResponse(event);
			}
		}
		
		// Not used ATM
		private function handleTableQueryResponse(event:PacketEvent):void {
			var packet:TableQueryResponsePacket = event.getObject() as TableQueryResponsePacket;
			for each (var seat:SeatInfoPacket in packet.seats) {
				PlayerRegistry.instance.addPlayer(seat.player);
			}
		}
	}
}