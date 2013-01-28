var Poker = Poker || {};


Poker.ConnectionManager = Class.extend({

    MAX_RECONNECT_ATTEMPTS : 30,

    retryCount : 0,

    /**
     * time out since last packet received to check for disconnect
     */
    disconnectCheckTimeout : null,

    /**
     * version packet grace timeout that triggers the reconnecting
     */
    startReconnectingGraceTimeout : null,

    /**
     * timeout for reconnect attempts
     */
    reconnectRetryTimeout : null,

    connected : false,

    operatorId : 0,
    authCookie : null,
    
    /**
     * @type {Poker.DisconnectDialog}
     */
    disconnectDialog : null,
    init : function(operatorId, authCookie) {
        this.disconnectDialog = new Poker.DisconnectDialog();
        this.operatorId = operatorId;
        this.authCookie = authCookie;
    },
    onUserLoggedIn : function(playerId, name) {
        Poker.MyPlayer.onLogin(playerId,name);
        $("#username").html(name);
        $("#userId").html(playerId);
        $('#loginView').hide();
        $("#lobbyView").show();
        document.location.hash="#";
        var viewManager = Poker.AppCtx.getViewManager();
        viewManager.onLogin();
        new Poker.LobbyRequestHandler().subscribeToCashGames();
        Poker.AppCtx.getTableManager().onPlayerLoggedIn();
        Poker.AppCtx.getTournamentManager().onPlayerLoggedIn();
        if(!this.authCookie) {
        	// Only store use if this is a username/password login
        	Poker.Utils.storeUser(name,Poker.MyPlayer.password);
        }
    },
    onUserConnected : function() {
        this.connected = true;
        this.scheduleDisconnectCheck();
        this.retryCount = 0;
        this.disconnectDialog.close();
        this.showConnectStatus("Connected");
        if(this.authCookie) {
	        // We have an auth cookie, so this is an 
        	// operator user login, we should bypass the login box
        	Poker.AppCtx.getCommunicationManager().doLogin(this.authCookie, this.authCookie);
        } else {
        	// Ordinary username / pass login
        	var loggedIn = this.handleLoginOnReconnect();
	        if(!loggedIn) {
	            this.handlePersistedLogin();
	        }
        }
    },
    /**
     * Tries to login with credentials stored in local storage
     */
    handlePersistedLogin : function() {
        var username = Poker.Utils.load("username");
        if(username!=null) {
            var password = Poker.Utils.load("password");
            Poker.AppCtx.getCommunicationManager().doLogin(username, password);
        }
    },

    handleLoginOnReconnect : function() {
        if(Poker.MyPlayer.password!=null) {
            Poker.AppCtx.getCommunicationManager().doLogin(Poker.MyPlayer.name, Poker.MyPlayer.password);
            return true;
        } else {
            return false;
        }

    },
    onForcedLogout : function() {
        this.clearTimeouts();
        Poker.AppCtx.getViewManager().onForceLogout();
    },
    onUserDisconnected : function() {
        if(this.connected==true) {
            this.handleDisconnect();
            this.connected = false;
        }
    },
    handleDisconnect : function() {
        console.log("DISCONNECTED");
        this.showConnectStatus("Disconnected, retrying (count " +this.retryCount+")");
        this.clearTimeouts();
        this.reconnect();
    },
    onUserConnecting : function() {
        this.showConnectStatus("Connecting");
    },
    showConnectStatus : function(text) {
        $(".connect-status").html(text);
    },
    onUserReconnecting : function() {
        this.retryCount++;
        this.disconnectDialog.show(this.retryCount);
        this.showConnectStatus("Disconnected, retrying (count " +this.retryCount+")");
    },
    onUserReconnected : function() {
        this.onUserConnected();
    },
    onPacketReceived : function() {
        this.scheduleDisconnectCheck();
    },
    scheduleDisconnectCheck : function() {
        console.log("KEEP ALIVE!");
        this.clearTimeouts();
        var self = this;
        this.disconnectCheckTimeout = setTimeout(function(){
            self.sendVersionPacket();
            self.startReconnectingGraceTimeout = setTimeout(function(){
                self.handleDisconnect();
            },2000);
        },10000);
    },
    clearTimeouts : function() {
        if(this.disconnectCheckTimeout!=null) {
            clearTimeout(this.disconnectCheckTimeout);
        }
        if(this.startReconnectingGraceTimeout!=null) {
            clearTimeout(this.startReconnectingGraceTimeout);
        }
        if(this.reconnectRetryTimeout!=null) {
            clearTimeout(this.reconnectRetryTimeout);
        }

    },
    reconnect : function() {
        if(this.retryCount < this.MAX_RECONNECT_ATTEMPTS) {
            this.onUserReconnecting();
            Poker.AppCtx.getCommunicationManager().connect();
            this.scheduleReconnect();
        } else {
            this.disconnectDialog.stoppedReconnecting();
        }
    },
    onLogout : function() {
        document.location = document.location.hash = "clear";
        document.location.reload();
    },
    scheduleReconnect : function() {
        if(this.reconnectRetryTimeout) {
            clearTimeout(this.reconnectRetryTimeout);
        }
        var self = this;
        this.reconnectRetryTimeout = setTimeout(function(){
            self.reconnect();
        },2000);
    },
    sendVersionPacket : function() {
        console.log("Sending version packet");
        var versionPacket = new FB_PROTOCOL.VersionPacket();
        versionPacket.game = 1;
        versionPacket.operatorid = this.operatorId;
        versionPacket.protocol = 8559;
        Poker.AppCtx.getCommunicationManager().getConnector().sendProtocolObject(versionPacket);
    }
});
