"use strict";
var Poker = Poker || {};

Poker.Seat = Class.extend({
   templateManager : null,
   seatId : -1,
   player : null,
   seatElement : null,
   progressBarElement : null,
   init : function(seatId, player, templateManager) {
       this.seatId = seatId
       this.player = player;
       this.templateManager = templateManager;
       this.seatElement =  $("#seat"+this.seatId);
       this.renderSeat();
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
       if(this.player.status == Poker.PlayerStatus.SITTING_OUT) {
            this.seatElement.addClass("seat-sit-out");
       } else {
           this.seatElement.removeClass("seat-sit-out");
       }
   },
   reset : function() {
       this.hideActionInfo();
       this.seatElement.find(".cards-container").html("").hide();
       this.seatElement.find(".hand-strength").html("").hide();
       this.clearProgressBar();
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
       this.seatElement.find(".cards-container").append(card.render()).show();
       var div = $('#' + card.getCardDivId());
       var currentTop = div.css("top");
       div.css({top: parseInt(currentTop) + 30 + "%"});
       Firmin.animate(div.get(0), { top: currentTop }, "400ms");
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
   activateSeat : function(allowedActions, timeToAct) {
       this.seatElement.addClass("active-seat");
       this.progressBarElement.show();
       Firmin.animate(
            this.progressBarElement.get(0), {
            scale:{y:0.001},
            origin : { x:"100%", y: "100%" },
            timingFunction : 'linear'
       }, timeToAct/1000);
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
