"use strict";
var Poker = Poker || {};
/**
 * Handles the table UI, extends the Poker.TableListener
 * interface to receive events about the table
 * @type {Poker.TableLayoutManager}
 */
Poker.TableLayoutManager = Class.extend({
    tableContainer : null,
    capacity : 10,
    seatTemplate : null,
    emptySeatTemplate : null,
    templateManager : null,
    cardElements : null,
    tableInfoElement : null,
    myActionsManager : null,
    myPlayerSeatId : -1,
    seats : null,
    dealerButton : null,
    currentDealer : -1,
    soundManager : null,
    tableId : -1,

    /**
     * @type {Poker.BuyInDialog}
     */
    buyInDialog : null,
    communityCardsContainer : null,
    mainPotContainer : null,
    tableView : null,
    animationManager : null,
    totalPotContainer : null,
    viewContainerOffsetTop : 0,
    /**
     * @type Poker.Clock
     */
    clock : null,

    /**
     *
     * @param {Number} tableId
     * @param tableViewContainer
     * @param {Poker.TemplateManager} templateManager
     * @param {Number} capacity
     * @constructor
     */
    init : function(tableId, tableViewContainer, templateManager, capacity) {
        if (!tableViewContainer) {
            throw "TableLayoutManager requires a tableViewContainer";
        }
        this.tableContainer = tableViewContainer;
        this.seats = new Poker.Map();
        this.animationManager = new Poker.AnimationManager();
        var tableViewTemplate = templateManager.getTemplate("tableViewTemplate");
        var tableViewHtml = Mustache.render(tableViewTemplate,{tableId : tableId});
        this.viewContainerOffsetTop = tableViewContainer.offset().top;
        tableViewContainer.append(tableViewHtml);
        var viewId = "#tableView-"+tableId;
        this.tableView = $(viewId);

        this.tableId = tableId;
        this.soundManager = new Poker.SoundManager(Poker.AppCtx.getSoundRepository(), tableId);
        var self = this;
        var actionCallback = function(actionType,amount){
            if(actionType.id == Poker.ActionType.SIT_IN.id) {
                self.handleSitIn();
            }
            new Poker.PokerRequestHandler(self.tableId).onMyPlayerAction(actionType,amount);

        };
        this.buyInDialog = new Poker.BuyInDialog();
        this.myActionsManager = new Poker.MyActionsManager(this.tableView, tableId, actionCallback, false);
        this.templateManager = templateManager;
        this.capacity = capacity || this.capacity;
        this.seatTemplate = $("#seatTemplate").html();
        this.emptySeatTemplate = templateManager.getTemplate("emptySeatTemplate");
        this.totalPotContainer = this.tableView.find(".total-pot").hide();

        for(var i = 0; i<this.capacity; i++){
            this.addEmptySeatContent(i,i,true);
        }

        this.dealerButton = new Poker.DealerButton(this.tableView.find(".dealer-button"),this.animationManager);
        $(this.tableView).show();
        this.communityCardsContainer = this.tableView.find(".community-cards");
        this.mainPotContainer = this.tableView.find(".main-pot");
        this.tableInfoElement = this.tableView.find(".table-info");
        tableViewContainer.show();
        this.cardElements = new Poker.Map();
        this.clock = new Poker.Clock(this.tableInfoElement.find(".time-to-next-level-value"));

        $(".future-action").show();
    },
    handleSitIn : function() {
        var myPlayerSeat = this.seats.get(this.myPlayerSeatId);
        if (myPlayerSeat!=null) {
            myPlayerSeat.doPostBlinds();
            myPlayerSeat.setSitOutNextHand(false)
        }

    },
    onActivateView : function() {
        this.animationManager.setActive(true);
    },
    onDeactivateView : function() {
        this.animationManager.setActive(false);
    },
    /**
     * Adds an empty seat div to a seat id and if position supplied
     * also the position css class
     * @param seatId - the seat id to add the empty seat div to
     * @param pos - position if supplied adds the corresponding position css class
     * @param active - {boolean} boolean to indicate if the seat is active or not (active == occupied)
     */
    addEmptySeatContent : function(seatId,pos,active) {
        console.log("addEmptySeatContent seatId="+seatId);
        var seat = $("#seat"+seatId+"-"+this.tableId);
        seat.addClass("seat-empty").html(Mustache.render(this.emptySeatTemplate,{}));
        seat.removeClass("seat-sit-out").removeClass("seat-folded");
        if (typeof(pos) != "undefined" && pos != -1) {
            seat.addClass("seat-pos-"+pos);
        }
        if (!active) {
            seat.addClass("seat-inactive");
        }
    },
    onBuyInCompleted : function() {
        this.buyInDialog.close();
    },
    onBuyInError : function(msg) {
        this.buyInDialog.onError(msg);
    },
    onBuyInInfo : function(tableName,balanceInWallet, balanceOnTable, maxAmount, minAmount, mandatory) {
        this.buyInDialog.show(this.tableId,tableName,balanceInWallet,maxAmount,minAmount);
    },
    /**
     * Called when a player is added to the table
     * @param seatId  - the seat id of the player
     * @param player  - the player that was added
     */
    onPlayerAdded : function(seatId,player) {
        console.log("Player " + player.name + " added at seat " + seatId);

        var seat = null;
        var elementId = null;
        if (player.id == Poker.MyPlayer.id) {
            elementId = "myPlayerSeat-"+this.tableId;
            seat = new Poker.MyPlayerSeat(this.tableId,elementId,seatId,player,this.myActionsManager,this.animationManager);
            this.myPlayerSeatId = seatId;
            this._calculateSeatPositions();
            if(this.currentDealer!=-1) {
                this.onMoveDealerButton(this.currentDealer);
            }
            this.seats.put(seatId,seat);
            this.tableView.find(".seat-pos-0").hide();
            var self = this;
            this.tableView.find(".click-area-0").touchSafeClick(function(){
                new Poker.PokerRequestHandler(self.tableId).requestBuyInInfo();
            });
        } else {

            elementId = "seat"+seatId+"-"+this.tableId;
            seat = new Poker.Seat(elementId, seatId, player, this.animationManager);
            seat.setSeatPos(-1,this._getNormalizedSeatPosition(seatId));

            this.seats.put(seatId,seat);
        }

    },
    /**
     * Called when a player left the table,
     * removes the player from the table UI and resets
     * the seat to open
     * @param playerId - the id of the player
     */
    onPlayerRemoved : function(playerId) {
        var seat = this.getSeatByPlayerId(playerId);
        if (this.myPlayerSeatId == seat.seatId) {
            this.myPlayerSeatId = -1;
            Poker.AppCtx.getDialogManager().displayGenericDialog(
                {header: "Seating info", message : "You have been removed from table "});
        }
        seat.clearSeat();
        this.seats.remove(seat.seatId);
        this.addEmptySeatContent(seat.seatId,-1,(this.myPlayerSeatId==-1));
    },

    /**
     * Retrieves the seat by player id
     * @param id  of the player
     * @return {Poker.Seat} the players seat or null if not found
     */
    getSeatByPlayerId : function(id) {
        var seats = this.seats.values();
        for(var i = 0; i<seats.length; i++) {
            if(seats[i].player.id == id) {
                return seats[i];
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
    onTableCreated : function() {
        this.currentDealer = -1;
        this.dealerButton.move(0,0);
        this.dealerButton.hide();
    },
    /**
     *
     * @param {String} handId
     */
    onStartHand : function(handId) {
        this._resetSeats();
        this._resetCommunity();
        this.tableView.find(".pot-transfer").remove();
        this.cardElements = new Poker.Map();
        this.myActionsManager.onStartHand();
        console.log("Hand " + handId + " started");
    },
    /**
     * Updates the blinds info given a new level.
     *
     * @param {com.cubeia.games.poker.io.protocol.BlindsLevel} level
     * @param {Number} secondsToNextLevel
     */
    onBlindsLevel : function(level, secondsToNextLevel) {
        if (level.smallBlind != null && level.bigBlind != null) {
            this.tableInfoElement.show();
            this.tableInfoElement.find(".table-blinds-value").html(level.smallBlind + "/" + level.bigBlind);
            this.myActionsManager.setBigBlind(Math.floor(parseFloat(level.bigBlind)*100));
            if (secondsToNextLevel >= 0){
                this.clock.sync(secondsToNextLevel);
            } else {
                this.tableInfoElement.find(".time-to-next-level").hide();
            }
        }
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
        this.playSound(Poker.Sounds.DEAL);
        var seat = this.getSeatByPlayerId(player.id);
        var card = new Poker.Card(cardId,this.tableId,cardString,this.templateManager);
        seat.dealCard(card);
        this._storeCard(card);
    },
    onExposePrivateCard : function(cardId,cardString){
        var card = this.cardElements.get(cardId);
        if(cardString == card.cardString) {
            return;
        }

        card.exposeCard(cardString);
        new Poker.CSSClassAnimation(card.getJQElement()).addClass("exposed").start(this.animationManager);

    },
    onMoveDealerButton : function(seatId) {
        this.currentDealer = seatId;
        var seat = this.seats.get(seatId);
        var off = seat.getDealerButtonOffsetElement().offset();
        var leftC  = this.tableView.offset().left;

        var pos = {
            left : Math.round(off.left  - leftC +  seat.getDealerButtonOffsetElement().width()*0.95),
            top : Math.round(off.top)
        };
        this.dealerButton.move(pos.top,pos.left);

    },
    onBettingRoundComplete :function() {
        var seats =  this.seats.values();
        for(var x = 0; x<seats.length; x++) {
            seats[x].onBettingRoundComplete();
        }
    },
    onPlayerHandStrength : function(player, hand) {
        var seat = this.getSeatByPlayerId(player.id);
        seat.showHandStrength(hand);
    },
    onDealCommunityCard : function(cardId, cardString) {
        this.playSound(Poker.Sounds.DEAL);
        var card = new Poker.CommunityCard(cardId,this.tableId,cardString,this.templateManager);
        var html = card.render();
        this.communityCardsContainer.append(html);

        // Animate the cards.
        var div = $('#' + card.getCardDivId());

        new Poker.TransformAnimation(div).addTranslate3d(0,0,0,"").start(this.animationManager);

        this._storeCard(card);
        this._moveToPot();
    },
    onTotalPotUpdate : function(amount) {
       this.totalPotContainer.show().find(".amount").html(Poker.Utils.formatCurrency(amount));
    },
    /**
     *
     * @param {Poker.Pot[]} pots
     */
    onPotUpdate : function(pots) {
        console.log("POTS:");
        console.log(pots);
        for(var i = 0; i<pots.length; i++) {
            var potElement = this.mainPotContainer.find(".pot-"+pots[i].id);
            if(potElement.length>0) {
                potElement.html(Poker.Utils.formatCurrency(pots[i].amount));
            } else {
                var t = this.templateManager.getTemplate("mainPotTemplate");
                this.mainPotContainer.append(Mustache.render(t,
                    { potId: pots[i].id, amount : Poker.Utils.formatCurrency(pots[i].amount) }));
            }

        }
    },
    onRequestPlayerAction : function(player,allowedActions,timeToAct,mainPot,fixedLimit){
        var seats = this.seats.values();
        for (var s = 0; s<seats.length; s++) {
            seats[s].inactivateSeat();
        }
        var seat = this.getSeatByPlayerId(player.id);
        seat.activateSeat(allowedActions,timeToAct,mainPot,fixedLimit);
    },
    onLeaveTableSuccess : function() {
        $(this.tableView).hide();
        for(var i = 0; i<this.capacity; i++) {
            var s = $("#seat"+i+"-"+this.tableId);
            s.empty();
            s.attr("class","seat");
        }
        if(this.myPlayerSeatId != -1) {
            this.seats.get(this.myPlayerSeatId).clear();
        }
        this.myPlayerSeatId = -1;
        this._resetCommunity();
        var cards = this.cardElements.values();
        for(var x = 0; x<cards.length; x++) {
            $("#"+cards[x].getCardDivId()).remove();
        }
        this.myActionsManager.clear();
    },
    _hideSeatActionText : function() {
        var seats = this.seats.values();
        for(var s in seats) {
            seats[s].hideActionText();
        }
    },
    _resetSeats : function() {
        var seats = this.seats.values();
        for(var s in seats){
            seats[s].reset();
        }
    },
    _storeCard : function(card){
        this.cardElements.put(card.id,card);
    },
    _calculateSeatPositions : function() {
        //my player seat position should always be 0
        console.log("seat length on calculate = " + this.seats.size());
        var seats = this.seats.values();
        for(var s in seats){
            seats[s].setSeatPos(seats[s].seatId,this._getNormalizedSeatPosition(seats[s].seatId));
        }
        //do empty seats, question is if we want them or not, looked a bit empty without them
        for(var i = 0; i<this.capacity; i++){
            var seat = $("#seat"+i+"-"+this.tableId);
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
        this.communityCardsContainer.empty();
        this.mainPotContainer.empty();
        this.totalPotContainer.hide().find(".amount").empty();
    },
    _hideSeatActionInfo : function() {
        var seats = this.seats.values();
        for(var s = 0; s<seats.length; s++) {
            seats[s].hideActionInfo();
        }
    },
    _moveToPot : function() {
        var seats = this.seats.values();
        for(var s=0; s<seats.length; s++) {
            seats[s].moveAmountToPot(this.tableView, this.mainPotContainer);
        }
    },
    onPotToPlayerTransfers : function(transfers) {

        var transferAnimator = new Poker.PotTransferAnimator(this.tableId, this.animationManager, $("#seatContainer-"+this.tableId),
            this.mainPotContainer);

        console.log("POT TRANSFERS: ");
        console.log(transfers);
        for(var i = 0; i<transfers.length; i++) {
            var trans = transfers[i];
            if(trans.amount<=0) {
                continue;
            }
            var seat = this.getSeatByPlayerId(trans.playerId);
            seat.onPotWon(trans.potId,trans.amount);
            transferAnimator.addTransfer(seat, trans.potId, trans.amount);
        }
        transferAnimator.start();
    },

    playSound : function(soundName) {
        this.soundManager.playSound(soundName);
    },
    /**
     * @param {Poker.FutureActionType[]} actions
     */
    displayFutureActions : function(actions,callAmount,minBetAmount) {
        this.myActionsManager.displayFutureActions(actions,callAmount,minBetAmount);
    }
});
