"use strict";
var Poker = Poker || {};

Poker.Seat = Class.extend({
   templateManager : null,
   seatId : -1,
   player : null,
   seatElement : null,
   progressBarElement : null,
   cssAnimator : null,
   cards : null,
   cardsContainer : null,
   init : function(seatId, player, templateManager) {
       this.seatId = seatId
       this.player = player;
       this.templateManager = templateManager;
       this.seatElement =  $("#seat"+this.seatId);
       this.cssAnimator = new Poker.CSSAnimator();
       this.renderSeat();
       this.cardsContainer = this.seatElement.find(".cards-container");
   },
   setSeatPos : function(previousPos, position) {
     this.seatElement.removeClass("seat-empty").removeClass("seat-pos-"+previousPos).removeClass("seat-inactive").addClass("seat-pos-"+position);
   },
   renderSeat : function() {
       var output = Mustache.render(this.templateManager.getTemplate("seatTemplate"),this.player);
       this.seatElement.html(output);
       this.progressBarElement = this.seatElement.find(".progress-bar");
       this.seatElement.find(".avatar").addClass("avatar"+(this.player.id%9));
       this.reset();
   },
   clearSeat : function() {
       this.seatElement.html("");
   },
   updatePlayer : function(player) {
       this.player = player;
       var balanceDiv = this.seatElement.find(".seat-balance");
       if (this.player.balance == 0) {
           balanceDiv.html("All in");
           balanceDiv.removeClass("balance");
       } else {
           balanceDiv.html("&euro;"+this.player.balance);
           balanceDiv.addClass("balance");
       }
       this.handlePlayerStatus();
   },
   handlePlayerStatus : function() {
       if(this.player.tableStatus == Poker.PlayerTableStatus.SITTING_OUT) {
            this.seatElement.addClass("seat-sit-out");
            this.seatElement.find(".player-status").html(this.player.tableStatus.text);
       } else {
           this.seatElement.find(".player-status").html("").hide();
           this.seatElement.removeClass("seat-sit-out");
       }
   },
   reset : function() {
       this.hideActionInfo();
       this.seatElement.find(".hand-strength").html("").hide();
       this.clearProgressBar();
       if(this.cardsContainer) {
           this.cardsContainer.empty();
       }
       this.seatElement.removeClass("seat-folded");
   },
   hideActionInfo : function() {
       this.hideActionText();
       this.seatElement.find(".action-amount").html("").hide();
   },
   hideActionText : function() {
       this.seatElement.find(".action-text").html("").hide();
   },
   onAction : function(actionType,amount) {
       this.inactivateSeat();
       this.showActionData(actionType,amount);
       if(actionType == Poker.ActionType.FOLD) {
            this.fold();
       }
   },
   showActionData : function(actionType,amount) {
       this.seatElement.find(".action-text").html(actionType.text).show();
       var icon = $("<div/>").addClass("player-action-icon").addClass(actionType.id);
       if(amount>0) {
           this.seatElement.find(".action-amount").empty().append("&euro;").
               append(amount).append(icon).show();
       }
   },
   fold : function() {
       this.seatElement.addClass("seat-folded");
       this.seatElement.removeClass("active-seat");
       this.seatElement.find(".player-card-container img").attr("src","images/cards/gray-back.svg");

   },
   dealCard : function(card) {
       this.cardsContainer.append(card.render());
       this.animateDealCard(card.getJQElement());
   },
   animateDealCard : function(div) {
      setTimeout(function(){
          div.addClass("dealt");
      },100);

   },
   inactivateSeat : function() {
        this.seatElement.removeClass("active-seat");
        this.clearProgressBar();
   },
   clearProgressBar : function() {
       if(this.progressBarElement) {
           this.progressBarElement.attr("style","").hide();
       }
   },
    /**
     * When a betting round is complete (communicards are dealt/show shown);
     */
   onBettingRoundComplete : function(){
       this.inactivateSeat();
   },
   activateSeat : function(allowedActions, timeToAct) {
       this.seatElement.addClass("active-seat");
       this.progressBarElement.show();
       var div = this.progressBarElement.get(0);
       this.cssAnimator.addTransition(div,"transform " + timeToAct/1000+"s linear");
       var self = this;
       setTimeout(function(){
            self.cssAnimator.addTransform(div,"scale3d(1,0.01,0)","bottom");
       },50);
   },
   showHandStrength : function(hand) {
       this.seatElement.find(".action-amount").html("").hide();
       this.seatElement.find(".action-text").html("").hide();
       if(hand.id != Poker.Hand.UNKNOWN.id) {
           this.seatElement.find(".hand-strength").html(hand.text).show();
       }

   },
   clear : function() {

   }
});
