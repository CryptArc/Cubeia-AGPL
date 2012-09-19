"use strict";
var Poker = Poker || {};

Poker.MyPlayerSeat = Poker.Seat.extend({
    myActionsManager : null,
    circularProgressBar : null,
    init : function(seatId, player, templateManager, myActionsManager) {
        this._super(seatId, player, templateManager);
        this.myActionsManager = myActionsManager;
        this.seatElement = $("#myPlayerSeat");
        this.renderSeat();
        $("#myPlayer").show();
        this.myActionsManager.onSitIn();
        this.circularProgressBar = new CircularProgressBar("circularProgressBar");
        this.circularProgressBar.hide();

    },
    renderSeat : function(){
        var t = this.templateManager.getTemplate("myPlayerSeatTemplate");
        var output = Mustache.render(t,this.player);
        this.seatElement.html(output);
        this.reset();
        $("#myPlayerName").html(this.player.name);
    },
    activateSeat : function(allowedActions, timeToAct) {
        this.myActionsManager.onRequestPlayerAction(allowedActions);
        this.circularProgressBar.show();
        this.circularProgressBar.render(timeToAct);

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
        $("#myPlayerBalance").html("&euro;"+this.player.balance);
        this.handlePlayerStatus();
    },
    handlePlayerStatus : function() {
        if(this.player.status == Poker.PlayerStatus.SITTING_OUT) {
            this.myActionsManager.onSitOut();
        } else {
            this.myActionsManager.onSitIn();
        }
    },
    fold : function() {
        this.seatElement.addClass("seat-folded");
        this.seatElement.find(".player-card-container").addClass("seat-folded");
        this.myActionsManager.onFold();
    },
    clear : function() {
        this.seatElement.empty();
        $("#myPlayer").hide();
        this.circularProgressBar.detach();
    }
});