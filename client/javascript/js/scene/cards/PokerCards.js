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
    console.log("setting url = " + cardUrl);
    card.image = "url(./images/cards-svg/"+cardUrl+")";
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

};

PokerCards.prototype.clearSeatEntityCards = function(seatEntity) {
    console.log("clearing cards: entity = " + seatEntity);
    if (!seatEntity) return;
    var divId = seatEntity.ui.cardFieldDivId;
    uiElementHandler.removeElementChildren(0, divId);
};

PokerCards.prototype.setCardsFolded = function(seatEntity, playerId) {
    console.log("Player " + playerId + " folded. I am " + playerHandler.myPlayerPid);
    if (playerId == playerHandler.myPlayerPid) {
        console.log("I folded");
        var cardHolderEntity = entityHandler.getEntityById("ownCardsAreaEntityId");
        var cardHolderElement = document.getElementById(cardHolderEntity.ui.divId);
        TweenMax.to(cardHolderElement, 0.5, {css:{opacity:0.2}});
    } else {
        if (!seatEntity) return;
        var cards = seatEntity.occupant.cardIds;
        for (var i = 0; i < cards.length; i++) {
            console.log("setting cards folded = " + cards[i]);
            this.setClientCardDivImageUrl(cards[i], "gray-back.svg");
        }
    }
};

PokerCards.prototype.handCardIdToPlayerEntity = function(cardId, playerEntity, cardUrl) {
    console.log("Dealing card " + cardId + " to entity " + playerEntity);
    if (playerEntity.pid == playerHandler.myPlayerPid) {
        // Give card to self.
        var card = this.addClientCardWithIdAndUrl(cardId, cardUrl);
        var cardDivId = card.divId;
        var cardHolderEntity = entityHandler.getEntityById("ownCardsAreaEntityId");
        document.getElementById(cardHolderEntity.ui.divId).style.opacity = 1;
        uiElementHandler.setDivElementParent(cardDivId, cardHolderEntity.ui.divId);
        var cardElement = document.getElementById(cardDivId);
        cardElement.className = "hud_card";
        TweenMax.to(cardElement, 0.6, {css:{opacity:1}});
        TweenMax.to(cardElement, 0.4, {css:{top:0}});
    } else {
        var seatEntity = view.table.getSeatBySeatNumber(playerEntity.state.seatId);
        if (!seatEntity) {
            console.log("Cannot find seat entity for seatId: " + playerEntity.state.seatId);
        } else {
            var cardFieldDivId = seatEntity.ui.cardFieldDivId;
            var card = this.addClientCardWithIdAndUrl(cardId, cardUrl);
            var cardDivId = card.divId;
            uiElementHandler.setDivElementParent(cardDivId, cardFieldDivId);
            this.addCardIdToEntity(seatEntity,cardId);
            var cardElement = document.getElementById(cardDivId);
            TweenMax.to(cardElement, 0.6, {css:{opacity:1}});
            TweenMax.to(cardElement, 0.4, {css:{top:0}});
        }
    }
};
PokerCards.prototype.addCardIdToEntity = function(seatEntity, cardId) {
	if(!seatEntity.occupant.cardIds) {
		seatEntity.occupant.cardIds = [];
	}
	if(!this.cardsExist(seatEntity,cardId)){
		seatEntity.occupant.cardIds.push(cardId);
	}
};
PokerCards.prototype.cardsExist = function(seatEntity,cardId) {
	var divs = seatEntity.occupant.cardIds;
	for(var i = 0; i<divs.length; i++) {
		if(divs[i]==cardId) {
			return true;
		}
	}
	return false;
};

PokerCards.prototype.initOwnCardArea = function() {
    var tableEntity = entityHandler.getEntityById(view.table.entityId);
    var uiEntity = entityHandler.addEntity("ownCardsAreaEntityId");
    uiEntity.cards = {};

    entityHandler.addUiComponent(uiEntity, "", "own_card_area", null);

    var posX = 40;
    var posY = 64.5;

    tableEntity.ui.ownCardsDivId = uiEntity.ui.divId;
    entityHandler.addSpatial("body", uiEntity, posX, posY);
    uiElementHandler.setDivElementParent(uiEntity.ui.divId, uiEntity.spatial.transform.anchorId);
    uiElementHandler.setDivElementParent(uiEntity.spatial.transform.anchorId, tableEntity.ui.divId);

    document.getElementById(uiEntity.ui.divId).style.width = 200;
    document.getElementById(uiEntity.ui.divId).style.left = -120;
    document.getElementById(uiEntity.ui.divId).style.top = -30;

    view.spatialManager.positionVisualEntityAtSpatial(uiEntity);
};

/**
 * Get a card
 * @param {POKER_PROTOCOL.GameCard} gamecard
 */
PokerCards.prototype.getCardUrl = function(gamecard) {
	
    var ranks = "23456789tjqka ";
    var suits = "cdhs ";
    var cardString = ranks.charAt(gamecard.rank) + suits.charAt(gamecard.suit);
    if ( cardString === "  ") {
        return "back.svg";
    }
    return cardString + ".svg";
};