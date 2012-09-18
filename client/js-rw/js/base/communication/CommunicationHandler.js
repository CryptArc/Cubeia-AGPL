var Poker = Poker || {};

Poker.CommunicationHandler = Poker.AbstractConnectorHandler.extend({
    webSocketUrl : null,
    webSocketPort : null,
    tableComManager : null,
    lobbyLayoutManager : null,
    init : function(webSocketUrl, webSocketPort) {
        this.webSocketUrl = webSocketUrl;
        this.webSocketPort = webSocketPort;

        var self = this;
        this.lobbyLayoutManager = new Poker.LobbyLayoutManager();
        this.connect();


    },
    openTable : function(tableId,capacity){
        console.log("CommunicationHandler.openTable");
        $("#boxes").hide();
        $("#tableContainer").show();
        this.tableComManager = new Poker.TableComHandler(this.connector);
        this.tableComManager.onOpenTable(tableId,capacity);
    },
    packetCallback : function(protocolObject){
        this.tableComManager.handlePacket(protocolObject);

    },
    lobbyCallback : function(protocolObject) {
        switch (protocolObject.classId) {
            // Table snapshot list
            case FB_PROTOCOL.TableSnapshotListPacket.CLASSID :
                this.lobbyLayoutManager.handleTableSnapshotList(protocolObject.snapshots);
                break;
            case FB_PROTOCOL.TableUpdateListPacket.CLASSID :
                this.lobbyLayoutManager.handleTableUpdateList(protocolObject.updates);
                break;
            case FB_PROTOCOL.TableRemovedPacket.CLASSID :
                this.lobbyLayoutManager.handleTableRemoved(protocolObject.tableid);
                break;

        }
    },
    watchTable : function(tableId) {
        this.connector.watchTable(tableId);
    },
    loginCallback : function(status,playerId,name) {
        if (status == "OK") {
            Poker.MyPlayer.onLogin(playerId,name);
            $('#dialog1').fadeOut(1000);
            this.subscribeToLobby();
        }
    },

    statusCallback : function(status) {
        console.log("Status recevied: " + status);

        if (status === FIREBASE.ConnectionStatus.CONNECTED) {
            this.lobbyLayoutManager.showLogin();
        } else if (status === FIREBASE.ConnectionStatus.DISCONNECTED) {
            this.connect();
        }
    },
    connect : function () {
        var self = this;
        this.connector = new FIREBASE.Connector(
            function(po) { self.tableComManager.handlePacket(po) },
            function(po){self.lobbyCallback(po);},
            function(status, playerId, name){self.loginCallback(status,playerId,name);},
            function(status){self.statusCallback(status);});

        this.connector.connect("FIREBASE.WebSocketAdapter", this.webSocketUrl, this.webSocketPort, "socket");
        this.tableComManager = new Poker.TableComHandler(this.connector);
    },
    doLogin : function(username,password) {
        this.connector.login(username, password, 0);
    },
    subscribeToLobby : function() {
        this.lobbyLayoutManager.createGrid();
        this.connector.lobbySubscribe(1, "/");


    }

});

