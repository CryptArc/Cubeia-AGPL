"use strict";
var Poker = Poker || {};

Poker.TableManager = Class.extend({
    tables : null,

    init : function() {
        this.tables = new Poker.Map();
    },
    tableExist : function(tableId) {
        return this.tables.contains(tableId)
    },
    createTable : function(tableId,capacity,name, tableListeners) {
        console.log("Creating table " + tableId + " with name = " + name);
        var table = new Poker.Table(tableId,capacity,name);
        this.tables.put(tableId,table);

        if(tableListeners) {
            for(var x in tableListeners)   {
                table.addListener(tableListeners[x]);
                tableListeners[x].onTableCreated();
            }
        }

        console.log("Creating table " + tableId + " with listeners = " + table.getListeners().length);
        console.log("Nr of tables open = " + this.tables.size());
    },
    getTableListeners : function(tableId) {
        var table = this.tables.get(tableId);
        if(table==null) {
           throw "Table not found " + tableId;
        }
        return table.getListeners();
    },
    removeEventListener : function(tableId) {
      console.log("REMOVE EVENT LISTENER NO OP");
    },
    handleBuyInResponse : function(tableId,status) {
        if(status == com.cubeia.games.poker.io.protocol.BuyInResultCodeEnum.PENDING) {
            var listeners = this.getTableListeners(tableId);
            for(var l in listeners) {
                listeners[l].onBuyInCompleted();
            }
        } else if(status != com.cubeia.games.poker.io.protocol.BuyInResultCodeEnum.OK){
            this.handleBuyInError(tableId,status);
        }
    },
    handleBuyInError : function(tableId,status) {
        var listeners = this.getTableListeners(tableId);
        for(var l in listeners) {
            listeners[l].onBuyInError("Unable to buy in");
        }
    },
    handleBuyInInfo : function(tableId,balanceInWallet, balanceOnTable, maxAmount, minAmount,mandatory) {
        var listeners = this.getTableListeners(tableId);
        var name = this.tables.get(tableId).name;
        for(var l in listeners) {
            listeners[l].onBuyInInfo(name,balanceInWallet,balanceOnTable,maxAmount,minAmount,mandatory);
        }
    },
    getTable : function(tableId) {
        return this.tables.get(tableId);
    },
    startNewHand : function(tableId,handId, dealerSeatId) {
        var table = this.tables.get(tableId);
        table.handCount++;
        table.dealerSeatId = dealerSeatId;
        this._notifyNewHand(tableId,dealerSeatId);
    },
    endHand : function(tableId,hands,potTransfers) {
        for (var hand in hands) {
            this.updateHandStrength(tableId,hands[hand]);
        }
        var table = this.tables.get(tableId);
        console.log("pot transfers:");
        console.log(potTransfers);
        var count = table.handCount;
        var self = this;

        if(potTransfers.fromPlayerToPot === false ){
            this._notifyPotToPlayerTransfer(tableId,potTransfers.transfers);
        }

        setTimeout(function(){
            //if no new hand has started in the next 15 secs we clear the table
            self.clearTable(tableId,count);
        },15000);
    },
    updateHandStrength : function(tableId,bestHand) {
        this.showHandStrength(tableId,bestHand.player, Poker.Hand.fromId(bestHand.handType));
    },
    _notifyPotToPlayerTransfer : function(tableId,transfers) {
        var listeners = this.getTableListeners(tableId);
        for(var l in listeners) {
            listeners[l].onPlayerToPotTransfers(transfers);
        }
    },
    clearTable : function(tableId,handCount) {
        var table = this.tables.get(tableId);
        if(table.handCount==handCount) {
            console.log("No hand started clearing table");
            this._notifyNewHand(tableId,this.dealerSeatId);
        } else {
            console.log("new hand started, skipping clear table")
        }
    },
    showHandStrength : function(tableId,playerId,hand) {
        var table = this.tables.get(tableId);
        var player = table.getPlayerById(playerId);
        var listeners = table.getListeners();
        for(var x in listeners)   {
            listeners[x].onPlayerHandStrength(player,hand);
        }
    },
    handlePlayerAction : function(tableId,playerId,actionType,amount){
        var table = this.tables.get(tableId);
        var player = table.getPlayerById(playerId);
        var listeners = table.getListeners();
        for(var x in listeners) {
            listeners[x].onPlayerActed(player,actionType,amount);
        }
    },
    _notifyNewHand : function(tableId,dealerSeatId) {
        var listeners = this.getTableListeners(tableId);
        for(var x in listeners)   {
            listeners[x].onStartHand(dealerSeatId);
        }
    },
    setDealerButton : function(tableId,seatId) {
        var listeners = this.getTableListeners(tableId);
        for(var x in listeners)   {
            listeners[x].onMoveDealerButton(seatId);
        }
    },
    addPlayer : function(tableId,seat,playerId, playerName) {
        console.log("adding player " + playerName + " at seat" + seat + " on table " + tableId);
        var table = this.tables.get(tableId);
        var p = new Poker.Player(playerId, playerName);
        table.addPlayer(seat,p);
        this._notifyPlayerAdded(tableId,seat,p);
    },
    removePlayer : function(tableId,playerId) {
        console.log("removing player with playerId " + playerId);
        var table = this.tables.get(tableId);
        table.removePlayer(playerId);
        this._notifyPlayerRemoved(tableId,playerId);
    },
    /**
     * handle deal cards, passes a card string as parameter
     * card string i h2 (two of hearts), ck (king of spades)
     * @param {int} playerId  the id of the player
     * @param {int} cardId id of the card
     * @param {string} cardString the card string identifier
     */
    dealPlayerCard : function(tableId,playerId,cardId,cardString) {
        var table = this.tables.get(tableId);
        var player = table.getPlayerById(playerId);
        var listeners = table.getListeners();
        for(var x in listeners)   {
            listeners[x].onDealPlayerCard(player,cardId, cardString);
        }
    },
    updatePlayerBalance : function(tableId,playerId, balance) {
        var table = this.tables.get(tableId);
        var p = table.getPlayerById(playerId);
        if(p == null) {
            throw "Unable to find player to update balance pid = " + playerId;
        }
        p.balance = balance;
        this._notifyPlayerUpdated(tableId,p);

    },
    updatePlayerStatus : function(tableId, playerId, status) {
        var table = this.tables.get(tableId);
        var p = table.getPlayerById(playerId);
        if(p==null) {
            throw "Player with id " + playerId + " not found";
        }

        p.tableStatus = status;
        this._notifyPlayerUpdated(tableId,p);
    },
    handleRequestPlayerAction : function(tableId,playerId,allowedActions,timeToAct) {
        var table = this.tables.get(tableId);
        var player = table.getPlayerById(playerId);
        var listeners = table.getListeners();
        for(var x in listeners)   {
            listeners[x].onRequestPlayerAction(player,allowedActions,timeToAct,this.mainPot);
        }

    },
    updateMainPot : function(tableId,amount){
        this.tables.get(tableId).mainPot = amount;
        this._notifyMainPotUpdated(tableId,amount);
    },
    dealCommunityCard : function(tableId,cardId,cardString) {
        var listeners = this.getTableListeners(tableId);
        for(var x in listeners)   {
            listeners[x].onDealCommunityCard(cardId,cardString);
        }
    },
    updatePots : function(tableId,pots) {
        var table = this.tables.get(tableId);

        for(var p in pots) {
            if(pots[p].type == Poker.PotType.MAIN) {
                console.log("updating main pot");
                table.mainPot = pots[p].amount;
                this._notifyMainPotUpdated(tableId,pots[p].amount);
                break;
            }
        }
    },
    exposePrivateCard : function(tableId,cardId,cardString) {
        var listeners = this.getTableListeners(tableId);
        for(var x in listeners)   {
            listeners[x].onExposePrivateCard(cardId,cardString);
        }
    },
    bettingRoundComplete : function(tableId) {
        var listeners = this.getTableListeners(tableId);
        for(var x in listeners)   {
            listeners[x].onBettingRoundComplete();
        }
    },
    leaveTable : function(tableId) {
        console.log("REMOVING TABLE = " + tableId);
        var listeners = this.getTableListeners(tableId);
        for(var x in listeners)   {
            listeners[x].onLeaveTableSuccess();
        }
        this.tables.remove(tableId).leave();
    },
    _notifyMainPotUpdated : function(tableId,amount) {
        var listeners = this.getTableListeners(tableId);
        for(var x in listeners)   {
            listeners[x].onMainPotUpdate(amount);
        }
    },
    _notifyPlayerUpdated : function(tableId,player) {
        var listeners = this.getTableListeners(tableId);
        for(var x in listeners)   {
            listeners[x].onPlayerUpdated(player);
        }
    },
    _notifyPlayerAdded : function(tableId,seat,player) {
        var listeners = this.getTableListeners(tableId);
        for(var l in listeners){
            listeners[l].onPlayerAdded(seat,player);
        }
    },
    _notifyPlayerRemoved : function(tableId,playerId) {
        var listeners = this.getTableListeners(tableId);
        for(var l in listeners){
            listeners[l].onPlayerRemoved(playerId);
        }
    }
});