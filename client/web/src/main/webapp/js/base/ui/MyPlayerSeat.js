"use strict";
var Poker = Poker || {};

/**
 * Handles the UI for the logged in player
 *
 * extends Poker.Seat
 *
 * @type {Poker.MyPlayerSeat}
 */
Poker.MyPlayerSeat = Poker.Seat.extend({

    /**
     * @type Poker.MyActionsManager
     */
    myActionsManager : null,

    /**
     * @type CircularProgressBar
     */
    circularProgressBar : null,

    /**
     * @type Number
     */
    tableId : null,

    seatBalance : null,

    avatarElement : null,

    noMoreBlindsCheckBox : null,

    noMoreBlinds : false,

    sitOutNextHand : false,

    sitOutNextHandCheckBox : null,

    infoElement : null,

    init : function(tableId,elementId, seatId, player, myActionsManager, animationManager) {
        this._super(elementId,seatId, player,animationManager);
        this.tableId = tableId;
        this.myActionsManager = myActionsManager;
        this.seatElement = $("#"+elementId);
        this.renderSeat();

        this.infoElement = $("#"+elementId+"Info").show();
        this.myActionsManager.onSitIn();
        this.circularProgressBar = new CircularProgressBar("#"+elementId+"Progressbar",this.animationManager);
        this.circularProgressBar.hide();
        var self = this;

        this.seatBalance = this.seatElement.find(".seat-balance");

        this.noMoreBlindsCheckBox =  $("#noMoreBlinds-"+tableId);
        this.noMoreBlindsCheckBox.change(function(e) {
            self.noMoreBlinds = $(this).is(":checked");
        });
        this.sitOutNextHandCheckBox =  $("#sitOutNextHand-"+tableId);
        this.sitOutNextHandCheckBox.change(function(e) {
            self.sitOutNextHand = $(this).is(":checked");
            self.onSitOutNextHand();
        });
    },
    setSeatPos : function(prev,pos) {
        //do nothing
    },
    renderSeat : function(){
        var t = this.templateManager.getTemplate("myPlayerSeatTemplate");
        var output = Mustache.render(t,this.player);
        this.seatElement.html(output);
        this.cardsContainer = this.seatElement.find(".cards-container");
        this.actionAmount = this.seatElement.find(".action-amount");
        this.actionText = this.seatElement.find(".action-text");
        this.handStrength = this.seatElement.find(".hand-strength");
        this.avatarElement = this.seatElement.find(".avatar");
        this.avatarElement.addClass("avatar" + (this.player.id % 9));

        this.reset();
        $("#myPlayerName-"+this.tableId).html(this.player.name);
    },
    activateSeat : function(allowedActions, timeToAct,mainPot,fixedLimit) {
        console.log("ON REQUEST ACTION FOR table = " + this.tableId);
        var blindsHandled = this.handleBlinds(allowedActions);
        if(blindsHandled == false) {
            this.myActionsManager.onRequestPlayerAction(allowedActions,mainPot,fixedLimit);
            if(this.circularProgressBar!=null) {
                this.circularProgressBar.detach();
            }
            this.circularProgressBar = new CircularProgressBar("#"+this.seatElement.attr("id")+"Progressbar",this.animationManager);
            this.circularProgressBar.show();
            this.circularProgressBar.render(timeToAct);
            Poker.AppCtx.getViewManager().requestTableFocus(this.tableId);
        }

    },
    /**
     * @param {Poker.Action[]} allowedActions
     * @return {Boolean} whether the request action was a blind or not
     */
    handleBlinds : function(allowedActions) {
        var requestHandler = new Poker.PokerRequestHandler(this.tableId);
        for (var i = 0; i < allowedActions.length; i++) {
            var action = allowedActions[i];
            //TODO: add a wait for big blind option
            if (action.type == Poker.ActionType.BIG_BLIND || action.type == Poker.ActionType.SMALL_BLIND ||
                action.type == Poker.ActionType.DEAD_SMALL_BLIND || action.type == Poker.ActionType.BIG_BLIND_PLUS_DEAD_SMALL_BLIND) {
                console.log("BLIND no more actions = " + this.noMoreBlinds);
                if (this.noMoreBlinds) {
                    requestHandler.onMyPlayerAction(Poker.ActionType.DECLINE_ENTRY_BET, 0);
                } else {
                    requestHandler.onMyPlayerAction(action.type, action.minAmount);
                }
                return true;
            }
        }
        return false;
    },
    onSitOutNextHand : function() {
        var requestHandler = new Poker.PokerRequestHandler(this.tableId);
        if (this.sitOutNextHand) {
            console.log("Player wants to sit out next hand.");
            requestHandler.sitOut();
        } else {
            console.log("Player no longer wants to sit out next hand.");
            requestHandler.sitIn();
        }
    },
    doPostBlinds : function() {
        this.noMoreBlinds = false;
        this.noMoreBlindsCheckBox.attr("checked",false);
    },
    setSitOutNextHand : function(checked) {
        this.sitOutNextHand = checked;
        this.sitOutNextHandCheckBox.attr("checked", checked);
    },
    onAction : function(actionType,amount){
        this.running = false;
        this.circularProgressBar.hide();
        this.showActionData(actionType,amount);
        this.myActionsManager.hideAllActionButtons();
        this.clearProgressBar();
        if(actionType.id == Poker.ActionType.FOLD.id) {
            this.fold();
            Poker.AppCtx.getViewManager().updateTableInfo(this.tableId,{});
        } else if(actionType.id == Poker.ActionType.SIT_IN.id) {

        }
    },
    clearSeat : function() {
        this.seatElement.html("");
        $("#myPlayerBalance-"+this.tableId).html("");
        $("#myPlayerName-"+this.tableId).html("");
        this.myActionsManager.onWatchingTable();
        this.infoElement.hide();
    },
    showHandStrength : function(hand) {
        if(hand.id != Poker.Hand.UNKNOWN.id) {
            this.handStrength.visible = true;
            this.handStrength.html(hand.text).show();
        }
    },
    updatePlayer : function(player) {
        this.player = player;
        $("#myPlayerBalance-"+this.tableId).html("&euro;"+this.player.balance);
        this.seatBalance.html("&euro;"+this.player.balance);

        this.handlePlayerStatus();
    },
    handlePlayerStatus : function() {
        if (this.player.tableStatus == Poker.PlayerTableStatus.SITTING_OUT) {
            this.myActionsManager.onSitOut();
        } else if (this.player.tableStatus == Poker.PlayerTableStatus.TOURNAMENT_OUT){
            this.myActionsManager.onTournamentOut();
        } else {
            this.myActionsManager.onSitIn();
        }
    },
    onCardDealt : function(card) {
        var div = card.getJQElement();
        new Poker.CSSClassAnimation(div).addClass("dealt").start(this.animationManager);
        Poker.AppCtx.getViewManager().updateTableInfo(this.tableId,{card:card});
    },
    onReset : function() {
        Poker.AppCtx.getViewManager().updateTableInfo(this.tableId,{});
    },
    fold : function() {
        this.seatElement.addClass("seat-folded");
        this.seatElement.find(".player-card-container").addClass("seat-folded");
        this.myActionsManager.onFold();
        this.handStrength.visible = false;
    },
    clear : function() {
        this.seatElement.empty();
        $("#myPlayer-"+this.tableId).hide();
        this.circularProgressBar.detach();
    },
    getDealerButtonOffsetElement : function() {
        return this.cardsContainer;
    },
    isMySeat : function() {
        return true;
    }
});
