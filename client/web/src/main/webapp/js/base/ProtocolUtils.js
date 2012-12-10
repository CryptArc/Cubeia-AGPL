"user strict";
Poker.ProtocolUtils = Class.extend({
    init : function(){},
    readParam : function(key,params) {
        for (var i = 0; i < params.length; i++) {
            var object = params[i];

            if (object.key == key) {
                var p = null;
                var valueArray = FIREBASE.ByteArray.fromBase64String(object.value);
                var byteArray = new FIREBASE.ByteArray(valueArray);
                if (object.type == 1) {
                    p = byteArray.readInt();
                } else {
                    p = byteArray.readString();
                }
                return p;
            }
        }
        return null;
    },
    extractTournamentData : function(snapshot) {
        var params = snapshot.params;
        var self = this;
        var param = function(name) {
            var val = self.readParam(name,params);
            if(val == null) {
                val = "N/A";
            }
            return val;
        };

        var data = {
            id: snapshot.mttid,
            name: param("NAME"),
            speed: "N/A",
            capacity: param("CAPACITY"),
            seated: param("REGISTERED"),
            biggestStack : param("BIGGEST_STACK"),
            smallestStack : param("SMALLEST_STACK"),
            averageStack : param("AVERAGE_STACK"),
            playersLeft : param("PLAYERS_LEFT"),
            blinds:"N/A",
            type:"NL",
            tableStatus:"open",
            ante:"N/A"
        };

        return data;
    },
    extractTableData : function(snapshot) {
        var params = snapshot.params;
        var self = this;
        var param = function(name) {
            var val = self.readParam(name,params);
            if(val == null) {
                val = "N/A";
            }
            return val;
        };

        var data = {
            id: snapshot.tableid,
            name: snapshot.name,
            speed: param("SPEED"),
            capacity:snapshot.capacity,
            seated: snapshot.seated,
            blinds: (Poker.Utils.formatBlinds(param("SMALL_BLIND")) + "/" + Poker.Utils.formatBlinds(param("BIG_BLIND"))),
            type: this.getBettingModel(param("BETTING_GAME_BETTING_MODEL")),
            tableStatus: this.getTableStatus(snapshot.seated, snapshot.capacity),
            smallBlind: param("SMALL_BLIND")
        };

        return data;
    },
    getTableStatus:function (seated, capacity) {
        if (seated == capacity) {
            return "full";
        }
        return "open";
    },
    getBettingModel:function (model) {
        if (model == "NO_LIMIT") {
            return "NL"
        } else if (model == "POT_LIMIT") {
            return "PL";
        } else if (model == "FIXED_LIMIT") {
            return "FL";
        }
        return model;
    },
    getProtocolObject : function() {

    },
    getActionEnumType : function (actionType) {
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
    }
});
Poker.ProtocolUtils = new Poker.ProtocolUtils();