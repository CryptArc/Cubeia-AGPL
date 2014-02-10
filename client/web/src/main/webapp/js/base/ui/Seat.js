"use strict";
var Poker = Poker || {};
/**
 * Handles all interactions and UI for a players seat
 * @type {Poker.Seat}
 */
Poker.Seat = Class.extend({
    /**
     * @type Poker.TemplateManager
     */
    templateManager: null,
    seatId: -1,
    player: null,
    seatElement: null,
    progressbar: null,
    cards: null,
    cardsContainer: null,
    avatarElement: null,
    actionAmount: null,
    actionText: null,
    handStrength: null,
    seatBalance: null,
    seatBase: null,
    animationManager: null,
    currentProgressBarAnimation: null,
    dealerButtonTarget : null,
    levelElement : null,
    awardElement : null,
    itemElement : null,
    hand : null,
    init: function(elementId, seatId, player, animationManager) {
        this.animationManager = animationManager;
        this.seatId = seatId;
        this.player = player;
        this.templateManager = Poker.AppCtx.getTemplateManager();
        this.seatElement = $("#" + elementId);
        this.renderSeat();
        this.hand = new Poker.PlayerHand();
    },
    setSeatPos: function(previousPos, position) {
        this.seatElement.removeClass("seat-empty").removeClass("seat-pos-" + previousPos).removeClass("seat-inactive").addClass("seat-pos-" + position);
    },
    renderSeat: function() {
        var output = this.templateManager.render("seatTemplate", this.player);
        this.seatElement.html(output);
        this.progressbar = new Poker.CanvasProgressbar(this.seatElement.find(".seat-progressbar canvas") , {border:true});
        this.avatarElement = this.seatElement.find(".avatar");
        this.cardsContainer = this.seatElement.find(".cards-container");
        this.actionAmount = this.seatElement.find(".action-amount");
        this.actionText = this.seatElement.find(".action-text");
        this.seatBalance = this.seatElement.find(".seat-balance");
        this.handStrength = this.seatElement.find(".hand-strength");
        this.seatBase = this.seatElement.find(".avatar-base");
        this.dealerButtonTarget = this.seatElement.find(".dealer-button-target");
        this.levelElement = this.seatElement.find(".player-level");
        this.awardElement = this.seatElement.find(".player-award");
        this.itemElement = this.seatElement.find(".player-item");

        this.reset();
    },
    updateAvatar : function(url) {
        if(url!=null) {
            this.avatarElement.css("backgroundImage","url('"+url+"')");
            this.avatarElement.addClass("custom-avatar");
        } else {
            this.avatarElement.addClass("avatar" + (this.player.id % 9));
        }
    },

    updatePlayerAward : function(url, description) {
        if(url!=null) {
            this.awardElement.css("backgroundImage","url('"+url+"')");
            this.awardElement.attr('title', description);
            this.awardElement.show();
        } else {
            this.awardElement.hide();
        }
    },

    updatePlayerItem : function(url, description) {
        if(url!=null) {
            this.itemElement.css("backgroundImage","url('"+url+"')");
            this.itemElement.attr('title', description);
            this.itemElement.show();
        } else {
            this.itemElement.hide();
        }
    },

    /**
     * Updates the players level or hides it if < 0
     * @param level
     */
    updateLevel : function(level) {
        if(level && level>0) {
            this.levelElement.attr("class","player-level level").addClass("level-"+level);
        } else {
            this.levelElement.hide();
        }
    },
    getDealerButtonOffsetElement: function() {
        return this.seatBase;
    },
    clearSeat: function() {
        this.seatElement.html("");
    },
    updatePlayer: function(player) {
        this.player = player;
        var balanceDiv = this.seatBalance;
        if (this.player.balance == 0) {
            balanceDiv.html("All in");
            balanceDiv.removeClass("balance");
        } else {
            balanceDiv.html(this.player.balance);
            balanceDiv.addClass("balance");
        }
        this.handlePlayerStatus();
    },
    updatePlayerStatus : function(player) {
        this.player = player;
        this.handlePlayerStatus();
    },
    handlePlayerStatus: function() {
        if (this.player.tableStatus == Poker.PlayerTableStatus.SITTING_OUT) {
            this.reset();
            this.seatElement.addClass("seat-sit-out");
            this.seatElement.find(".player-status").show().html(this.player.tableStatus.text);
        } else {
            this.seatElement.find(".player-status").html("").hide();
            this.seatElement.removeClass("seat-sit-out");
        }
    },
    reset: function() {
        this.hideActionInfo();
        this.handStrength.html("").removeClass("won").hide();
        if(this.progressbar!=null) {
            this.progressbar.stop();
        }
        if (this.cardsContainer) {
            this.cardsContainer.empty();
        }
        this.seatElement.removeClass("seat-folded");
        this.onReset();
    },
    onReset: function() {

    },
    hideActionInfo: function() {
        this.hideActionText();
        if (this.actionAmount != null) {
            this.actionAmount.html("");
        }
    },
    hideActionText: function() {
        this.actionText.html("").hide();
    },
    onAction: function(actionType, amount, cardsToDiscard) {
        this.inactivateSeat();
        this.showActionData(actionType, amount);
        if (actionType == Poker.ActionType.FOLD) {
            this.fold();
        } else if(actionType == Poker.ActionType.DISCARD) {
            this.discardCards(cardsToDiscard);
        }
    },
    discardCards : function(cardsToDiscard) {
        this.hand.discardCards(cardsToDiscard);
    },
    showActionData: function(actionType, amount) {
        this.actionText.html(actionType.text).show();
        var icon = $("<div/>").addClass("player-action-icon").addClass(actionType.id + "-icon");
        if (amount!="0") {
            this.actionAmount.removeClass("placed");
            var cont = $("<div/>").addClass("value").append($("<span/>").append(amount));
            this.actionAmount.empty().append(cont);
            this.actionAmount.append(icon).show();
            this.animateActionAmount();
        }
    },
    animateActionAmount: function() {
        new Poker.CSSClassAnimation(this.actionAmount).addClass("placed").start(this.animationManager);
    },
    fold: function() {
        this.seatElement.addClass("seat-folded");
        this.seatElement.removeClass("active-seat");
        this.seatElement.find(".player-card-container img").attr("src", contextPath + "/skins/" + Poker.SkinConfiguration.name + "/images/cards/back.png");
    },
    dealCard: function(card) {
        this.cardsContainer.append(card.render(this.cardsContainer.children().length));
        this.hand.addCard(card);
        this.onCardDealt(card);
    },
    onCardDealt: function(card) {
        var div = card.getJQElement();
        //animate deal card
        new Poker.CSSClassAnimation(div).addClass("dealt").start(this.animationManager);


    },
    inactivateSeat: function() {
        this.seatElement.removeClass("active-seat");
        this.progressbar.stop();
    },
    /**
     * When a betting round is complete (community cards are dealt/shown);
     */
   onBettingRoundComplete : function(){
       this.inactivateSeat();
   },
   activateSeat : function(allowedActions, timeToAct,mainPot,fixedLimit) {
       this.seatElement.addClass("active-seat");
       this.progressbar.start(timeToAct);
       return true;
    },
    rebuyRequested: function(rebuyCost, chipsForRebuy, timeToAct) {
        this.showTimer(timeToAct);
    },
    addOnRequested: function(addOnCost, chipsForAddOn) {
        // Nothing to do.
    },
    hideRebuyButtons: function() {
        // Nothing to do.
    },
    hideAddOnButton: function() {
        // Nothing to do.
    },
    rebuyPerformed: function() {
        this.progressbar.stop();
    },
    showTimer: function(timeToAct) {
        this.seatElement.addClass("active-seat");
        this.progressbar.start(timeToAct);
    },
    showHandStrength: function(hand) {
        this.actionAmount.html("");
        this.actionText.html("").hide();
        if (hand.id != Poker.Hand.UNKNOWN.id) {
            this.handStrength.visible = true;
            this.handStrength.removeClass("long").removeClass("short");
            this.handStrength.addClass(hand.type).html(hand.text).show();
        }

    },
    clear: function() {

    },
    moveAmountToPot: function(view, mainPotContainer) {
        this.hideActionInfo();
        return;
        //before enabling animations for bet amounts going into the pot we need a better
        //handling of animations
        var self = this;
        var amount = this.actionAmount.get(0);
        var pos = self.calculatePotOffset(view, mainPotContainer);

        this.moveToPotComplete = false;
        new Poker.TransformAnimation(amount).
                addTranslate3d(pos.left, pos.top, 0, "px").
                addCallback(function() {
                    self.onMoveToPotEnd();
                });
    },
    moveToPotComplete: true,
    onMoveToPotEnd: function() {
        if (this.moveToPotComplete == false) {
            this.moveToPotComplete = true;
            this.hideActionInfo();
            this.actionAmount.attr("style", "");
        }
    },
    calculatePotOffset: function(view, mainPotContainer) {
        var width = view.width();
        var height = view.height();
        var amountOffset = this.actionAmount.offset();
        var mainPotOffset = mainPotContainer.offset();
        var left = mainPotOffset.left - amountOffset.left;
        var top = mainPotOffset.top - amountOffset.top;
        return { top: Math.round(top) + "px", left: Math.round(left) + "px" };
    },
    isMySeat: function() {
        return false;
    },
    onPotWon: function(potId, amount) {
        this.handStrength.addClass("won");
        this.hideActionInfo();
    }
});