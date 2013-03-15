var Poker = Poker || {};

/**
 *
 * @type {Poker.TableEventLog}
 * @extends {Poker.Log}
 */
Poker.TableEventLog = Poker.Log.extend({
    init : function(logContainer) {
        this._super(logContainer);
    },
    appendAction : function(player, actionType, amount) {
        var data = {
            name : player.name,
            action : actionType.text,
            amount : amount,
            showAmount : (amount!="0")
        };
        this.appendTemplate("playerActionLogTemplate",data);
    }
});