"use strict";
var Poker = Poker || {};
/**
 * Handles the table UI extends the Poker.TableListener
 * interface to receive events about the table
 * @type {Poker.TableLayoutManager}
 */
Poker.TableLayoutManager = Poker.TableListener.extend({
    tableContainer : null,
    capacity : 10,
    seatTemplate : null,
    emptySeatTemplate : null,
    templateManager : null,
    cardElements : [],
    myActionsManager : null,
    tableComHandler : null,
    myPlayerSeatId : -1,
    seats : [],
    init : function(tableContainer,templateManager,tableComHandler,capacity){
        if(!tableContainer) {
            throw "TableLayoutManager requires a tableContainer";
        }
        this.tableComHandler = tableComHandler;
        var self = this;
        var actionCallback = function(actionType,amount){
          self.tableComHandler.onMyPlayerAction(actionType,amount);
        };
        this.myActionsManager = new Poker.MyActionsManager(actionCallback);

        this.templateManager = templateManager;
        this.capacity = capacity || this.capacity;
        this.tableContainer = tableContainer;
        this.seatTemplate = $("#").html();
        this.emptySeatTemplate = templateManager.getTemplate("emptySeatTemplate");
        for(var i = 0; i<this.capacity; i++){
             $("#seat"+i).addClass("seat-empty").addClass("seat-pos-"+i).html(this.emptySeatTemplate);
        };
        $(this.tableContainer).show();
    },
    onPlayerAdded : function(seatId,player) {
        seatId = parseInt(seatId);
        console.log("Player " + player.name + " added at seat " + seatId);
        var seat = null;
        if (player.id == Poker.MyPlayer.id) {
            seat = new Poker.MyPlayerSeat(seatId,player,this.templateManager,this.myActionsManager);
            this.myPlayerSeatId = seatId;
            this._calculateSeatPositions();
        } else {
            seat = new Poker.Seat(seatId,player,this.templateManager);
            seat.setSeatPos(-1,this._getNormalizedSeatPosition(seatId));
        }
        this.seats[seatId] = seat;
    },
    onPlayerRemoved : function(playerId) {
        var seat = this.getSeatByPlayerId(playerId);
        seat.clearSeat();
    },
    getSeatByPlayerId : function(id) {
        for(var s in this.seats) {
            if(this.seats[s].player.id == id) {
                return this.seats[s];
            }
        }
        return null;
    },
    onPlayerUpdated : function(p) {
        var seat = this.getSeatByPlayerId(p.id);
        if(seat==null) {
            console.log("Unable to find player " + p.name + " seat");
            return;
        }
        seat.updatePlayer(p);
    },
    onStartHand : function(dealerSeatId) {
        this._resetSeats();
        this._resetCommunity();
        this.cardElements = [];
    },
    onPlayerActed : function(player,actionType,amount) {
        var seat = this.getSeatByPlayerId(player.id);
        if(seat==null){
            throw "unable to find seat for player " + player.id;
        }
        //make icons gray and hide the action text
        if(actionType == Poker.ActionType.BET || actionType == Poker.ActionType.RAISE) {
             $(".player-action-icon").addClass("action-inactive");
             this._hideSeatActionText();
        }
        seat.onAction(actionType,amount);
    },
    onDealPlayerCard : function(player,cardId,cardString) {
        console.log("DEAL Player CARD = " + player +" cardId = " + cardId);
        var seat = this.getSeatByPlayerId(player.id);
        var card = new Poker.Card(cardId,cardString,this.templateManager);
        seat.dealCard(card);
        this._storeCard(card);
    },
    onExposePrivateCard : function(cardId,cardString){
        var card = new Poker.Card(cardId, cardString,this.templateManager);
        var oldCard = this.cardElements[card.getCardDivId()];
        var cardId = oldCard.getCardDivId();
        if(cardString == oldCard.cardString) {
            return;
        }
        $("#"+cardId).replaceWith(card.render());
        var e = $("#"+cardId);
        setTimeout(function(){
            e.attr("style","top:-35%; -webkit-transform: scale(1); -webkit-transform-origin: center bottom;");
        },100);

    },
    onBettingRoundComplete :function() {
        for(var x in this.seats) {
            this.seats[x].onBettingRoundComplete();
        }
    },
    onPlayerHandStrength : function(player, hand) {
        var seat = this.getSeatByPlayerId(player.id);
        seat.showHandStrength(hand);
    },
    onDealCommunityCard : function(cardId, cardString) {
        console.log("DEAL COMM CARD cardId =" + cardId);
        var card = new Poker.CommunityCard(cardId,cardString,this.templateManager);
        var html = card.render();
        $("#communityCards").append(html);

        // Animate the cards.
        console.log(card);
        var div = $('#' + card.getCardDivId());
        div.css({top:  "30%"});
        Firmin.animate(div.get(0), { top: "0%" }, "400ms");

        this._storeCard(card);
        this._hideSeatActionInfo();
    },
    onMainPotUpdate : function(amount) {
        var t = this.templateManager.getTemplate("mainPotTemplate");
        $("#mainPotContainer").html(Mustache.render(t,{amount : amount}));
    },
    onRequestPlayerAction : function(player,allowedActions,timeToAct){
        for (var s in this.seats) {
            this.seats[s].inactivateSeat();
        }
        var seat = this.getSeatByPlayerId(player.id);
        seat.activateSeat(allowedActions,timeToAct);
    },
    onLeaveTableSuccess : function() {
        $(this.tableContainer).hide();
        for(var i = 0; i<this.capacity; i++) {
            var s = $("#seat"+i);
            s.empty();
            s.attr("class","seat");
        }
        if(this.myPlayerSeatId!=-1) {
            this.seats[this.myPlayerSeatId].clear();
        }
        this.myPlayerSeatId=-1;
        this._resetCommunity();
        for(var x in this.cardElements) {
            $("#"+this.cardElements[x].getCardDivId()).remove();
        }
        this.myActionsManager.clear();
    },
    _hideSeatActionText : function() {
        for(var s in this.seats) {
            this.seats[s].hideActionText();
        }
    },
    _resetSeats : function() {
        for(var s in this.seats){
            this.seats[s].reset();
        }
    },

    _storeCard : function(card){
        this.cardElements[card.getCardDivId()]=card;
    },
    _calculateSeatPositions : function() {
        //my player seat should always be 0
        for(var s in this.seats){
            this.seats[s].setSeatPos(this.seats[s].seatId,this._getNormalizedSeatPosition(this.seats[s].seatId));
        }
        //do empty seats, question is if we want them or not, looked a bit empty without them
        for(var i = 0; i<this.capacity; i++){
            var seat = $("#seat"+i);
            if(seat.hasClass("seat-empty")){
                seat.removeClass("seat-pos-"+i).addClass("seat-inactive").addClass("seat-pos-"+this._getNormalizedSeatPosition(i));
            }
        }

    },
    _getNormalizedSeatPosition : function(seatId){
        if(this.myPlayerSeatId != -1) {
            return ( this.capacity + seatId - this.myPlayerSeatId ) % this.capacity;
        } else {
            return seatId;
        }
    },
    _resetCommunity : function() {
        $("#communityCards").empty();
        $("#mainPotContainer").empty();
    },
    _hideSeatActionInfo : function() {
        for(var s in this.seats) {
            this.seats[s].hideActionInfo();
        }
    }
});
