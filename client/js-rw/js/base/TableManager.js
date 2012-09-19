"use strict";
var Poker = Poker || {};

Poker.TableManager = Class.extend({
    table : null,
    tableListeners : [],
    init : function() {

    },
    createTable : function(tableId,capacity, tableListeners) {
        this.table = new Poker.Table(tableId,capacity);
        if(tableListeners) {
            for(var x in tableListeners)   {
                this.addTableListener(tableListeners[x]);
            }
        }
    },
    getTableId : function() {
      return this.table.id;
    },
    startNewHand : function(handId, dealerSeatId) {
        this._notifyNewHand(dealerSeatId);
    },
    endHand : function(hands,potTransfers) {
        for(var h in hands) {
            this.showHandStrength(
                hands[h].player,
                Poker.Hand.fromId(hands[h].handType));
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
        console.log("tableManager.setDealerButton");
    },
    addPlayer : function(seat,playerId, playerName) {
        console.log("adding player " + playerName + " at seat" + seat);
        var p = new Poker.Player(playerId, playerName);
        this.table.addPlayer(seat,p);
        this._notifyPlayerAdded(seat,p);
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
        if(listener instanceof Poker.TableListener) {
            this.tableListeners.push(listener);
        } else {
            throw "listener not instance of Poker.TableListener";
        }
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

        p.status = status;
        this._notifyPlayerUpdated(p);
    },
    handleRequestPlayerAction : function(playerId,allowedActions,timeToAct) {
        var player = this.table.getPlayerById(playerId);
        for(var x in this.tableListeners)   {
            this.tableListeners[x].onRequestPlayerAction(player,allowedActions,timeToAct);
        }

    },
    updateMainPot : function(amount){
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
            this.tableListeners[x].onLeaveTable();
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
    }
});