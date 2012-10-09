"use strict";
var Poker = Poker || {};

Poker.CommunityCard = Poker.Card.extend({
    cardString : null,
    templateManager : null,
    init : function(id,cardString,templateManager) {
        this._super(id,cardString,templateManager);
    },
    getTemplate : function() {
        return this.templateManager.getTemplate("communityCardTemplate");
    },
    getCardDivId : function() {
        return "communityCard-"+this.id;
    }
});