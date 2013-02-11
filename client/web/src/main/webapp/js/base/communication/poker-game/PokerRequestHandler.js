var Poker = Poker || {};

Poker.PokerRequestHandler = Class.extend({
    tableId : null,

    /**
     * @type Poker.TableManager
     */
    tableManager : null,

    /**
     * @constructor
     * @param {Number} tableId
     */
    init : function(tableId){
        this.tableId = tableId;
        this.tableManager = Poker.AppCtx.getTableManager();
    },
    onMyPlayerAction : function (actionType, amount) {
        console.log("ON my player action");
        console.log(actionType);
        console.log(amount);
        var tableRequestHandler = new Poker.TableRequestHandler(this.tableId);
        if (actionType.id == Poker.ActionType.JOIN.id) {
            tableRequestHandler.joinTable();
        } else if (actionType.id == Poker.ActionType.LEAVE.id) {
            var table = this.tableManager.getTable(this.tableId);
            if (table.tournamentClosed) {
                console.log("Tournament is closed, will close the table without telling the server.");
                this.tableManager.leaveTable(this.tableId);
            } else if (this.tableManager.isSeated(this.tableId)) {
                tableRequestHandler.leaveTable();
            } else {
                tableRequestHandler.unwatchTable();
            }
        } else if (actionType.id == Poker.ActionType.SIT_IN.id) {
            this.sitIn();
        } else if (actionType.id == Poker.ActionType.SIT_OUT.id) {
            this.sitOut();
        } else {
            this.sendAction(Poker.ActionUtils.getActionEnumType(actionType), amount, 0);
        }
    },
    sendAction : function(actionType, betAmount, raiseAmount) {
        var action = Poker.ActionUtils.getPlayerAction(this.tableId,Poker.PokerSequence.getSequence(this.tableId),
            actionType, betAmount, raiseAmount);
        this.sendGameTransportPacket(action);
    },
    sendGameTransportPacket : function(gamedata) {
        var connector = Poker.AppCtx.getConnector();
        connector.sendStyxGameData(0, this.tableId, gamedata);
        console.log("package sent to table " + this.tableId);
        console.log(gamedata);
    },
    buyIn : function(amount) {
        var buyInRequest = new com.cubeia.games.poker.io.protocol.BuyInRequest();
        buyInRequest.amount = amount;

        buyInRequest.sitInIfSuccessful = true;
        this.sendGameTransportPacket(buyInRequest);
    },
    sitOut : function () {
        var sitOut = new com.cubeia.games.poker.io.protocol.PlayerSitoutRequest();
        sitOut.player = Poker.MyPlayer.id;
        this.sendGameTransportPacket(sitOut);
    },
    sitIn : function () {
        var sitIn = new com.cubeia.games.poker.io.protocol.PlayerSitinRequest();
        sitIn.player = Poker.MyPlayer.id;
        this.sendGameTransportPacket(sitIn);
    }
});