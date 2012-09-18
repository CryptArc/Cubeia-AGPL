"use strict";
var Poker = Poker || {};
/**
 * Handles the table UI extends the Poker.TableListener
 * interface to receive events about the table
 * @type {*}
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
            this.calculateSeatPositions();
        } else {
            seat = new Poker.Seat(seatId,player,this.templateManager);
            seat.setSeatPos(-1,this.getNormalizedSeatPosition(seatId));
        }
        this.seats[seatId] = seat;
    },
    calculateSeatPositions : function() {
        //my player seat should always be 0
        for(var s in this.seats){
            this.seats[s].setSeatPos(this.seats[s].seatId,this.getNormalizedSeatPosition(this.seats[s].seatId));
        }
        //do empty seats, question is if we want them or not, looked a bit empty without them
        for(var i = 0; i<this.capacity; i++){
            var seat = $("#seat"+i);
            if(seat.hasClass("seat-empty")){
                 seat.removeClass("seat-pos-"+i).addClass("seat-inactive").addClass("seat-pos-"+this.getNormalizedSeatPosition(i));
            }
        }

    },
    getNormalizedSeatPosition : function(seatId){
        if(this.myPlayerSeatId != -1) {
            return ( this.capacity + seatId - this.myPlayerSeatId ) % this.capacity;
        } else {
            return seatId;
        }
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
    storeCard : function(card){
        this.cardElements[card.getCardDivId()]=card.getDOMElement();
    },
    onStartHand : function(dealerSeatId) {
        this.resetSeats();
        this.resetCommunity();
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
             this.hideSeatActionText();
        }
        seat.onAction(actionType,amount);
    },
    onDealPlayerCard : function(player,cardId,cardString) {
        var seat = this.getSeatByPlayerId(player.id);
        var card = new Poker.Card(cardId,cardString,this.templateManager);
        seat.dealCard(card);
        this.storeCard(card);
    },
    onExposePrivateCard : function(cardId,cardString){
        var card = new Poker.Card(cardId, cardString,this.templateManager);
        var cardEl = this.cardElements[card.getCardDivId()];
        $(cardEl).replaceWith(card.render());
    },
    onPlayerHandStrength : function(player, hand) {
        var seat = this.getSeatByPlayerId(player.id);
        seat.showHandStrength(hand);
    },
    onDealCommunityCard : function(cardId, cardString) {
        var card = new Poker.CommunityCard(cardId,cardString,this.templateManager);
        var html = card.render();
        $("#communityCards").append(html);
        this.storeCard(card);
        this.hideSeatActionInfo();
    },
    resetCommunity : function() {
      $("#communityCards").empty();
      $("#mainPotContainer").empty();
    },
    hideSeatActionInfo : function() {
      for(var s in this.seats) {
          this.seats[s].hideActionInfo();
      }
    },
    hideSeatActionText : function() {
        for(var s in this.seats) {
            this.seats[s].hideActionText();
        }
    },
    resetSeats : function() {
        for(var s in this.seats){
            this.seats[s].reset();
        }
    },
    onMainPotUpdate : function(amount) {
        var t = this.templateManager.getTemplate("mainPotTemplate");
        $("#mainPotContainer").html(Mustache.render(t,{amount : amount}));
    },
    onRequestPlayerAction : function(player,allowedActions,timeToAct){
        for(var s in this.seats) {
            this.seats[s].inactivateSeat();
        }
        var seat = this.getSeatByPlayerId(player.id);
        seat.activateSeat(allowedActions,timeToAct);
    },
    onLeaveTable : function() {
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
        this.resetCommunity();
        this.cardElements = [];
        this.myActionsManager.clear();

    }

});
