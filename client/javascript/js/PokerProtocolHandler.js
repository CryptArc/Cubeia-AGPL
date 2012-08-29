var POKER_PROTOCOL = POKER_PROTOCOL || {};

/**
 * Construct a poker protocol object handler
 * @constructor
 * @param {com.cubeia.games.poker.PokerGameHandler} pokerGameHandler
 */
POKER_PROTOCOL.PokerProtocolHandler = function(pokerGameHandler) {

	this.pokerGameHandler = pokerGameHandler;

	this.handleGameTransportPacket = function(gameTransportPacket) {

		var valueArray =  FIREBASE.ByteArray.fromBase64String(gameTransportPacket.gamedata);
		var gameData = new FIREBASE.ByteArray(valueArray);
		var length = gameData.readInt();
		var classId = gameData.readUnsignedByte();
		
		console.log("received protocolObject - classId=" + classId);
		
		var protocolObject = POKER_PROTOCOL.ProtocolObjectFactory.create(classId, gameData);
		
		//console.log(protocolObject);
		
		switch ( protocolObject.classId() ) {
			case POKER_PROTOCOL.BestHand.CLASSID:
				this.pokerGameHandler.handleBestHand(protocolObject);
				break;
			case POKER_PROTOCOL.BuyInInfoRequest.CLASSID:
				this.pokerGameHandler.handleBuyInInfoRequest(protocolObject);
				break;
			case POKER_PROTOCOL.BuyInInfoResponse.CLASSID:
				this.pokerGameHandler.handleBuyInInfoResponse(protocolObject);
				break;
			case POKER_PROTOCOL.BuyInResponse.CLASSID:
				this.pokerGameHandler.handleBuyInResponse(protocolObject);
				break;
			case POKER_PROTOCOL.CardToDeal.CLASSID:
				this.pokerGameHandler.handleCardToDeal(protocolObject);
				break;
			case POKER_PROTOCOL.DealerButton.CLASSID:
				this.pokerGameHandler.handleDealerButton(protocolObject);
				break;
			case POKER_PROTOCOL.DealPrivateCards.CLASSID:
				this.pokerGameHandler.handleDealPrivateCards(protocolObject);
				break;
			case POKER_PROTOCOL.DealPublicCards.CLASSID:
				this.pokerGameHandler.handleDealPublicCards(protocolObject);
				break;
			case POKER_PROTOCOL.DeckInfo.CLASSID:
				this.pokerGameHandler.handleDeckInfo(protocolObject);
				break;
			case POKER_PROTOCOL.ErrorPacket.CLASSID:
				this.pokerGameHandler.handleErrorPacket(protocolObject);
				break;
			case POKER_PROTOCOL.ExposePrivateCards.CLASSID:
				this.pokerGameHandler.handleExposePrivateCards(protocolObject);
				break;
			case POKER_PROTOCOL.ExternalSessionInfoPacket.CLASSID:
				this.pokerGameHandler.handleExternalSessionInfoPacket(protocolObject);
				break;
			case POKER_PROTOCOL.FuturePlayerAction.CLASSID:
				this.pokerGameHandler.handleFuturePlayerAction(protocolObject);
				break;
			case POKER_PROTOCOL.GameCard.CLASSID:
				this.pokerGameHandler.handleGameCard(protocolObject);
				break;
			case POKER_PROTOCOL.HandCanceled.CLASSID:
				this.pokerGameHandler.handleHandCanceled(protocolObject);
				break;
			case POKER_PROTOCOL.HandEnd.CLASSID:
				this.pokerGameHandler.handleHandEnd(protocolObject);
				break;
			case POKER_PROTOCOL.InformFutureAllowedActions.CLASSID:
				this.pokerGameHandler.handleInformFutureAllowedActions(protocolObject);
				break;
			case POKER_PROTOCOL.PerformAction.CLASSID:
				this.pokerGameHandler.handlePerformAction(protocolObject);
				break;
			case POKER_PROTOCOL.PingPacket.CLASSID:
				this.pokerGameHandler.handlePingPacket(protocolObject);
				break;
			case POKER_PROTOCOL.PlayerAction.CLASSID:
				this.pokerGameHandler.handlePlayerAction(protocolObject);
				break;
			case POKER_PROTOCOL.PlayerBalance.CLASSID:
				this.pokerGameHandler.handlePlayerBalance(protocolObject);
				break;
			case POKER_PROTOCOL.PlayerDisconnectedPacket.CLASSID:
				this.pokerGameHandler.handlePlayerDisconnectedPacket(protocolObject);
				break;
			case POKER_PROTOCOL.PlayerHandStartStatus.CLASSID:
				this.pokerGameHandler.handlePlayerHandStartStatus(protocolObject);
				break;
			case POKER_PROTOCOL.PlayerPokerStatus.CLASSID:
				this.pokerGameHandler.handlePlayerPokerStatus(protocolObject);
				break;
			case POKER_PROTOCOL.PlayerReconnectedPacket.CLASSID:
				this.pokerGameHandler.handlePlayerReconnectedPacket(protocolObject);
				break;
			case POKER_PROTOCOL.PlayerState.CLASSID:
				this.pokerGameHandler.handlePlayerState(protocolObject);
				break;
			case POKER_PROTOCOL.PingPacket.CLASSID:
				this.pokerGameHandler.handlePingPacket(protocolObject);
				break;
			case POKER_PROTOCOL.PongPacket.CLASSID:
				this.pokerGameHandler.handlePongPacket(protocolObject);
				break;
            case POKER_PROTOCOL.PotTransfers.CLASSID:
                this.pokerGameHandler.handlePotTransfers(protocolObject);
                break;
			case POKER_PROTOCOL.RakeInfo.CLASSID:
				this.pokerGameHandler.handleRakeInfo(protocolObject);
				break;
			case POKER_PROTOCOL.RequestAction.CLASSID:
				this.pokerGameHandler.handleRequestAction(protocolObject);
				break;
			case POKER_PROTOCOL.StartHandHistory.CLASSID:
				this.pokerGameHandler.handleStartHandHistory(protocolObject);
				break;
			case POKER_PROTOCOL.StartNewHand.CLASSID:
				this.pokerGameHandler.handleStartNewHand(protocolObject);
				break;
			case POKER_PROTOCOL.StopHandHistory.CLASSID:
				this.pokerGameHandler.handleStopHandHistory(protocolObject);
				break;
			case POKER_PROTOCOL.TakeBackUncalledBet.CLASSID:
				this.pokerGameHandler.handleTakeBackUncalledBet(protocolObject);
				break;
			case POKER_PROTOCOL.TournamentOut.CLASSID:
				this.pokerGameHandler.handleTournamentOut(protocolObject);
				break;
            default:
                console.log("Ignoring packet: " + protocolObject);
                break;
		}
	};
};