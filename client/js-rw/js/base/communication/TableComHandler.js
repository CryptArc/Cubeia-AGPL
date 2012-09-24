var Poker = Poker || {};

Poker.TableComHandler = Poker.AbstractConnectorHandler.extend({
    tableManager : null,
    pokerProtocolHandler : null,
    isSeated : false,
    init : function(connector) {
        this.connector = connector;
    },
    onMyPlayerAction : function(actionType,amount) {
        console.log("onMyPlayerAction:"+actionType.text + " amount = " + amount);
        if(actionType.id == Poker.ActionType.JOIN.id){
            this.joinTable();
        } else if(actionType.id == Poker.ActionType.LEAVE.id) {
            if (this.isSeated) {
                this.leaveTable();
            } else {
                this.unwatchTable();
            }
        } else if(actionType.id == Poker.ActionType.SIT_IN.id) {
            this.sitIn();
        } else if(actionType.id == Poker.ActionType.SIT_OUT.id) {
            this.sitOut();
        } else {
            if(actionType.id == Poker.ActionType.RAISE.id) {
                this.sendAction(this.pokerProtocolHandler.seq, this.getActionEnumType(actionType), amount, 0);
            } else {
                this.sendAction(this.pokerProtocolHandler.seq, this.getActionEnumType(actionType), amount, 0);
            }
        }
    },
    sitOut : function() {
        var sitOut = new com.cubeia.games.poker.io.protocol.PlayerSitoutRequest();
        sitOut.player = Poker.MyPlayer.id;
        this.sendGameTransportPacket(sitOut);
    },
    sitIn : function() {
        var sitIn = new com.cubeia.games.poker.io.protocol.PlayerSitinRequest();
        sitIn.player = Poker.MyPlayer.id;
        this.sendGameTransportPacket(sitIn);
    },
    getActionEnumType : function(actionType) {
        switch(actionType.id) {
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
    joinTable : function() {
        this.connector.joinTable(this.tableManager.getTableId(), -1);
    },
    getTableId : function() {
        return this.tableManager.getTableId();
    },
    onOpenTable : function(tableId,capacity){
        console.log("ON OPEN TABLE")
        this.onOpenTableAccepted(tableId,capacity);
        this.connector.watchTable(tableId);
    },
    onOpenTableAccepted : function(tableId,capacity) {
        var tableContainer = $("#tableView").get(0);
        var templateManager = new Poker.TemplateManager();
        var tableLayoutManager = new Poker.TableLayoutManager(tableContainer,templateManager,this,capacity);
        this.tableManager = new Poker.TableManager();

        this.tableId = tableId;
        this.tableManager.createTable(tableId,capacity,[tableLayoutManager]);
        this.pokerProtocolHandler = new Poker.PokerProtocolHandler(this.tableManager,this);
    },
    handleSeatInfo : function(seatInfoPacket) {
        console.log(seatInfoPacket);
        console.log("seatInfo pid[" + seatInfoPacket.player.pid + "]  seat[" +  seatInfoPacket.seat +"]");
        console.log(seatInfoPacket);
        this.tableManager.addPlayer(seatInfoPacket.seat, seatInfoPacket.player.pid, seatInfoPacket.player.nick);
        //seatPlayer(seatInfoPacket.player.pid, seatInfoPacket.seat, seatInfoPacket.player.nick);
    },
    handleNotifyLeave : function(notifyLeavePacket) {
        if (notifyLeavePacket.pid === Poker.MyPlayer.id) {
            console.log("I left this table, closing it.");
            this.tableManager.leaveTable();
            this.showLobby();
        } else {
            this.tableManager.removePlayer(notifyLeavePacket.pid);
        }
    },
    handleNotifyJoin : function(notifyJoinPacket) {
        console.log("NOTIFY JOIN!!");
        this.tableManager.addPlayer(notifyJoinPacket.seat,notifyJoinPacket.pid,notifyJoinPacket.nick);
    },
    handleJoinResponse : function(joinResponsePacket) {
        console.log(joinResponsePacket);
        console.log("join response seat = " + joinResponsePacket.seat + " player id = " + Poker.MyPlayer.id);
        if (joinResponsePacket.status == "OK") {
            this.tableManager.addPlayer(joinResponsePacket.seat,Poker.MyPlayer.id,Poker.MyPlayer.name);
            this.isSeated = true;
        } else {
            console.log("Join failed. Status: " + joinResponsePacket.status);

        }
    },
    handleUnwatchResponse : function(unwatchResponse) {
        console.log("Unwatch response = ");
        console.log(unwatchResponse);
        this.tableManager.leaveTable();
        this.showLobby();
    },
    handleLeaveResponse : function(leaveResponse) {
        console.log("leave response: ");
        console.log(leaveResponse);
        this.tableManager.leaveTable();
        this.showLobby();
    },
    leaveTable : function() {
        this.connector.leaveTable(this.tableManager.getTableId());
    },
    unwatchTable : function() {
        var unwatchRequest = new FB_PROTOCOL.UnwatchRequestPacket();
        unwatchRequest.tableid = this.tableId;
        this.connector.sendProtocolObject(unwatchRequest)
        comHandler.subscribeToLobby();
    },
    handlePacket : function(protocolObject) {
        switch (protocolObject.classId) {
            case FB_PROTOCOL.NotifyJoinPacket.CLASSID :
                this.handleNotifyJoin(protocolObject);
                break;
            case FB_PROTOCOL.NotifyLeavePacket.CLASSID :
                this.handleNotifyLeave(protocolObject);
                break;
            case FB_PROTOCOL.SeatInfoPacket.CLASSID :
                this.handleSeatInfo(protocolObject);
                break;
            case FB_PROTOCOL.JoinResponsePacket.CLASSID :
                this.handleJoinResponse(protocolObject);
                break;
            case FB_PROTOCOL.GameTransportPacket.CLASSID :
                this.handleGameDataPacket(protocolObject);
                break;
            case FB_PROTOCOL.UnwatchResponsePacket.CLASSID:
                this.handleUnwatchResponse(protocolObject);
                break;
            case FB_PROTOCOL.LeaveResponsePacket.CLASSID:
                console.log("leave response = ");
                console.log(protocolObject);
                this.handleLeaveResponse(protocolObject);
                $("#lobbyView").show(); //should be somewhere else
                break;
            case FB_PROTOCOL.WatchResponsePacket.CLASSID:
                if(protocolObject.status == "DENIED_ALREADY_SEATED")  {
                    this.joinTable();
                } else if(protocolObject.status == "CONNECTED") {

                }
                break;
            default :
                console.log("NO HANDLER");
                console.log(protocolObject);
                break;
        }
    },
    handleGameDataPacket : function(packet) {
        this.pokerProtocolHandler.handleGameTransportPacket(packet);
    },
    joinGame : function() {
        this.connector.joinTable(this.tableManager.getTableId(), -1);
    },
    showLobby : function() {
        $("#lobbyView").show(); //should be somewhere else
        comHandler.subscribeToLobby();
    }
});