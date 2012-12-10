var Poker = Poker || {};

Poker.CommunicationManager = Class.extend({
    connector : null,
    webSocketUrl : null,
    webSocketPort : null,
    tableNames : null,
    tableManager : null,
    init : function(webSocketUrl, webSocketPort) {
        this.tableNames = new Poker.Map();
        this.webSocketUrl = webSocketUrl;
        this.webSocketPort = webSocketPort;
        this.tableManager = Poker.AppCtx.getTableManager();
        this.connect();

    },
    getConnector : function() {
        return this.connector;
    },
    showConnectStatus : function(text) {
      $(".connect-status").html(text);
    },
    openTable : function(tableId, capacity,name){
        this.tableNames.put(tableId,name);
        console.log("CommunicationManager.openTable");
        this.onOpenTable(tableId, capacity);

    },
    openTournamentLobby : function(tournamentId,name) {
        var tournamentManager = Poker.AppCtx.getTournamentManager();
        tournamentManager.createTournament(tournamentId,name);

    },
    lobbyCallback : function(protocolObject) {
        console.log("Lobby protocol obj");
        console.log(protocolObject);
        var lobbyPacketHandler = new Poker.LobbyPacketHandler();
        switch (protocolObject.classId) {
            case FB_PROTOCOL.TableSnapshotListPacket.CLASSID :
                lobbyPacketHandler.handleTableSnapshotList(protocolObject.snapshots);
                break;
            case FB_PROTOCOL.TableUpdateListPacket.CLASSID :
                lobbyPacketHandler.handleTableUpdateList(protocolObject.updates);
                break;
            case FB_PROTOCOL.TableRemovedPacket.CLASSID :
                lobbyPacketHandler.handleTableRemoved(protocolObject.tableid);
                break;
            case FB_PROTOCOL.TournamentSnapshotListPacket.CLASSID :
                console.log(protocolObject);
                lobbyPacketHandler.handleTournamentSnapshotList(protocolObject.snapshots);
                break;
            case FB_PROTOCOL.TournamentUpdateListPacket.CLASSID :
                lobbyPacketHandler.handleTournamentUpdates(protocolObject.updates);
                break;


        }
    },
    watchTable : function(tableId) {
        console.log("WATCHING TABLE = " + tableId);
        this.connector.watchTable(tableId);
    },
    loginCallback : function(status,playerId,name) {
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
        }
    },
    retryCount : 0,
    statusCallback : function(status) {
        console.log("Status recevied: " + status);
        //CONNECTING:1,CONNECTED:2,DISCONNECTED:3,RECONNECTING:4,RECONNECTED:5,FAIL:6,CANCELLED:7

        if (status === FIREBASE.ConnectionStatus.CONNECTED) {
            this.retryCount = 0;
            this.showConnectStatus("Connected");
            this.initOperatorConfig();

        } else if (status === FIREBASE.ConnectionStatus.DISCONNECTED) {
            this.retryCount++;
            this.showConnectStatus("Disconnected, retrying (count " +this.retryCount+")");
            var self = this;
            setTimeout(function(){
                self.connect();
            },500);
        } else if(status === FIREBASE.ConnectionStatus.CONNECTING){
            this.showConnectStatus("Connecting");
        }
    },
    initOperatorConfig : function() {
        if(!Poker.OperatorConfig.isPopulated()) {
            var packet = new FB_PROTOCOL.LocalServiceTransportPacket();
            packet.seq = 0;
            packet.servicedata = utf8.toByteArray("1");
            this.connector.sendProtocolObject(packet);
        }
    },
    connect : function () {
        var self = this;
        this.showConnectStatus("Initializing");
        this.connector = new FIREBASE.Connector(
            function(po) {
                self.handlePacket(po);
            },
            function(po){
                self.lobbyCallback(po);
            },
            function(status, playerId, name){
                self.loginCallback(status,playerId,name);
            },
            function(status){
                self.statusCallback(status);
            });

        this.connector.connect("FIREBASE.WebSocketAdapter", this.webSocketUrl, this.webSocketPort, "socket");
    },
    doLogin : function(username,password) {
        this.connector.login(username, password, 0);
    },
    onOpenTable:function (tableId, capacity) {
        var t = this.tableManager.getTable(tableId);
        if(t!=null) {
            Poker.AppCtx.getViewManager().activateViewByTableId(tableId);
        } else {
            this.onOpenTableAccepted(tableId, capacity);
            this.connector.watchTable(tableId);
        }

    },
    onOpenTableAccepted : function (tableId, capacity) {
        var comHandler = Poker.AppCtx.getCommunicationManager();
        var name = comHandler.tableNames.get(tableId);
        if(name==null) {
            name = "Table"; //TODO: fix
        }
        var tableViewContainer = $(".view-container");
        var templateManager = new Poker.TemplateManager();
        var tableLayoutManager = new Poker.TableLayoutManager(tableId, tableViewContainer, templateManager, capacity);
        this.tableManager.createTable(tableId, capacity, name , [tableLayoutManager]);
        Poker.AppCtx.getViewManager().addTableView(tableLayoutManager,name);
    },

    handlePacket:function (packet) {
        console.log(packet);
        var tournamentPacketHandler = new Poker.TournamentPacketHandler();
        var tableId = -1;
        if(packet.tableid) {
            tableId = packet.tableid;
        }
        var tablePacketHandler = new Poker.TablePacketHandler(tableId);
        switch (packet.classId) {
            case FB_PROTOCOL.NotifyJoinPacket.CLASSID :
                tablePacketHandler.handleNotifyJoin(packet);
                break;
            case FB_PROTOCOL.NotifyLeavePacket.CLASSID :
                tablePacketHandler.handleNotifyLeave(packet);
                break;
            case FB_PROTOCOL.SeatInfoPacket.CLASSID :
                tablePacketHandler.handleSeatInfo(packet);
                break;
            case FB_PROTOCOL.JoinResponsePacket.CLASSID :
                tablePacketHandler.handleJoinResponse(packet);
                break;
            case FB_PROTOCOL.GameTransportPacket.CLASSID :
                this.handleGameDataPacket(packet);
                break;
            case FB_PROTOCOL.UnwatchResponsePacket.CLASSID:
                tablePacketHandler.handleUnwatchResponse(packet);
                break;
            case FB_PROTOCOL.LeaveResponsePacket.CLASSID:
                tablePacketHandler.handleLeaveResponse(packet);
                break;
            case FB_PROTOCOL.WatchResponsePacket.CLASSID:
                tablePacketHandler.handleWatchResponse(packet);
                break;
            case FB_PROTOCOL.MttSeatedPacket.CLASSID:
                tournamentPacketHandler.handleSeatedAtTournamentTable(packet);
                this.onOpenTableAccepted(packet.tableid, 10);   //TODO: FIX!
                break;
            case FB_PROTOCOL.MttRegisterResponsePacket.CLASSID:
                tournamentPacketHandler.handleRegistrationResponse(packet);
                break;
            case FB_PROTOCOL.MttUnregisterResponsePacket.CLASSID:
                tournamentPacketHandler.handleUnregistrationResponse(packet);
                break;
            case FB_PROTOCOL.MttTransportPacket.CLASSID:
                tournamentPacketHandler.handleTournamentTransport(packet);
                break;
            case FB_PROTOCOL.MttPickedUpPacket.CLASSID:
                tournamentPacketHandler.handleRemovedFromTournamentTable(packet);
                break;
            case FB_PROTOCOL.LocalServiceTransportPacket.CLASSID:
                this.handleLocalServiceTransport(packet);
                break;
            case FB_PROTOCOL.NotifyRegisteredPacket.CLASSID:
                tournamentPacketHandler.handleNotifyRegistered(packet);
                break;
            default :
                console.log("NO HANDLER");
                console.log(packet);
                break;
        }
    },
    handleLocalServiceTransport : function(packet) {
        var byteArray = FIREBASE.ByteArray.fromBase64String(packet.servicedata);
        var message = utf8.fromByteArray(byteArray);
        var config = JSON.parse(message);
        Poker.OperatorConfig.populate(config);
        console.log(config);

    },

    handleGameDataPacket:function (gameTransportPacket) {
        if(Poker.Settings.isEnabled(Poker.Settings.Param.FREEZE_COMMUNICATION,null)==true) {
            return;
        }
        if(!this.tableManager.tableExist(gameTransportPacket.tableid)) {
            console.log("Received packet for table ("+gameTransportPacket.tableid+") you're not viewing");
            return;
        }
        var tableId = gameTransportPacket.tableid;
        var valueArray =  FIREBASE.ByteArray.fromBase64String(gameTransportPacket.gamedata);
        var gameData = new FIREBASE.ByteArray(valueArray);
        var length = gameData.readInt();
        var classId = gameData.readUnsignedByte();

        var protocolObject = com.cubeia.games.poker.io.protocol.ProtocolObjectFactory.create(classId, gameData);

        console.log("Received packet: ");
        console.log(protocolObject);

        var pokerPacketHandler = new Poker.PokerPacketHandler(tableId);

        switch (protocolObject.classId() ) {
            case com.cubeia.games.poker.io.protocol.BestHand.CLASSID:
                this.tableManager.updateHandStrength(tableId,protocolObject);
                break;
            case com.cubeia.games.poker.io.protocol.BuyInInfoRequest.CLASSID:
                console.log("UNHANDLED PO BuyInInfoRequest");
                console.log(protocolObject);
                break;
            case com.cubeia.games.poker.io.protocol.BuyInInfoResponse.CLASSID:
                pokerPacketHandler.handleBuyIn(protocolObject);
                break;
            case com.cubeia.games.poker.io.protocol.BuyInResponse.CLASSID:
                console.log("BUY-IN RESPONSE ");
                console.log(protocolObject);
                this.tableManager.handleBuyInResponse(tableId,protocolObject.resultCode);
                break;
            case com.cubeia.games.poker.io.protocol.CardToDeal.CLASSID:
                console.log("UNHANDLED PO CardToDeal");
                console.log(protocolObject);
                break;
            case com.cubeia.games.poker.io.protocol.DealerButton.CLASSID:
                this.tableManager.setDealerButton(tableId,protocolObject.seat);
                break;
            case com.cubeia.games.poker.io.protocol.DealPrivateCards.CLASSID:
                var cardsToDeal = protocolObject.cards;
                for(var c in cardsToDeal) {
                    var cardString = Poker.Utils.getCardString(cardsToDeal[c].card);
                    this.tableManager.dealPlayerCard(tableId,cardsToDeal[c].player,cardsToDeal[c].card.cardId,cardString);
                }
                break;
            case com.cubeia.games.poker.io.protocol.DealPublicCards.CLASSID:
                this.tableManager.bettingRoundComplete(tableId);
                for ( var i = 0; i < protocolObject.cards.length; i ++ ) {
                    this.tableManager.dealCommunityCard(tableId,protocolObject.cards[i].cardId,
                        Poker.Utils.getCardString(protocolObject.cards[i]));
                }

                break;
            case com.cubeia.games.poker.io.protocol.DeckInfo.CLASSID:
                console.log("UNHANDLED PO DeckInfo");
                console.log(protocolObject);
                break;
            case com.cubeia.games.poker.io.protocol.ErrorPacket.CLASSID:
                console.log("UNHANDLED PO ErrorPacket");
                console.log(protocolObject);
                break;
            case com.cubeia.games.poker.io.protocol.ExposePrivateCards.CLASSID:
                this.tableManager.bettingRoundComplete(tableId);
                for ( var i = 0; i < protocolObject.cards.length; i ++ ) {
                    this.tableManager.exposePrivateCard(tableId,protocolObject.cards[i].card.cardId,
                        Poker.Utils.getCardString(protocolObject.cards[i].card));
                }
                break;
            case com.cubeia.games.poker.io.protocol.ExternalSessionInfoPacket.CLASSID:
                console.log("UNHANDLED PO ExternalSessionInfoPacket");
                console.log(protocolObject);
                break;
            case com.cubeia.games.poker.io.protocol.FuturePlayerAction.CLASSID:
                console.log("UNHANDLED PO FuturePlayerAction");
                console.log(protocolObject);
                break;
            case com.cubeia.games.poker.io.protocol.GameCard.CLASSID:
                console.log("UNHANDLED PO GameCard");
                console.log(protocolObject);
                break;
            case com.cubeia.games.poker.io.protocol.HandCanceled.CLASSID:
                console.log("UNHANDLED PO HandCanceled");
                console.log(protocolObject);
                break;
            case com.cubeia.games.poker.io.protocol.HandEnd.CLASSID:
                this.tableManager.endHand(tableId,protocolObject.hands,protocolObject.potTransfers);
                break;
            case com.cubeia.games.poker.io.protocol.InformFutureAllowedActions.CLASSID:
                console.log("UNHANDLED PO InformFutureAllowedActions");
                console.log(protocolObject);
                break;
            case com.cubeia.games.poker.io.protocol.PerformAction.CLASSID:
                pokerPacketHandler.handlePerformAction(protocolObject);
                break;
            case com.cubeia.games.poker.io.protocol.PingPacket.CLASSID:
                console.log("UNHANDLED PO PingPacket");
                console.log(protocolObject);
                break;
            case com.cubeia.games.poker.io.protocol.PlayerAction.CLASSID:
                console.log("UNHANDLED PO PlayerAction");
                console.log(protocolObject);
                break;
            case com.cubeia.games.poker.io.protocol.PlayerBalance.CLASSID:
                this.tableManager.updatePlayerBalance(tableId,
                    protocolObject.player,
                    Poker.Utils.formatCurrency(protocolObject.balance)
                );
                break;
            case com.cubeia.games.poker.io.protocol.PlayerDisconnectedPacket.CLASSID:
                console.log("UNHANDLED PO PlayerDisconnectedPacket");
                console.log(protocolObject);
                break;
            case com.cubeia.games.poker.io.protocol.PlayerHandStartStatus.CLASSID:
                var status = Poker.PlayerTableStatus.SITTING_OUT;
                if(protocolObject.status == com.cubeia.games.poker.io.protocol.PlayerTableStatusEnum.SITIN){
                    status = Poker.PlayerTableStatus.SITTING_IN;
                }
                this.tableManager.updatePlayerStatus(tableId,protocolObject.player, status);
                break;
            case com.cubeia.games.poker.io.protocol.PlayerPokerStatus.CLASSID:
                var status = protocolObject.status;
                switch (status) {
                    case com.cubeia.games.poker.io.protocol.PlayerTableStatusEnum.SITIN :
                        this.tableManager.updatePlayerStatus(tableId,protocolObject.player, Poker.PlayerTableStatus.SITTING_IN);
                        break;
                    case com.cubeia.games.poker.io.protocol.PlayerTableStatusEnum.SITOUT :
                        this.tableManager.updatePlayerStatus(tableId,protocolObject.player, Poker.PlayerTableStatus.SITTING_OUT);
                        break;
                }
                break;
            case com.cubeia.games.poker.io.protocol.PlayerReconnectedPacket.CLASSID:
                console.log("UNHANDLED PO PlayerReconnectedPacket");
                console.log(protocolObject);
                break;
            case com.cubeia.games.poker.io.protocol.PlayerState.CLASSID:
                console.log("UNHANDLED PO PlayerState");
                console.log(protocolObject);
                break;
            case com.cubeia.games.poker.io.protocol.PongPacket.CLASSID:
                console.log("UNHANDLED PO PongPacket");
                console.log(protocolObject);
                break;
            case com.cubeia.games.poker.io.protocol.PotTransfers.CLASSID:

                var pots = [];
                for(var i in protocolObject.pots) {
                    var p = protocolObject.pots[i];
                    var type = Poker.PotType.MAIN;
                    if(com.cubeia.games.poker.io.protocol.PotTypeEnum.SIDE == p.type) {
                        type = Poker.PotType.SIDE;
                    }
                    pots.push(new Poker.Pot(p.id,type, p.amount));
                }
                if(pots.length>0) {
                    this.tableManager.updatePots(tableId,pots);
                }
                break;
            case com.cubeia.games.poker.io.protocol.RakeInfo.CLASSID:
                console.log("UNHANDLED PO RakeInfo");
                console.log(protocolObject);
                break;
            case com.cubeia.games.poker.io.protocol.RequestAction.CLASSID:
                pokerPacketHandler.handleRequestAction(protocolObject);
                break;
            case com.cubeia.games.poker.io.protocol.StartHandHistory.CLASSID:
                console.log("UNHANDLED PO StartHandHistory");
                console.log(protocolObject);
                break;
            case com.cubeia.games.poker.io.protocol.StartNewHand.CLASSID:
                this.tableManager.startNewHand(tableId,protocolObject.handId,protocolObject.dealerSeatId);
                break;
            case com.cubeia.games.poker.io.protocol.StopHandHistory.CLASSID:
                console.log("UNHANDLED PO StopHandHistory");
                console.log(protocolObject);
                break;
            case com.cubeia.games.poker.io.protocol.TakeBackUncalledBet.CLASSID:
                console.log("UNHANDLED PO TakeBackUncalledBet");
                console.log(protocolObject);
                break;
            case com.cubeia.games.poker.io.protocol.TournamentOut.CLASSID:
                console.log("UNHANDLED PO TournamentOut");
                console.log(protocolObject);
                break;
            case com.cubeia.games.poker.io.protocol.WaitingToStartBreak:
                this.tableManager.notifyWaitingToStartBreak();
                break;
            case com.cubeia.games.poker.io.protocol.BlindsAreUpdated.CLASSID:
                this.tableManager.notifyBlindsUpdated(protocolObject);
                break;
            default:
                console.log("Ignoring packet: " + protocolObject);
                break;
        }
    }
});

