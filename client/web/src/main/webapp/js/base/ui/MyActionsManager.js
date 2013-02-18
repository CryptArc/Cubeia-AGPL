"use strict";
var Poker = Poker || {};
/**
 * Handles the displaying of user buttons,
 * handles what shows when
 * @type {Poker.MyActionsManager}
 */
Poker.MyActionsManager  = Class.extend({
    actionButtons : null,
    tableButtons : null,
    doBetActionButton : null,
    fixedBetActionButton : null,
    fixedRaiseActionButton : null,
    currentActions : null,
    allActions : null,
    cancelBetActionButton : null,
    slider : null,
    tableId : null,
    noMoreBlinds : false,
    bigBlindInCents : 0,
    actionCallback : null,

    /**
     * @type {Poker.FutureActions}
     */
    futureActions : null,
    userActionsContainer : null,

    init : function(view,tableId, actionCallback) {
        var self = this;
        this.actionCallback = actionCallback;
        this.tableId = tableId;
        this.actionButtons = [];
        this.tableButtons = [];
        this.currentActions = [];
        this.allActions = [];
        this.userActionsContainer = $(".user-actions",view);
        this.futureActions = new Poker.FutureActions($(".future-actions",view));
        this._addTableButton($(".action-join",view),Poker.ActionType.JOIN,actionCallback);
        this._addTableButton($(".action-leave",view),Poker.ActionType.LEAVE,actionCallback);
        this._addTableButton($(".action-sit-in",view),Poker.ActionType.SIT_IN,actionCallback);

        var cb = function(minAmount,maxAmount,mainPot){
            self.onClickBetButton(minAmount,maxAmount,mainPot);
        };
        this._addActionButton($(".action-bet",view),Poker.ActionType.BET,cb ,false);

        var cr = function(minAmount,maxAmount,mainPot) {
            self.onClickRaiseButton(minAmount,maxAmount,mainPot);
        };

        this._addActionButton($(".action-raise",view),Poker.ActionType.RAISE,cr,false);

        //we can't put it in actionButtons since it's a duplicate action
        this.doBetActionButton = new Poker.BetAmountButton($(".do-action-bet",view),Poker.ActionType.BET,actionCallback,true);
        this.doRaiseActionButton = new Poker.BetAmountButton($(".do-action-raise",view),Poker.ActionType.RAISE,actionCallback,true);
        this.cancelBetActionButton = new Poker.ActionButton($(".action-cancel-bet",view),null,function(){
            self.onClickCancelButton();
        },false);

        this.fixedBetActionButton = new Poker.ActionButton($(".fixed-action-bet",view),Poker.ActionType.BET,actionCallback,true);
        this.fixedRaiseActionButton = new Poker.ActionButton($(".fixed-action-raise",view),Poker.ActionType.RAISE,actionCallback,true);

        this.allActions.push(this.doBetActionButton);
        this.allActions.push(this.doRaiseActionButton);
        this.allActions.push(this.cancelBetActionButton);


        this._addActionButton($(".action-check", view), Poker.ActionType.CHECK, actionCallback, false);
        this._addActionButton($(".action-fold", view), Poker.ActionType.FOLD, actionCallback, false);
        this._addActionButton($(".action-call", view), Poker.ActionType.CALL, actionCallback, true);
        this._addActionButton($(".action-big-blind", view), Poker.ActionType.BIG_BLIND, actionCallback, true);
        this._addActionButton($(".action-small-blind", view), Poker.ActionType.SMALL_BLIND, actionCallback, true);

        this.onWatchingTable();

    },
    setNoMoreBlinds : function(enabled) {
        console.log("setting no more blinds = " + enabled);
        this.noMoreBlinds = enabled;
    },
    onClickBetButton : function(minAmount,maxAmount,mainPot) {
        this.handleBetSliderButtons(minAmount,maxAmount,mainPot);
        this.doBetActionButton.show();
    },
    onClickRaiseButton : function(minAmount,maxAmount,mainPot) {
        console.log(minAmount);
        this.handleBetSliderButtons(minAmount,maxAmount,mainPot);
        this.doRaiseActionButton.show();
    },
    handleBetSliderButtons : function(minAmount,maxAmount,mainPot) {
        this.hideAllActionButtons();
        this.cancelBetActionButton.show();
        this.showSlider(minAmount,maxAmount,mainPot);
    },
    onClickCancelButton : function() {
        this.hideAllActionButtons();
        this.showActionButtons(this.currentActions);
        this.doBetActionButton.hide();
        this.cancelBetActionButton.hide();
        this.hideSlider();
    },
    hideSlider : function() {
        if (this.slider) {
            this.slider.remove();
        }
    },
    setBigBlind : function(bigBlindInCents) {
        this.bigBlindInCents = bigBlindInCents;
    },
    showSlider : function(minAmount,maxAmount,mainPot) {
        this.slider = new Poker.BetSlider("betSlider");
        this.slider.clear();

        this.slider.setMinBet(minAmount);
        this.slider.setMaxBet(maxAmount);
        this.slider.setBigBlind(this.bigBlindInCents);

        this.slider.addMarker("Min", minAmount);
        this.slider.addMarker("All in", maxAmount);
        this.slider.addMarker("Pot",mainPot);
        this.slider.draw();
    },
    _addActionButton : function(elId, actionType, callback, showAmount){
        var button = null;
        if(actionType.id == Poker.ActionType.BET.id || actionType.id == Poker.ActionType.RAISE.id ) {
            button = new Poker.BetSliderButton(elId,actionType,callback,showAmount);
        } else {
            button = new Poker.ActionButton(elId, actionType, callback, showAmount);
        }
        this.actionButtons[actionType.id] = button;
        this.allActions.push(button);
    },
    _addTableButton : function(elId,actionType,callback) {
        this.tableButtons[actionType.id] = new Poker.ActionButton(elId,actionType,callback,false);
        this.allActions.push(this.tableButtons[actionType.id]);
    },
    onRequestPlayerAction : function(actions,mainPot,fixedLimit,progressBar){

        this.currentActions = actions;
        this.hideAllActionButtons();

        var fromFutureAction = this.futureActions.getAction(actions);
        this.futureActions.clear();

        if(fromFutureAction!=null) {
            this.actionCallback(fromFutureAction.type,fromFutureAction.minAmount);
            return;
        }
        this.futureActions.hide();
        var self = this;
        //to avoid users clicking the action buttons by mistake
        setTimeout(function(){
            self.showActionButtons(actions,mainPot,fixedLimit);
            progressBar.show();
            progressBar.render();
        },500);

    },
    showActionButtons : function(actions,mainPot,fixedLimit) {

        this.userActionsContainer.show();


        for (var a in actions){
            var act = actions[a];
            console.log("Action:");
            console.log(act);
            if(fixedLimit==true && act.type.id == Poker.ActionType.BET.id) {
                if(act.minAmount>0) {
                    this.fixedBetActionButton.setAmount(act.minAmount);
                }
                this.fixedBetActionButton.show();
            } else if(fixedLimit==true && act.type.id == Poker.ActionType.RAISE.id) {
                if(act.minAmount>0) {
                    this.fixedRaiseActionButton.setAmount(act.minAmount,act.maxAmount,mainPot);
                }
                this.fixedRaiseActionButton.show();
            } else {
                if(act.minAmount>0) {
                    this.actionButtons[act.type.id].setAmount(act.minAmount,act.maxAmount,mainPot);
                }
                this.actionButtons[act.type.id].show();
            }
        }
    },
    onStartHand : function() {
        this.futureActions.clear();
        this.futureActions.hide();
    },
    onTournamentOut : function(){
        this.hideAllTableButtons();
        this.hideAllActionButtons();
        this.display(Poker.ActionType.LEAVE);
    },
    onSitIn : function() {
        this.noMoreBlinds = false;
        this.hideAllTableButtons();
        this.display(Poker.ActionType.LEAVE);
    },
    onSitOut : function() {
        this.hideAllTableButtons();
        this.hideAllActionButtons();
        this.display(Poker.ActionType.LEAVE);
        this.display(Poker.ActionType.SIT_IN);
    },
    onWatchingTable : function() {
        this.hideAllActionButtons();
        this.hideAllTableButtons();
        this.display(Poker.ActionType.JOIN);
        this.display(Poker.ActionType.LEAVE);
    },
    clear : function() {
        $.each(this.allActions,function(i,e){
            e.clear();
        });
    },
    onFold : function() {
      this.futureActions.hide();
      this.hideAllActionButtons();
    },
    display : function(actionType) {
        if(this.actionButtons[actionType.id]) {
            this.actionButtons[actionType.id].el.show();
        } else {
            this.tableButtons[actionType.id].el.show();
        }
    },
    hideAllActionButtons : function() {
        for(var a in this.actionButtons) {
            this.actionButtons[a].el.hide();
        }
        this.cancelBetActionButton.hide();
        this.doBetActionButton.hide();
        this.doRaiseActionButton.hide();
        this.fixedBetActionButton.hide();
        this.fixedRaiseActionButton.hide();
        this.hideSlider();

    },
    hideAllTableButtons : function() {
        for(var a in this.tableButtons) {
            this.tableButtons[a].el.hide();
        }
    },
    /**
     * @param {Poker.FutureActionType[]} actions
     */
    displayFutureActions : function(actions, callAmount, minBetAmount) {
        this.futureActions.setFutureActions(actions,callAmount,minBetAmount);
        $("#userActActions-"+this.tableId).hide();
    }
});

