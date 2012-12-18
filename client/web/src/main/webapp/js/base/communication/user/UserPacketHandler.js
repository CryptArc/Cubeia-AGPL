var Poker = Poker || {};

/**
 *
 * @type {Poker.UserPacketHandler}
 */
Poker.UserPacketHandler = Class.extend({
    init : function() {
    },
    handleLogin : function(status,playerId,name) {
        if (status == "OK") {
            Poker.MyPlayer.onLogin(playerId,name);
            $("#username").html(name);
            $("#userId").html(playerId);
            $('#loginView').hide();
            $("#lobbyView").show();
            document.location.hash="#";
            var viewManager = Poker.AppCtx.getViewManager();
            viewManager.onLogin();
            new Poker.LobbyRequestHandler().subscribeToCashGames();
        } else {
            this.showConnectStatus("Connected (Login failed with status " + status+ ")");
        }
    },
    handleStatus : function(connectFunction,status) {
        //CONNECTING:1,CONNECTED:2,DISCONNECTED:3,RECONNECTING:4,RECONNECTED:5,FAIL:6,CANCELLED:7

        if (status === FIREBASE.ConnectionStatus.CONNECTED) {
            this.retryCount = 0;
            this.showConnectStatus("Connected");
            this.initOperatorConfig();

        } else if (status === FIREBASE.ConnectionStatus.DISCONNECTED) {
            this.retryCount++;
            this.showConnectStatus("Disconnected, retrying (count " +this.retryCount+")");
            setTimeout(function(){
                connectFunction();
            },500);
        } else if(status === FIREBASE.ConnectionStatus.CONNECTING){
            this.showConnectStatus("Connecting");
        }
    },
    showConnectStatus : function(text) {
        $(".connect-status").html(text);
    },
    initOperatorConfig : function() {
        if(!Poker.OperatorConfig.isPopulated()) {
            var packet = new FB_PROTOCOL.LocalServiceTransportPacket();
            packet.seq = 0;
            packet.servicedata = utf8.toByteArray("0");
            Poker.AppCtx.getConnector().sendProtocolObject(packet);
        }
    }
});
