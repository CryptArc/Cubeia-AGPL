"use strict";
var Poker = Poker  || {};
Poker.AppCtx = Class.extend({
    init : function() {
    },
    wire : function(settings) {
        var templateManager = new Poker.TemplateManager();
        this.getTemplateManager = function() {
            return templateManager;
        };

        var tableManager = new Poker.TableManager();
        this.getTableManager = function() {
            return tableManager;
        };

        var dialogManager = new Poker.DialogManager();
        this.getDialogManager = function() {
            return dialogManager;
        };

        var viewManager = new Poker.ViewManager("tabItems");
        this.getViewManager = function(){
            return viewManager;
        };

        var mainMenuManager = new Poker.MainMenuManager(this.getViewManager());
        this.getMainMenuManager = function() {
            return mainMenuManager;
        };

        var soundsRepository = new Poker.SoundRepository();
        this.getSoundRepository = function() {
            return soundsRepository;
        };


        var actionSender = new Poker.ActionSender();
        this.getActionSender = function() {
            return actionSender;
        };

        var pokerProtocolHandler = new Poker.PokerProtocolHandler();
        this.getProtocolHandler = function() {
            return pokerProtocolHandler;
        };

        var comHandler = new Poker.CommunicationHandler(settings.webSocketUrl, settings.webSocketPort);
        this.getComHandler = function() {
          return comHandler;
        };

        this.getConnector = function() {
            return comHandler.getConnector();
        };








    }
});
Poker.AppCtx = new Poker.AppCtx();