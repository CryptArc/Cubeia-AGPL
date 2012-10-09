var Poker = Poker || {};

Poker.TableComHandler = Poker.AbstractConnectorHandler.extend({
    tableManager:null,
    pokerProtocolHandler:null,
    isSeated:false,
    inTableView : false,
    init:function (connector) {
        this.connector = connector;
    },
    onMyPlayerAction:function (actionType, amount) {
        console.log("onMyPlayerAction:" + actionType.text + " amount = " + amount);
        if (actionType.id == Poker.ActionType.JOIN.id) {
            this.joinTable();
        } else if (actionType.id == Poker.ActionType.LEAVE.id) {
            if (this.isSeated) {
                this.leaveTable();
            } else {
                this.unwatchTable();
            }
        } else if (actionType.id == Poker.ActionType.SIT_IN.id) {
            this.sitIn();
        } else if (actionType.id == Poker.ActionType.SIT_OUT.id) {
            this.sitOut();
        } else {
            if (actionType.id == Poker.ActionType.RAISE.id) {
                this.sendAction(this.pokerProtocolHandler.seq, this.getActionEnumType(actionType), amount, 0);
            } else {
                this.sendAction(this.pokerProtocolHandler.seq, this.getActionEnumType(actionType), amount, 0);
            }
        }
    },
    sitOut:function () {
        var sitOut = new com.cubeia.games.poker.io.protocol.PlayerSitoutRequest();
        sitOut.player = Poker.MyPlayer.id;
        this.sendGameTransportPacket(sitOut);
    },
    sitIn:function () {
        var sitIn = new com.cubeia.games.poker.io.protocol.PlayerSitinRequest();
        sitIn.player = Poker.MyPlayer.id;
        this.sendGameTransportPacket(sitIn);
    },
    buyIn : function(amount) {
        var buyInRequest = new com.cubeia.games.poker.io.protocol.BuyInRequest();
        buyInRequest.amount = amount;

        buyInRequest.sitInIfSuccessful = true;
        this.sendGameTransportPacket(buyInRequest);
    },
    getActionEnumType:function (actionType) {
        switch (actionType.id) {
            case Poker.ActionType.SMALL_BLIND.id:
                return com.cubeia.games.poker.io.protocol.ActionTypeEnum.SMALL_BLIND;
            case Poker.ActionType.BIG_BLIND.id:
                return com.cubeia.games.poker.io.protocol.ActionTypeEnum.BIG_BLIND;
            case Poker.ActionType.CALL.id:
                return com.cubeia.games.poker.io.protocol.ActionTypeEnum.CALL;
            case Poker.ActionType.CHECK.id:
                return com.cubeia.games.poker.io.protocol.ActionTypeEnum.CHECK;
            case Poker.ActionType.BET.id:
                return com.cubeia.games.poker.io.protocol.ActionTypeEnum.BET;
            case Poker.ActionType.RAISE.id:
                return com.cubeia.games.poker.io.protocol.ActionTypeEnum.RAISE;
            case Poker.ActionType.FOLD.id:
                return com.cubeia.games.poker.io.protocol.ActionTypeEnum.FOLD;
            default:
                console.log("Unhandled action " + actionType.text);
                return null;

        }
    },
    joinTable:function (tableId) {
        if(!tableId)  {
            tableId = this.tableManager.getTableId();
        }
        this.connector.joinTable(tableId, -1);
    },
    getTableId:function () {
        return this.tableManager.getTableId();
    },
    onOpenTable:function (tableId, capacity) {
        console.log("ON OPEN TABLE");
        this.onOpenTableAccepted(tableId, capacity);
        this.connector.watchTable(tableId);
    },
    onOpenTableAccepted:function (tableId, capacity) {
        var tableContainer = $("#tableView").get(0);
        var templateManager = new Poker.TemplateManager();
        var tableLayoutManager = new Poker.TableLayoutManager(tableId, tableContainer, templateManager, this, capacity);
        this.tableManager = new Poker.TableManager();

        this.tableId = tableId;
        this.inTableView = true;
        this.tableManager.createTable(tableId, capacity, comHandler.tableNames.get(tableId), [tableLayoutManager]);
        this.pokerProtocolHandler = new Poker.PokerProtocolHandler(this.tableManager, this);
    },
    handleSeatInfo:function (seatInfoPacket) {
        console.log(seatInfoPacket);
        console.log("seatInfo pid[" + seatInfoPacket.player.pid + "]  seat[" + seatInfoPacket.seat + "]");
        console.log(seatInfoPacket);
        this.tableManager.addPlayer(seatInfoPacket.seat, seatInfoPacket.player.pid, seatInfoPacket.player.nick);
        //seatPlayer(seatInfoPacket.player.pid, seatInfoPacket.seat, seatInfoPacket.player.nick);
    },
    handleNotifyLeave:function (notifyLeavePacket) {
        if (notifyLeavePacket.pid === Poker.MyPlayer.id) {
            console.log("I left this table, closing it.");
            this.tableManager.leaveTable();
            this.showLobby();
        } else {
            this.tableManager.removePlayer(notifyLeavePacket.pid);
        }
    },
    handleNotifyJoin:function (notifyJoinPacket) {
        console.log("NOTIFY JOIN!!");
        this.tableManager.addPlayer(notifyJoinPacket.seat, notifyJoinPacket.pid, notifyJoinPacket.nick);
    },
    handleJoinResponse:function (joinResponsePacket) {
        console.log(joinResponsePacket);
        console.log("join response seat = " + joinResponsePacket.seat + " player id = " + Poker.MyPlayer.id);
        if (joinResponsePacket.status == "OK") {
            this.tableManager.addPlayer(joinResponsePacket.seat, Poker.MyPlayer.id, Poker.MyPlayer.name);
            this.isSeated = true;
        } else {
            console.log("Join failed. Status: " + joinResponsePacket.status);

        }
    },
    handleUnwatchResponse:function (unwatchResponse) {
        console.log("Unwatch response = ");
        console.log(unwatchResponse);
        this.tableManager.leaveTable();
        this.showLobby();

    },
    handleLeaveResponse:function (leaveResponse) {
        console.log("leave response: ");
        console.log(leaveResponse);
        this.tableManager.leaveTable();
        this.showLobby();

    },
    leaveTable:function () {
        this.connector.leaveTable(this.tableManager.getTableId());
    },
    unwatchTable:function () {
        var unwatchRequest = new FB_PROTOCOL.UnwatchRequestPacket();
        unwatchRequest.tableid = this.tableId;
        this.connector.sendProtocolObject(unwatchRequest);
        comHandler.subscribeToCashGames();
    },
    handlePacket:function (packet) {
        switch (packet.classId) {
            case FB_PROTOCOL.NotifyJoinPacket.CLASSID :
                this.handleNotifyJoin(packet);
                break;
            case FB_PROTOCOL.NotifyLeavePacket.CLASSID :
                this.handleNotifyLeave(packet);
                break;
            case FB_PROTOCOL.SeatInfoPacket.CLASSID :
                this.handleSeatInfo(packet);
                break;
            case FB_PROTOCOL.JoinResponsePacket.CLASSID :
                this.handleJoinResponse(packet);
                break;
            case FB_PROTOCOL.GameTransportPacket.CLASSID :
                this.handleGameDataPacket(packet);
                break;
            case FB_PROTOCOL.UnwatchResponsePacket.CLASSID:
                this.handleUnwatchResponse(packet);
                break;
            case FB_PROTOCOL.LeaveResponsePacket.CLASSID:
                this.handleLeaveResponse(packet);
                break;
            case FB_PROTOCOL.WatchResponsePacket.CLASSID:
                this.handleWatchResponse(packet);
                break;
            case FB_PROTOCOL.MttSeatedPacket.CLASSID:
                this.handleSeatedAtTournamentTable(packet);
                break;
            case FB_PROTOCOL.MttRegisterResponsePacket.CLASSID:
                this.handleRegistrationResponse(packet);
                break;
            case FB_PROTOCOL.MttUnregisterResponsePacket.CLASSID:
                this.handleUnregistrationResponse(packet);
                break;
            case FB_PROTOCOL.MttTransportPacket.CLASSID:
                this.handleTournamentTransport(packet);
                break;
            case FB_PROTOCOL.MttPickedUpPacket.CLASSID:
                this.handleRemovedFromTournamentTable(packet);
                break;
            default :
                console.log("NO HANDLER");
                console.log(packet);
                break;
        }
    },
    handleWatchResponse:function (watchResponse) {
        if (watchResponse.status == "DENIED_ALREADY_SEATED") {
            this.joinTable(this.tableManager.getTableId());
        } else if (watchResponse.status == "CONNECTED") {
        }

    },
    handleGameDataPacket:function (packet) {
        this.pokerProtocolHandler.handleGameTransportPacket(packet);
    },
    handleTournamentTransport:function (packet) {
        console.log("Got tournament transport");
        console.log(packet);
        var valueArray =  FIREBASE.ByteArray.fromBase64String(packet.mttdata);
        var gameData = new FIREBASE.ByteArray(valueArray);
        var length = gameData.readInt(); // drugs.
        var classId = gameData.readUnsignedByte();

        var tournamentPacket = com.cubeia.games.poker.io.protocol.ProtocolObjectFactory.create(classId, gameData);
        console.log(tournamentPacket);
        switch (tournamentPacket.classId()) {
            case com.cubeia.games.poker.io.protocol.TournamentOut.CLASSID:
                this.handleTournamentOut(tournamentPacket);
                break;
            default:
                console.log("Unhandled tournament packet");
        }
    },
    handleTournamentOut: function (packet) {
        console.log("Tournament out:");
        console.log(packet);
        if (packet.position == 1) {
            dialogManager.displayGenericDialog({header:"Message", message:"Congratulations, you won the tournament!"});
        } else {
            dialogManager.displayGenericDialog({header:"Message", message:"You finished " + packet.position + " in the tournament."});
        }
    },
    handleRemovedFromTournamentTable:function (packet) {
        console.log("Removed from table " + packet.tableid + " in tournament " + packet.mttid + " keep watching? " + packet.keepWatching);
    },
    joinGame:function () {
        this.connector.joinTable(this.tableManager.getTableId(), -1);
    },
    showLobby:function () {
        this.inTableView = false;
        $("#lobbyView").show(); //should be somewhere else
        comHandler.subscribeToCashGames();
    },
    handleSeatedAtTournamentTable:function (seated) {
        console.log("I was seated in a tournament, opening table");
        console.log(seated)
        $("#lobbyView").hide();
        $("#tableView").show();
        this.onOpenTableAccepted(seated.tableid, 10)
        this.joinTable(seated.tableid);
    },
    handleRegistrationResponse:function (registrationResponse) {
        console.log("Registration response:");
        console.log(registrationResponse);
        if (registrationResponse.status == "OK") {
            comHandler.notifyRegisteredToTournament(registrationResponse.mttid);
            dialogManager.displayGenericDialog({header:"Message", message:"You successfully registered to tournament " + registrationResponse.mttid});
        } else {
            dialogManager.displayGenericDialog({header:"Message",
                message:"Your registration attempt to tournament " + registrationResponse.mttid + " was denied."});
        }
    },
    handleUnregistrationResponse:function (unregistrationResponse) {
        console.log("Unregistration response:");
        console.log(unregistrationResponse);
        if (unregistrationResponse.status == "OK") {
            comHandler.notifyUnregisteredFromTournament(unregistrationResponse.mttid);
            dialogManager.displayGenericDialog({header:"Message",
                message:"You successfully unregistered from tournament " + unregistrationResponse.mttid});
        } else {
            dialogManager.displayGenericDialog({header:"Message",
                message:"Your unregistration attempt from tournament " + unregistrationResponse.mttid + " was denied."});
        }
    }

});