Poker.ActionButton = Class.extend({
    el : null,
    actionType : null,
    callback : null,
    showAmount : false,
    minAmount : 0,
    maxAmount : 0,
    totalPot : 0,
    init : function(el,actionType,callback,showAmount) {
        this.el = el;
        if(!this.el) {
            console.log("Unable to find action button DOM element with id " + el);
        }
        this.showAmount = showAmount;
        if(this.showAmount==false){
            this.el.find(".amount").hide();
        }
        this.callback=callback;

        this.actionType = actionType;
        this.bindCallBack();
    },
    clear : function() {
        if(this.el) {
            this.el.unbind();
        }
    },
    bindCallBack : function() {

        var self = this;
        if(this.callback!=null && this.actionType!=null) {
            this.el.touchSafeClick(function(e) {
                self.callback(self.actionType,self.minAmount);
            });
        } else if(this.callback!=null) {
            this.el.touchSafeClick(function(e){
                self.callback();
            });
        }
    },
    setAmount : function(minAmount,maxAmount,mainPot){
        if(this.showAmount){
            this.el.find(".amount").html("&euro;").append(Poker.Utils.formatCurrency(minAmount)).show();
        }
        if(maxAmount) {
            this.maxAmount = maxAmount;
        }
        if(mainPot) {
            this.totalPot = mainPot;
        }
        this.minAmount = minAmount;
    },
    show : function(){
        this.el.show();
    },
    hide : function() {
        this.el.hide();
    }
});
Poker.BetAmountButton = Poker.ActionButton.extend({
    init : function(el,actionType,callback,showAmount){
        this._super(el,actionType,callback,showAmount);
    },
    bindCallBack : function() {
        var self = this;
        this.el.touchSafeClick(function(){
            self.callback(self.actionType, Poker.MyPlayer.betAmount);
        });
    }
});
Poker.BetSliderButton = Poker.ActionButton.extend({
    init : function(el,actionType,callback,showAmount){
        this._super(el,actionType,callback,showAmount);
    },
    bindCallBack : function() {
        var self = this;
        this.el.touchSafeClick(function(){
            self.callback(self.minAmount,self.maxAmount,self.totalPot);
        });
    }
});