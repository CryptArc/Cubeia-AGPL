var Poker = Poker || {};

/**
 *
 * @type {Poker.ConnectionPacketHandler}
 */
Poker.ConnectionPacketHandler = Class.extend({
    /**
     * @type {Poker.ConnectionManager}
     */
    connectionManager : null,
    init : function() {
        this.connectionManager = Poker.AppCtx.getConnectionManager();
    },
    handleLogin : function(status,playerId,name) {
        if (status == "OK") {
            this.connectionManager.onUserLoggedIn(playerId,name);
        } else {
            this.connectionManager.showConnectStatus("Connected (Login failed with status " + status+ ")");
        }
    },
    handleStatus : function(status) {
        //CONNECTING:1,CONNECTED:2,DISCONNECTED:3,RECONNECTING:4,RECONNECTED:5,FAIL:6,CANCELLED:7
        if (status === FIREBASE.ConnectionStatus.CONNECTED) {
            this.connectionManager.onUserConnected();
            this.initOperatorConfig();
        } else if (status === FIREBASE.ConnectionStatus.DISCONNECTED) {
            this.connectionManager.onUserDisconnected();
        } else if(status === FIREBASE.ConnectionStatus.CONNECTING){
            this.connectionManager.onUserConnecting();
        } else if(status == FIREBASE.ConnectionStatus.RECONNECTING){
            this.connectionManager.onUserReconnecting();
        } else if(status == FIREBASE.ConnectionStatus.RECONNECTED) {
            this.connectionManager.onUserReconnected();
        } else {
            console.log("Unhandled status " + status);
        }
    },
    handleForceLogout : function(code,message) {
        console.log(message);
        this.connectionManager.onForcedLogout();
    },
    initOperatorConfig : function() {
        if(!Poker.OperatorConfig.isPopulated()) {
            var packet = new FB_PROTOCOL.LocalServiceTransportPacket();
            packet.seq = 0;
            packet.servicedata = utf8.toByteArray("" + Poker.SkinConfiguration.operatorId);
            Poker.AppCtx.getConnector().sendProtocolObject(packet);
        }
    }
});