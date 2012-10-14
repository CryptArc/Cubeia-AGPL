"use strict";
var Poker = Poker || {};

Poker.MyPlayerSeat = Poker.Seat.extend({
    myActionsManager : null,
    circularProgressBar : null,
    tableId : null,
    init : function(tableId,elementId, seatId, player, templateManager, myActionsManager, animationManager) {
        this._super(elementId,seatId, player, templateManager,animationManager);
        this.tableId = tableId;
        this.myActionsManager = myActionsManager;
        this.seatElement = $("#"+elementId);
        this.renderSeat();
        console.log(elementId+"Info");
        $("#"+elementId+"Info").show();
        this.myActionsManager.onSitIn();
        this.circularProgressBar = new CircularProgressBar("#"+elementId+"Progressbar",this.animationManager);
        this.circularProgressBar.hide();
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

        this.reset();
        $("#myPlayerName-"+this.tableId).html(this.player.name);
    },
    activateSeat : function(allowedActions, timeToAct,mainPot) {
        this.myActionsManager.onRequestPlayerAction(allowedActions,mainPot);
        this.circularProgressBar.show();
        this.circularProgressBar.render(timeToAct);
        Poker.AppCtx.getViewManager().requestTableFocus(this.tableId);
    },
    onAction : function(actionType,amount){
        this.running = false;
        this.circularProgressBar.hide();
        this.showActionData(actionType,amount);
        this.myActionsManager.hideAllActionButtons();
        this.clearProgressBar();
        if(actionType == Poker.ActionType.FOLD) {
            this.fold();
        }
    },
    updatePlayer : function(player) {
        this.player = player;
        $("#myPlayerBalance-"+this.tableId).html("&euro;"+this.player.balance);
        this.handlePlayerStatus();
    },
    handlePlayerStatus : function() {
        if(this.player.tableStatus == Poker.PlayerTableStatus.SITTING_OUT) {
            this.myActionsManager.onSitOut();
        } else {
            this.myActionsManager.onSitIn();
        }
    },
    animateDealCard : function(div) {
        var self = this;
        new Poker.CSSClassAnimation(div).addClass("dealt").start(this.animationManager);
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