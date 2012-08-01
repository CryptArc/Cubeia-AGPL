PokerCards = function() {
    this.clientCards = {};
};

PokerCards.prototype.generateCardDivId = function(cardFieldDivId, cardId) {
    var time = new Date().getTime();
    var cardDivId = "card_"+cardId+"_"+time;
    return cardDivId;
};

PokerCards.prototype.getClientCardByCardId = function(cardId) {
    return this.clientCards[cardId];
};

PokerCards.prototype.setClientCardDivImageUrl = function(cardId, cardUrl) {
    var card = this.getClientCardByCardId(cardId);
    card.image = "url(./images/cards-75/"+cardUrl+")";
    document.getElementById(card.divId).style.backgroundImage = card.image;
};

PokerCards.prototype.clearAllCards = function() {
    for (index in this.clientCards) {
        uiElementHandler.removeDivElement(this.clientCards[index].divId);
    }
    this.clientCards = {};
};

PokerCards.prototype.addClientCardWithIdAndUrl = function(cardId, cardUrl) {
    var card = {};
    card.id = cardId;
    this.clientCards[card.id] = card;
    var cardDivId = "clientCardDiv_"+cardId;
    console.log(cardDivId);
    card.divId = cardDivId;
    uiElementHandler.createDivElement("body", card.divId, "", "poker_card", null);


    this.setClientCardDivImageUrl(cardId, cardUrl);
    return card;

}

PokerCards.prototype.clearSeatEntityCards = function(seatEntity) {
    var divId = seatEntity.ui.cardFieldDivId;
    uiElementHandler.removeElementChildren(0, divId);

};

PokerCards.prototype.handCardIdToPlayerEntity = function(cardId, playerEntity, cardUrl) {
    var seatEntity = view.table.getSeatBySeatNumber(playerEntity.state.seatId)
    var cardFieldDivId = seatEntity.ui.cardFieldDivId;
    var card = this.addClientCardWithIdAndUrl(cardId, cardUrl);
    var cardDivId = card.divId;
    uiElementHandler.setDivElementParent(cardDivId, cardFieldDivId);
};

/**
 * Get a card
 * @param {POKER_PROTOCOL.GameCard} gamecard
 */
PokerCards.prototype.getCardUrl = function(gamecard) {
	
    var ranks = "23456789TJQKA ";
    var suits = "CDHS ";
    var cardString = ranks.charAt(gamecard.rank) + suits.charAt(gamecard.suit);
    if ( cardString === "  ") {
        return "cubeia_back.png";
    }
    return cardString + "-75.png";
};