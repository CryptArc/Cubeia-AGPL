"use strict";
var Poker = Poker || {};

Poker.TableManager = Class.extend({
    table : null,
    tableListeners : [],
    handCount: 0,
    dealerSeatId : -1,
    mainPot : 0,
    init : function() {

    },
    createTable : function(tableId,capacity,name, tableListeners) {
        this.table = new Poker.Table(tableId,capacity,name);

        this.tableListeners = [];
        if(tableListeners) {
            for(var x in tableListeners)   {
                this.addTableListener(tableListeners[x]);
                tableListeners[x].onTableCreated();
            }
        }
        console.log("Creating table with listeners = " + this.tableListeners.length);
        console.log(this.tableListeners);
    },
    removeEventListener : function() {
      this.tableListeners = [];
    },
    handleBuyInResponse : function(status) {
        if(status == com.cubeia.games.poker.io.protocol.BuyInResultCodeEnum.PENDING) {
            for(var l in this.tableListeners) {
                this.tableListeners[l].onBuyInCompleted();
            }
        } else if(status != com.cubeia.games.poker.io.protocol.BuyInResultCodeEnum.OK){
            this.handleBuyInError(status);
        }
    },
    handleBuyInError : function(status) {
        for(var l in this.tableListeners) {
            this.tableListeners[l].onBuyInError("Unable to buy in");
        }
    },
    handleBuyInInfo : function(balanceInWallet, balanceOnTable, maxAmount, minAmount,mandatory) {
        for(var l in this.tableListeners) {
            this.tableListeners[l].onBuyInInfo(this.table.name,balanceInWallet,balanceOnTable,maxAmount,minAmount,mandatory);
        }
    },
    getTable : function() {
        return this.table;
    },
    getTableId : function() {
      return this.table.id;
    },
    startNewHand : function(handId, dealerSeatId) {
        this.handCount++;
        this.dealerSeatId = dealerSeatId;
        this._notifyNewHand(dealerSeatId);
    },
    endHand : function(hands,potTransfers) {
        for (var hand in hands) {
            this.updateHandStrength(hands[hand]);
        }
        console.log("pot transfers:");
        console.log(potTransfers);
        var count = this.handCount;
        var self = this;

        if(potTransfers.fromPlayerToPot === false ){
            this._notifyPotToPlayerTransfer(potTransfers.transfers);
        }

        setTimeout(function(){
            //if no new hand has started in the next 15 secs we clear the table
            self.clearTable(count);
        },15000);
    },
    updateHandStrength : function(bestHand) {
        this.showHandStrength(bestHand.player, Poker.Hand.fromId(bestHand.handType));
    },
    _notifyPotToPlayerTransfer : function(transfers) {

        for(var l in this.tableListeners) {
            this.tableListeners[l].onPlayerToPotTransfers(transfers);
        }
    },
    clearTable : function(handCount) {
        if(this.handCount==handCount) {
            console.log("No hand started clearing table");
            this._notifyNewHand(this.dealerSeatId);
        } else {
            console.log("new hand started, skipping clear table")
        }
    },
    showHandStrength : function(playerId,hand) {
        var player = this.table.getPlayerById(playerId);
        for(var x in this.tableListeners)   {
            this.tableListeners[x].onPlayerHandStrength(player,hand);
        }
    },
    handlePlayerAction : function(playerId,actionType,amount){
        var player = this.table.getPlayerById(playerId);
        for(var x in this.tableListeners) {
            this.tableListeners[x].onPlayerActed(player,actionType,amount);
        }
    },
    _notifyNewHand : function(dealerSeatId) {
        for(var x in this.tableListeners)   {
            this.tableListeners[x].onStartHand(dealerSeatId);
        }
    },
    setDealerButton : function(seatId) {
        for(var x in this.tableListeners)   {
            this.tableListeners[x].onMoveDealerButton(seatId);
        }
    },
    addPlayer : function(seat,playerId, playerName) {
        console.log("adding player " + playerName + " at seat" + seat);
        var p = new Poker.Player(playerId, playerName);
        this.table.addPlayer(seat,p);
        this._notifyPlayerAdded(seat,p);
    },
    removePlayer : function(playerId) {
        console.log("removing player with playerId " + playerId);
        this.table.removePlayer(playerId);
        this._notifyPlayerRemoved(playerId);
    },
    /**
     * handle deal cards, passes a card string as parameter
     * card string i h2 (two of hearts), ck (king of spades)
     * @param {int} playerId  the id of the player
     * @param {int} cardId id of the card
     * @param {string} cardString the card string identifier
     */
    dealPlayerCard : function(playerId,cardId,cardString) {
        var player = this.table.getPlayerById(playerId);
        for(var x in this.tableListeners)   {
            this.tableListeners[x].onDealPlayerCard(player,cardId, cardString);
        }
    },
    addTableListener : function(listener) {
        this.tableListeners.push(listener);
    },
    updatePlayerBalance : function(playerId, balance) {
        var p = this.table.getPlayerById(playerId);
        if(p == null) {
            throw "Unable to find player to update balance pid = " + playerId;
        }
        p.balance = balance;
        this._notifyPlayerUpdated(p);

    },
    updatePlayerStatus : function(playerId, status) {
        var p = this.table.getPlayerById(playerId);
        if(p==null) {
            throw "Player with id " + playerId + " not found";
        }

        p.tableStatus = status;
        this._notifyPlayerUpdated(p);
    },
    handleRequestPlayerAction : function(playerId,allowedActions,timeToAct) {
        var player = this.table.getPlayerById(playerId);
        for(var x in this.tableListeners)   {
            this.tableListeners[x].onRequestPlayerAction(player,allowedActions,timeToAct,this.mainPot);
        }

    },
    updateMainPot : function(amount){
        this.mainPot = amount;
        this._notifyMainPotUpdated(amount);
    },
    dealCommunityCard : function(cardId,cardString) {
        for(var x in this.tableListeners)   {
            this.tableListeners[x].onDealCommunityCard(cardId,cardString);
        }
    },
    updatePots : function(pots) {
        for(var p in pots) {
            if(pots[p].type == Poker.PotType.MAIN) {
                console.log("updating main pot");
                this.mainPot = pots[p].amount;
                this._notifyMainPotUpdated(pots[p].amount);
                break;
            }
        }
    },
    exposePrivateCard : function(cardId,cardString) {
        for(var x in this.tableListeners)   {
            this.tableListeners[x].onExposePrivateCard(cardId,cardString);
        }
    },
    bettingRoundComplete : function() {
        for(var x in this.tableListeners)   {
            this.tableListeners[x].onBettingRoundComplete();
        }
    },
    leaveTable : function() {
        for(var x in this.tableListeners)   {
            this.tableListeners[x].onLeaveTableSuccess();
        }
        this.tableListeners = [];
        this.table = null;
    },
    _notifyMainPotUpdated : function(amount) {
        for(var x in this.tableListeners)   {
            this.tableListeners[x].onMainPotUpdate(amount);
        }
    },
    _notifyPlayerUpdated : function(player) {
        for(var x in this.tableListeners)   {
            this.tableListeners[x].onPlayerUpdated(player);
        }
    },
    _notifyPlayerAdded : function(seat,player) {
        for(var l in this.tableListeners){
            this.tableListeners[l].onPlayerAdded(seat,player);
        }
    },
    _notifyPlayerRemoved : function(playerId) {
        for(var l in this.tableListeners){
            this.tableListeners[l].onPlayerRemoved(playerId);
        }
    }
});