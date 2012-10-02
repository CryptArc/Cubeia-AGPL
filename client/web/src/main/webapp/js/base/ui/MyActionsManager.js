"use strict";
var Poker = Poker || {};
/**
 * Handles the displaying of user buttons,
 * handles what shows when
 * @type {*}
 */
Poker.MyActionsManager  = Class.extend({
    actionButtons : [],
    tableButtons : [],
    doBetActionButton : null,
    currentActions : [],
    allActions : [],
    cancelBetActionButton : null,
    slider : null,
    init : function(actionCallback) {
        var self = this;
        this._addTableButton("actionJoin",Poker.ActionType.JOIN,actionCallback);
        this._addTableButton("actionLeave",Poker.ActionType.LEAVE,actionCallback);
        this._addTableButton("actionSitIn",Poker.ActionType.SIT_IN,actionCallback);
        this._addTableButton("actionSitOut",Poker.ActionType.SIT_OUT,actionCallback);

        var cb = function(minAmount,maxAmount,mainPot){
            self.onClickBetButton(minAmount,maxAmount,mainPot);
        };
        this._addActionButton("actionBet",Poker.ActionType.BET,cb ,false);

        var cr = function(minAmount,maxAmount,mainPot) {
            self.onClickRaiseButton(minAmount,maxAmount,mainPot);
        };

        this._addActionButton("actionRaise",Poker.ActionType.RAISE,cr,false);

        this._addActionButton("actionCheck",Poker.ActionType.CHECK,actionCallback,false);
        this._addActionButton("actionFold",Poker.ActionType.FOLD,actionCallback,false);
        this._addActionButton("actionCall",Poker.ActionType.CALL,actionCallback,true);

        //we can't put it in actionButtons since it's a duplicate action
        this.doBetActionButton = new Poker.BetAmountButton("doActionBet",Poker.ActionType.BET,actionCallback,true);
        this.doRaiseActionButton = new Poker.BetAmountButton("doActionRaise",Poker.ActionType.RAISE,actionCallback,true);
        this.cancelBetActionButton = new Poker.ActionButton("actionCancelBet",null,function(){
                self.onClickCancelButton();
            },false);

        this.allActions.push(this.doBetActionButton);
        this.allActions.push(this.doRaiseActionButton);
        this.allActions.push(this.cancelBetActionButton);
        this.onWatchingTable();
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
        this.onRequestPlayerAction(this.currentActions);
        this.doBetActionButton.hide();
        this.cancelBetActionButton.hide();
        this.hideSlider();
    },
    hideSlider : function() {
        if(this.slider) {
            this.slider.remove();
        }
    },
    showSlider : function(minAmount,maxAmount,mainPot) {
        this.slider = new Poker.BetSlider("betSlider");
        this.slider.clear();
        this.slider.setMinBet(minAmount);
        this.slider.setMaxBet(maxAmount);

        this.slider.addMarker("Min", minAmount);
        console.log("showing slider mainPot = " + mainPot);
        this.slider.addMarker("All in", maxAmount);
        this.slider.addMarker("Pot",mainPot);
        this.slider.draw();
    },
    _addActionButton : function(elId, actionType ,callback,showAmount){
        var button = null;
        if(actionType.id == Poker.ActionType.BET.id || actionType.id == Poker.ActionType.RAISE.id ) {
            button = new Poker.BetSliderButton(elId,actionType,callback,showAmount);
        } else {
            button = new Poker.ActionButton(elId,actionType,callback,showAmount);
        }
        this.actionButtons[actionType.id] = button;
        this.allActions.push(button);
    },
    _addTableButton : function(elId,actionType,callback) {
        this.tableButtons[actionType.id] = new Poker.ActionButton(elId,actionType,callback,false);
        this.allActions.push(this.tableButtons[actionType.id]);
    },
    onRequestPlayerAction : function(actions,mainPot){
      this.currentActions = actions;
      this.hideAllActionButtons();
      for(var a in actions){;
          var act = actions[a];
          if(act.minAmount>0) {
              this.actionButtons[act.type.id].setAmount(act.minAmount,act.maxAmount,mainPot);
          }
          this.actionButtons[act.type.id].show();
      }
    },
    onSitIn : function() {
        this.hideAllTableButtons();
        this.display(Poker.ActionType.SIT_OUT);
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
        this.hideSlider();

    },
    hideAllTableButtons : function() {
        for(var a in this.tableButtons) {
            this.tableButtons[a].el.hide();
        }
    }
});

Poker.ActionButton = Class.extend({
    el : null,
    actionType : null,
    callback : null,
    showAmount : false,
    minAmount : 0,
    maxAmount : 0,
    mainPot : 0,
    init : function(elId,actionType,callback,showAmount){
        var self = this;
        this.el = $("#"+elId);
        if(!this.el) {
            console.log("Unable to find action button DOM element with id " + elId);
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
            this.el.click(function(e){
                self.callback(self.actionType,0);
            });
        } else if(this.callback!=null) {
            this.el.click(function(e){
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
            this.mainPot = mainPot;
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
    init : function(elId,actionType,callback,showAmount){
        this._super(elId,actionType,callback,showAmount);
    },
    bindCallBack : function() {
        var self = this;
        this.el.click(function(){
            self.callback(self.actionType, Poker.MyPlayer.betAmount);
        });
    }
});
Poker.BetSliderButton = Poker.ActionButton.extend({
    init : function(elId,actionType,callback,showAmount){
        this._super(elId,actionType,callback,showAmount);
    },
    bindCallBack : function() {
        var self = this;
        this.el.click(function(){
            self.callback(self.minAmount,self.maxAmount,self.mainPot);
        });
    }
});