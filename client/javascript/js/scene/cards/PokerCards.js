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
    if (!card) return;
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
    var cardDivId = "clientCardDiv_" + cardId;
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
    if (playerEntity.pid == playerHandler.myPlayerPid)
    {
        var card = this.addClientCardWithIdAndUrl(cardId, cardUrl);
        var cardDivId = card.divId;
        var cardHolderEntity = entityHandler.getEntityById("ownCardsAreaEntityId");
        uiElementHandler.setDivElementParent(cardDivId, cardHolderEntity.ui.divId);
        document.getElementById(cardDivId).className = "hud_card";
    }
    else
    {
        var seatEntity = view.table.getSeatBySeatNumber(playerEntity.state.seatId)
        var cardFieldDivId = seatEntity.ui.cardFieldDivId;
        var card = this.addClientCardWithIdAndUrl(cardId, cardUrl);
        var cardDivId = card.divId;
        uiElementHandler.setDivElementParent(cardDivId, cardFieldDivId);
    }
};

PokerCards.prototype.initOwnCardArea = function() {
    var tableEntity = entityHandler.getEntityById(view.table.entityId);
    var uiEntity = entityHandler.addEntity("ownCardsAreaEntityId");
    uiEntity.cards = {};

    entityHandler.addUiComponent(uiEntity, "", "own_card_area", null);

    var posX = 42;
    var posY = 64.5;

    tableEntity.ui.ownCardsDivId = uiEntity.ui.divId
    entityHandler.addSpatial("body", uiEntity, posX, posY);
    uiElementHandler.setDivElementParent(uiEntity.ui.divId, uiEntity.spatial.transform.anchorId);
    uiElementHandler.setDivElementParent(uiEntity.spatial.transform.anchorId, tableEntity.ui.divId);

    document.getElementById(uiEntity.ui.divId).style.width = 200;
    document.getElementById(uiEntity.ui.divId).style.left = -100;
    document.getElementById(uiEntity.ui.divId).style.top = -30;

    view.spatialManager.positionVisualEntityAtSpatial(uiEntity);
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
        return "blue_card_back2.png";
    }
    return cardString + "-75.png";
};