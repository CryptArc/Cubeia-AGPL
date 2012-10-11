"use strict";
var Poker = Poker  || {};
Poker.ApplicationContext = Class.extend({
    tableManager : null,
    dialogManager : null,
    viewManager : null,
    mainMenuManager : null,
    soundsRepository : null,
    comHandler : null,
    tableComHandler : null,
    pokerProtocolHandler : null,
    actionSender : null,
    connector : null,
    init : function() {

    },
    wire : function(settings) {
        this.tableManager = new Poker.TableManager();
        this.dialogManager = new Poker.DialogManager();
        this.viewManager = new Poker.ViewManager("tabItems");
        this.mainMenuManager = new Poker.MainMenuManager(this.viewManager);
        this.soundsRepository = new Poker.SoundRepository();
        this.comHandler = new Poker.CommunicationHandler(settings.webSocketUrl, settings.webSocketPort);
        this.connector = this.comHandler.getConnector();
        this.actionSender = new Poker.ActionSender(this.connector);
        this.tableComHandler = new Poker.TableComHandler(this.connector);
        this.pokerProtocolHandler = new Poker.PokerProtocolHandler();
    }
});
Poker.ApplicationContext = new Poker.ApplicationContext();