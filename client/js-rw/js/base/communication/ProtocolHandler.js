"use strict";
var Poker = Poker || {};

/**
 * Construct a poker protocol object handler
 * @constructor
 * @param {Poker.TableManager} tableManager
 */
Poker.PokerProtocolHandler = function(tableManager,tableComHandler) {

	this.tableManager = tableManager;
    this.tableComHandler = tableComHandler;
    this.seq = -1;
    this.packetCount = 0;
	this.handleGameTransportPacket = function(gameTransportPacket) {

		var valueArray =  FIREBASE.ByteArray.fromBase64String(gameTransportPacket.gamedata);
		var gameData = new FIREBASE.ByteArray(valueArray);
		var length = gameData.readInt();
		var classId = gameData.readUnsignedByte();

		//console.log("received protocolObject - classId=" + classId);

		var protocolObject = com.cubeia.games.poker.io.protocol.ProtocolObjectFactory.create(classId, gameData);
		
		switch (protocolObject.classId() ) {
			case com.cubeia.games.poker.io.protocol.BestHand.CLASSID:
                console.log("UNHANDLED PO BestHand");
                console.log(protocolObject);
				break;
			case com.cubeia.games.poker.io.protocol.BuyInInfoRequest.CLASSID:
                console.log("UNHANDLED PO BuyInInfoRequest");
                console.log(protocolObject);
				break;
			case com.cubeia.games.poker.io.protocol.BuyInInfoResponse.CLASSID:
                //TODO let the user input the buy-in amount
              this.handleBuyIn(protocolObject);

				break;
			case com.cubeia.games.poker.io.protocol.BuyInResponse.CLASSID:
                console.log("UNHANDLED PO BuyInResponse");
				console.log(protocolObject);
				break;
			case com.cubeia.games.poker.io.protocol.CardToDeal.CLASSID:
                console.log("UNHANDLED PO CardToDeal");
				console.log(protocolObject);
				break;
			case com.cubeia.games.poker.io.protocol.DealerButton.CLASSID:
                this.tableManager.setDealerButton(protocolObject.seat);
				break;
			case com.cubeia.games.poker.io.protocol.DealPrivateCards.CLASSID:
                var cardsToDeal = protocolObject.cards;
                for(var c in cardsToDeal) {
                    var cardString = Poker.Utils.getCardString(cardsToDeal[c].card);
                    this.tableManager.dealPlayerCard(cardsToDeal[c].player,cardsToDeal[c].card.cardId,cardString);
                }

				break;
			case com.cubeia.games.poker.io.protocol.DealPublicCards.CLASSID:
                for ( var i = 0; i < protocolObject.cards.length; i ++ ) {
                    this.tableManager.dealCommunityCard(protocolObject.cards[i].cardId,
                        Poker.Utils.getCardString(protocolObject.cards[i]));
                }
                break;
			case com.cubeia.games.poker.io.protocol.DeckInfo.CLASSID:
                console.log("UNHANDLED PO DeckInfo");
                console.log(protocolObject);
				break;
			case com.cubeia.games.poker.io.protocol.ErrorPacket.CLASSID:
                console.log("UNHANDLED PO ErrorPacket");
                console.log(protocolObject);
				break;
			case com.cubeia.games.poker.io.protocol.ExposePrivateCards.CLASSID:
                for ( var i = 0; i < protocolObject.cards.length; i ++ ) {
                    this.tableManager.exposePrivateCard(protocolObject.cards[i].card.cardId,
                        Poker.Utils.getCardString(protocolObject.cards[i].card));
                }

                break;
            case com.cubeia.games.poker.io.protocol.ExternalSessionInfoPacket.CLASSID:
                console.log("UNHANDLED PO ExternalSessionInfoPacket");
                console.log(protocolObject);
				break;
			case com.cubeia.games.poker.io.protocol.FuturePlayerAction.CLASSID:
                console.log("UNHANDLED PO FuturePlayerAction");
                console.log(protocolObject);
				break;
			case com.cubeia.games.poker.io.protocol.GameCard.CLASSID:
                console.log("UNHANDLED PO GameCard");
                console.log(protocolObject);
				break;
			case com.cubeia.games.poker.io.protocol.HandCanceled.CLASSID:
                console.log("UNHANDLED PO HandCanceled");
                console.log(protocolObject);
				break;
			case com.cubeia.games.poker.io.protocol.HandEnd.CLASSID:
                this.tableManager.endHand(protocolObject.hands,protocolObject.potTransfers);

				break;
			case com.cubeia.games.poker.io.protocol.InformFutureAllowedActions.CLASSID:
                console.log("UNHANDLED PO InformFutureAllowedActions");
                console.log(protocolObject);
				break;
			case com.cubeia.games.poker.io.protocol.PerformAction.CLASSID:
				this.handlePerformAction(protocolObject);
				break;
			case com.cubeia.games.poker.io.protocol.PingPacket.CLASSID:
                console.log("UNHANDLED PO PingPacket");
                console.log(protocolObject);
				break;
			case com.cubeia.games.poker.io.protocol.PlayerAction.CLASSID:
                console.log("UNHANDLED PO PlayerAction");
                console.log(protocolObject);
				break;
			case com.cubeia.games.poker.io.protocol.PlayerBalance.CLASSID:
                this.tableManager.updatePlayerBalance(
                    protocolObject.player,
                    Poker.Utils.formatCurrency(protocolObject.balance)
                    );
    			break;
			case com.cubeia.games.poker.io.protocol.PlayerDisconnectedPacket.CLASSID:
                console.log("UNHANDLED PO PlayerDisconnectedPacket");
                console.log(protocolObject);
				break;
			case com.cubeia.games.poker.io.protocol.PlayerHandStartStatus.CLASSID:
                var status = Poker.PlayerStatus.SITTING_OUT;
                if(protocolObject.status == com.cubeia.games.poker.io.protocol.PlayerTableStatusEnum.SITIN){
                    status = Poker.PlayerStatus.SITTING_IN;
                }
                this.tableManager.updatePlayerStatus(protocolObject.player, status);
                break;
			case com.cubeia.games.poker.io.protocol.PlayerPokerStatus.CLASSID:
                var status = protocolObject.status;
                switch (status) {
                    case com.cubeia.games.poker.io.protocol.PlayerTableStatusEnum.SITIN :
                        this.tableManager.updatePlayerStatus(protocolObject.player, Poker.PlayerStatus.SITTING_IN);
                        break;
                    case com.cubeia.games.poker.io.protocol.PlayerTableStatusEnum.SITOUT :
                        this.tableManager.updatePlayerStatus(protocolObject.player, Poker.PlayerStatus.SITTING_OUT);
                        break;
                }
				break;
			case com.cubeia.games.poker.io.protocol.PlayerReconnectedPacket.CLASSID:
                console.log("UNHANDLED PO PlayerReconnectedPacket");
                console.log(protocolObject);
				break;
			case com.cubeia.games.poker.io.protocol.PlayerState.CLASSID:
                console.log("UNHANDLED PO PlayerState");
                console.log(protocolObject);
				break;
			case com.cubeia.games.poker.io.protocol.PongPacket.CLASSID:
                console.log("UNHANDLED PO PongPacket");
                console.log(protocolObject);
				break;
            case com.cubeia.games.poker.io.protocol.PotTransfers.CLASSID:

                var pots = [];
                for(var i in protocolObject.pots) {
                    var p = protocolObject.pots[i];
                    var type = Poker.PotType.MAIN;
                    if(com.cubeia.games.poker.io.protocol.PotTypeEnum.SIDE == p.type) {
                        type = Poker.PotType.SIDE;
                    }
                    pots.push(new Poker.Pot(p.id,type, Poker.Utils.formatCurrency(p.amount)));
                }
                if(pots.length>0) {
                    this.tableManager.updatePots(pots);
                }
                break;
			case com.cubeia.games.poker.io.protocol.RakeInfo.CLASSID:
                console.log("UNHANDLED PO RakeInfo");
                console.log(protocolObject);
				break;
			case com.cubeia.games.poker.io.protocol.RequestAction.CLASSID:
				this.handleRequestAction(protocolObject);
				break;
			case com.cubeia.games.poker.io.protocol.StartHandHistory.CLASSID:
                console.log("UNHANDLED PO StartHandHistory");
                console.log(protocolObject);
				break;
			case com.cubeia.games.poker.io.protocol.StartNewHand.CLASSID:
				this.tableManager.startNewHand(protocolObject.handId,protocolObject.dealerSeatId);
				break;
			case com.cubeia.games.poker.io.protocol.StopHandHistory.CLASSID:
                console.log("UNHANDLED PO StopHandHistory");
                console.log(protocolObject);
				break;
			case com.cubeia.games.poker.io.protocol.TakeBackUncalledBet.CLASSID:
                console.log("UNHANDLED PO TakeBackUncalledBet");
                console.log(protocolObject);
				break;
			case com.cubeia.games.poker.io.protocol.TournamentOut.CLASSID:
                console.log("UNHANDLED PO TournamentOut");
                console.log(protocolObject);
				break;
            default:
                console.log("Ignoring packet: " + protocolObject);
                break;
		}
	};
    this.handleBuyIn = function(protocolObject) {
        var buyInRequest = new com.cubeia.games.poker.io.protocol.BuyInRequest();
        buyInRequest.amount = protocolObject.maxAmount;
        console.log("buying in = " + protocolObject.maxAmount);
        buyInRequest.sitInIfSuccessful = true;
        this.tableComHandler.sendGameTransportPacket(buyInRequest);
    };
    this.handlePerformAction = function(performAction){
        var actionType = this.getActionType(performAction.action.type);

        var amount = 0;
        if(performAction.betAmount) {
            amount = Poker.Utils.formatCurrency(performAction.betAmount);
        }
        this.tableManager.handlePlayerAction(performAction.player,actionType,amount);
    };
    this.getActionType = function(actType){
        var type = null;
        switch (actType) {
            case com.cubeia.games.poker.io.protocol.ActionTypeEnum.CHECK:
                type = Poker.ActionType.CHECK;
                break;
            case com.cubeia.games.poker.io.protocol.ActionTypeEnum.CALL:
                type = Poker.ActionType.CALL;
                break;
            case com.cubeia.games.poker.io.protocol.ActionTypeEnum.BET:
                type = Poker.ActionType.BET;
                break;
            case com.cubeia.games.poker.io.protocol.ActionTypeEnum.RAISE:
                type = Poker.ActionType.RAISE;
                break;
            case com.cubeia.games.poker.io.protocol.ActionTypeEnum.FOLD:
                type = Poker.ActionType.FOLD;
                break;
            case com.cubeia.games.poker.io.protocol.ActionTypeEnum.SMALL_BLIND:
                type = Poker.ActionType.SMALL_BLIND;
                break;
            case com.cubeia.games.poker.io.protocol.ActionTypeEnum.BIG_BLIND:
                type = Poker.ActionType.BIG_BLIND;
                break;
            default:
                console.log("Unhandled ActionTypeEnum " + actType);
                break;
        }
        return type;
    };
    this.getAction = function(act) {
        var action = null;
        var type = this.getActionType(act.type);

        return new Poker.Action(type,act.minAmount, act.maxAmount);
    };

    this.getPokerActions = function(allowedActions){
        var actions = [];
        for(var a in allowedActions) {
            actions.push(this.getAction(allowedActions[a]));
        }
        return actions;
    };
    this.handleRequestAction = function(requestAction) {

        this.tableManager.updateMainPot(Poker.Utils.formatCurrency(requestAction.currentPotSize));
        this.seq = requestAction.seq;
        var acts = this.getPokerActions(requestAction.allowedActions);

        if(acts.length>0 && (acts[0].type == Poker.ActionType.BIG_BLIND || acts[0].type == Poker.ActionType.SMALL_BLIND)) {
            //for now auto post blinds
            console.log("Auto posting " + acts[0].type.text);
            this.tableComHandler.sendAction(requestAction.seq, requestAction.allowedActions[0].type, requestAction.allowedActions[0].minAmount);
            return;
        }
        this.tableManager.handleRequestPlayerAction(
            requestAction.player,
            acts,
            requestAction.timeToAct);

    };
};