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
    cardElements : null,
    myActionsManager : null,
    tableComHandler : null,
    myPlayerSeatId : -1,
    cssAnimator : null,
    seats : null,
    dealerButton : null,
    currentDealer : -1,
    potTransferTemplate : null,
    soundManager : null,
    tableId : -1,
    buyInDialog : null,
    communityCardsContainer : null,
    mainPotContainer : null,
    tableView : null,
    animationManager : null,

    init : function(tableId, tableViewContainer, templateManager, tableComHandler, capacity) {
        if (!tableViewContainer) {
            throw "TableLayoutManager requires a tableViewContainer";
        }
        this.seats = new Poker.Map();
        this.animationManager = new Poker.AnimationManager();
        var tableViewTemplate = templateManager.getTemplate("tableViewTemplate");
        var tableViewHtml = Mustache.render(tableViewTemplate,{tableId : tableId});

        tableViewContainer.append(tableViewHtml);
        var viewId = "#tableView-"+tableId;
        this.tableView = $(viewId);

        this.tableId = tableId;
        this.soundManager = new Poker.SoundManager(Poker.AppCtx.getSoundRepository(), tableId);
        this.tableComHandler = tableComHandler;
        var self = this;
        var actionCallback = function(actionType,amount){
          self.tableComHandler.onMyPlayerAction(self.tableId,actionType,amount);
        };
        this.buyInDialog = new Poker.BuyInDialog(tableComHandler);
        this.myActionsManager = new Poker.MyActionsManager(this.tableView,actionCallback);
        this.cssAnimator = new Poker.CSSAnimator();
        this.templateManager = templateManager;
        this.capacity = capacity || this.capacity;
        this.seatTemplate = $("#seatTemplate").html();
        this.emptySeatTemplate = templateManager.getTemplate("emptySeatTemplate");
        this.potTransferTemplate = templateManager.getTemplate("potTransferTemplate");

        for(var i = 0; i<this.capacity; i++){
            this.addEmptySeatContent(i,i,true);
        }

        this.dealerButton = new Poker.DealerButton(this.tableView.find(".dealer-button"),this.animationManager);
        $(this.tableView).show();
        this.communityCardsContainer = this.tableView.find(".community-cards");
        this.mainPotContainer = this.tableView.find(".main-pot");
        tableViewContainer.show();
        this.cardElements = new Poker.Map();
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
     */
    addEmptySeatContent : function(seatId,pos,active) {
        console.log("addEmptySeatContent seatId="+seatId);
        var seat = $("#seat"+seatId+"-"+this.tableId);
        seat.addClass("seat-empty").html(Mustache.render(this.emptySeatTemplate,{}));
        seat.removeClass("seat-sit-out").removeClass("seat-folded");
        if(typeof(pos)!="undefined" && pos!=-1) {
            seat.addClass("seat-pos-"+pos);
        }
        if(!active) {
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
        if (player.id == Poker.MyPlayer.id) {
            var elementId = "myPlayerSeat-"+this.tableId;
            seat = new Poker.MyPlayerSeat(this.tableId,elementId,seatId,player,this.templateManager,this.myActionsManager,this.animationManager);
            this.myPlayerSeatId = seatId;
            this._calculateSeatPositions();
            if(this.currentDealer!=-1) {
                this.onMoveDealerButton(this.currentDealer);
            }
            this.seats.put(seatId,seat);
            this.tableView.find(".seat-pos-0").hide();
        } else {
            var elementId = "seat"+seatId+"-"+this.tableId;
            seat = new Poker.Seat(elementId, seatId,player,this.templateManager,this.animationManager);
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
    onStartHand : function(dealerSeatId) {
        this._resetSeats();
        this._resetCommunity();
        this.cardElements = new Poker.Map();
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
        for(var x in seats) {
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
        //this.cssAnimator.addTransition(div.get(0),"transform 0.5s ease-out",false);

        new Poker.TransformAnimation(div).addTranslate3d(0,0,0,"").start(this.animationManager);

        this._storeCard(card);
        this._moveToPot();
    },
    onMainPotUpdate : function(amount) {
        var t = this.templateManager.getTemplate("mainPotTemplate");
        this.mainPotContainer.html(Mustache.render(t,{amount : Poker.Utils.formatCurrency(amount)}));
    },
    onRequestPlayerAction : function(player,allowedActions,timeToAct,mainPot){
        var seats = this.seats.values();
        for (var s in seats) {
            seats[s].inactivateSeat();
        }
        var seat = this.getSeatByPlayerId(player.id);
        seat.activateSeat(allowedActions,timeToAct,mainPot);
    },
    onLeaveTableSuccess : function() {
        $(this.tableView).hide();
        for(var i = 0; i<this.capacity; i++) {
            var s = $("#seat"+i+"-"+this.tableId);
            s.empty();
            s.attr("class","seat");
        }
        if(this.myPlayerSeatId!=-1) {
            this.seats.get(this.myPlayerSeatId).clear();
        }
        this.myPlayerSeatId=-1;
        this._resetCommunity();
        var cards = this.cardElements.values();
        for(var x in cards) {
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
    },
    _hideSeatActionInfo : function() {
        var seats = this.seats.values();
        for(var s in seats) {
            seats[s].hideActionInfo();
        }
    },
    _moveToPot : function() {
        var seats = this.seats.values();
        for(var s in seats) {
            seats[s].moveAmountToPot(this.tableView, this.mainPotContainer);
        }
    },
    onPlayerToPotTransfers : function(transfers) {
        for(var t in transfers) {
            var trans = transfers[t];
            this.displayPlayerToPotTransfer(trans.playerId,trans.potId, trans.amount);
        }
    },
    displayPlayerToPotTransfer : function(playerId,potId,amount) {
        var s = this.getSeatByPlayerId(playerId);
        if(amount>0){
            console.log("pot tranfer playerId = " + playerId + ", amount="+amount);
            s.onPotWon(potId,amount);
            this.displayPotTransfer(s.actionAmount ,amount, s.seatId);
        }

    },
    displayPotTransfer : function(targetElement,amount,seatId) {

        var html = Mustache.render(this.potTransferTemplate,{ id : seatId + "-" + this.tableId, amount: Poker.Utils.formatCurrency(amount)});
        $("#seatContainer-"+this.tableId).append(html);
        var div = $("#potTransfer" + seatId + "-"+this.tableId);

        var self = this;

        var offset =  Poker.Utils.calculateDistance(div,targetElement);
        div.css("visibility","visible");

        new Poker.TransformAnimation(div).
            addTranslate3d(offset.left,offset.top,0,"%").
            addCallback(
            function(){
                setTimeout(function(){div.remove();},1000);
            }
        ).start(this.animationManager);
    },
    playSound : function(soundName) {
        this.soundManager.playSound(soundName);
    }
});
