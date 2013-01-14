"use strict";
var Poker = Poker  || {};
/**
 * Class that manages global instances.
 * Usage:
 *  //needs to be executed before anything else,
 *  //usually when the onload event is triggered
 *  Poker.AppCtx.wire();
 *
 *  var tableManager = Poker.AppCtx.getTableManager();
 * @type {Poker.AppCtx}
 */
Poker.AppCtx = Class.extend({
    init : function() {
    },
    /**
     * Creates all the global instances that is needed for the application
     *
     * @param settings
     */
    wire : function(settings) {




        //this
        var templateManager = new Poker.TemplateManager();

        /**
         *
         * @return {Poker.TemplateManager}
         */
        this.getTemplateManager = function() {
            return templateManager;
        };

        var tableManager = new Poker.TableManager();
        /**
         *
         * @return {Poker.TableManager}
         */
        this.getTableManager = function() {
            return tableManager;
        };

        var dialogManager = new Poker.DialogManager();
        /**
         *
         * @return {Poker.DialogManager}
         */
        this.getDialogManager = function() {
            return dialogManager;
        };

        var viewManager = new Poker.ViewManager("tabItems");
        /**
         *
         * @return {Poker.ViewManager}
         */
        this.getViewManager = function(){
            return viewManager;
        };

        var mainMenuManager = new Poker.MainMenuManager(this.getViewManager());

        /**
         *
         * @return {Poker.MainMenuManager}
         */
        this.getMainMenuManager = function() {
            return mainMenuManager;
        };


        var lobbyLayoutManager = new Poker.LobbyLayoutManager();

        /**
         *
         * @return {Poker.LobbyLayoutManager}
         */
        this.getLobbyLayoutManager = function() {
            return lobbyLayoutManager;
        };

        /*
         * The only layout manager we only need (?) one instance of,
         * since you only are able to have one lobby open at once
         */
        var lobbyManager = new Poker.LobbyManager();

        /**
         *
         * @return {Poker.LobbyManager}
         */
        this.getLobbyManager = function() {
            return lobbyManager;
        };

        var soundsRepository = new Poker.SoundRepository();



        /**
         *
         * @return {Poker.SoundRepository}
         */
        this.getSoundRepository = function() {
            return soundsRepository;
        };



        var connectionManager = new Poker.ConnectionManager();
        /**
         * @return {Poker.ConnectionManager}
         */
        this.getConnectionManager = function() {
            return connectionManager;
        };


        var comHandler = new Poker.CommunicationManager(settings.webSocketUrl, settings.webSocketPort);
        /**
         *
         * @return {Poker.CommunicationManager}
         */
        this.getCommunicationManager = function() {
            return comHandler;
        };

        /**
         *
         * @return {FIREBASE.Connector}
         */
        this.getConnector = function() {
            return comHandler.getConnector();
        };

        var tournamentManager = new Poker.TournamentManager(settings.tournamentLobbyUpdateInterval);
        /**
         * @return {Poker.TournamentManager}
         */
        this.getTournamentManager = function() {
            return tournamentManager;
        };






    }
});
Poker.AppCtx = new Poker.AppCtx();