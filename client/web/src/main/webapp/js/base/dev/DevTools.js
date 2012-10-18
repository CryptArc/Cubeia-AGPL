"use strict";

var Poker = Poker || {};

Poker.DevTools = Class.extend({
    tableId : 999999,
    tableManager : null,
    cards : null,
    cardIdSeq : 0,
    mockEventManager : null,
    init : function() {
        var self = this;
        this.initCards();
    },
    initCards : function() {
       var suits = "hsdc ";
       var rank = "0"
    },
    launch : function() {
        var self = this;
        Poker.AppCtx.getViewManager().onLogin();
        setTimeout(function(){
            self.createTable();
        },1000);
        new Poker.PositionEditor("#tableView-"+this.tableId);

    },

    createTable : function() {
        var self = this;

        var tableName = "Dev Table";
        this.tableManager = Poker.AppCtx.getTableManager();
        var tableViewContainer = $(".view-container");
        var templateManager = new Poker.TemplateManager();


        var beforeFunction = function() {
            var tableLayoutManager = new Poker.TableLayoutManager(self.tableId, tableViewContainer,
                templateManager, null, 10);
            self.tableManager.createTable(self.tableId, 10, tableName , [tableLayoutManager]);
            Poker.AppCtx.getViewManager().addTableView(tableLayoutManager,tableName);
        };

        var cleanUpFunction = function() {
            self.tableManager.leaveTable(self.tableId);
            Poker.AppCtx.getViewManager().removeTableView(self.tableId);
        };

        this.mockEventManager = new Poker.MockEventManager(beforeFunction,cleanUpFunction);

        var mockEvent = function(name,func,delay) {
            return new Poker.MockEvent(name,func,delay);
        };

        this.mockEventManager.addEvent(
            mockEvent("Add players",function(){
                for(var i = 0; i<10; i++) {
                    self.addPlayer(i,i,"CoolPlayer"+i);
                }
            })
        );
        this.mockEventManager.addEvent(
            mockEvent("Deal cards",function(){
                for(var i = 0; i<10; i++) {
                    self.dealCards(i,i);
                }
            })
        );
        this.mockEventManager.addEvent(
            mockEvent("Player 0 small blind",function(){
                self.playerAction(0,Poker.ActionType.SMALL_BLIND);
            })
        );

        this.mockEventManager.addEvent(
            mockEvent("Update main pots", function(){
                self.tableManager.updatePots(self.tableId,[{type:Poker.PotType.MAIN, amount:10000}]);
            })
        );


        this.mockEventManager.addEvent(
            mockEvent("Player 1 small blind",function(){
                self.playerAction(1,Poker.ActionType.BIG_BLIND);
            })
        );
        this.mockEventManager.addEvent(
            mockEvent("Player 2 call",function(){
                self.playerAction(2,Poker.ActionType.CALL);
            })
        );
        this.mockEventManager.addEvent(
            mockEvent("Player 3 fold",function(){
                self.playerAction(3,Poker.ActionType.FOLD,0);
            })
        );
        this.mockEventManager.addEvent(
            mockEvent("Player 4 raise",function(){
                self.playerAction(4,Poker.ActionType.RAISE);
            })
        );
        this.mockEventManager.addEvent(
            mockEvent("Player 5 raise",function(){
                self.playerAction(5,Poker.ActionType.RAISE);
            })
        );
        this.mockEventManager.addEvent(
            mockEvent("All players call",function(){
                self.playerAction(6,Poker.ActionType.CALL);
                self.playerAction(7,Poker.ActionType.CALL);
                self.playerAction(8,Poker.ActionType.CALL);
                self.playerAction(9,Poker.ActionType.CALL);
                self.playerAction(0,Poker.ActionType.CALL);
                self.playerAction(1,Poker.ActionType.CALL);
                self.playerAction(2,Poker.ActionType.CALL);
                self.playerAction(4,Poker.ActionType.CALL);
            })
        );

        this.mockEventManager.addEvent(
            mockEvent("Deal flop", function(){
                self.tableManager.dealCommunityCard(self.tableId,self.cardIdSeq++,"as");
                self.tableManager.dealCommunityCard(self.tableId,self.cardIdSeq++,"ad");
                self.tableManager.dealCommunityCard(self.tableId,self.cardIdSeq++,"ks");
            })
        );


        this.mockEventManager.addEvent(
            mockEvent("Deal turn", function(){
                self.tableManager.dealCommunityCard(self.tableId,self.cardIdSeq++,"qs");
            })
        );
        this.mockEventManager.addEvent(
            mockEvent("Deal river", function(){
                self.tableManager.dealCommunityCard(self.tableId,self.cardIdSeq++,"ac");
            })
        );


    },

    addPlayer : function(seat,playerId,name) {
        this.tableManager.addPlayer(this.tableId,seat,playerId, name);
        this.tableManager.updatePlayerStatus(this.tableId, playerId, Poker.PlayerTableStatus.SITTING_IN);
        this.tableManager.updatePlayerBalance(this.tableId,playerId, 100000);
    },
    dealCards : function(seat,playerId) {
        this.tableManager.dealPlayerCard(this.tableId,playerId,this.cardIdSeq++,"  ");
        this.tableManager.dealPlayerCard(this.tableId,playerId,this.cardIdSeq++,"  ");
    },
    playerAction : function(playerId,action,amount) {
        if(!amount) {
            amount = 10000
        }
        this.tableManager.handlePlayerAction(this.tableId,playerId,action,amount);
    },

    getRandomCard : function() {

    }
});

$(document).ready(function(){

    if(document.location.hash.indexOf("dev")!=-1){
        console.log("dev mode enabled");
        var dt = new Poker.DevTools();
        setTimeout(function(){dt.launch();},1000);
    }
});
