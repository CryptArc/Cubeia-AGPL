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
    },
    appendCommunityCards : function(cards) {
        this.appendTemplate("communityCardsLogTemplate", { cards : cards });
    },
    appendExposedCards : function(playerCards) {
        this.appendTemplate("playerCardsExposedLogTemplate", playerCards);
    },
    appendHandStrength : function(player,hand,cardStrings) {
        this.appendTemplate("playerHandStrengthLogTemplate", {player : player, hand : hand, cardStrings : cardStrings});
    },
    appendPotTransfer : function(player, potId, amount) {
        this.appendTemplate("potTransferLogTemplate", {player : player, potId : potId, amount : Poker.Utils.formatCurrency(amount) });
    },
    appendNewHand : function(handId) {
        this.appendTemplate("newHandLogTemplate", {handId : handId});
    }
});