PokerDealer = function() {
    this.dealerButtonEntityId = "dealer_button_entity"
};

/**
 * Deal a card
 * @param {Number} pid
 * @param {POKER_PROTOCOL.CardToDeal} cardToDeal
 */
PokerDealer.prototype.dealCardIdToPid = function(cardToDeal) {
    console.log(cardToDeal);
    var playerEntity = entityHandler.getEntityById(playerHandler.getPlayerEntityIdByPid(cardToDeal.player));
    var cardUrl = pokerCards.getCardUrl(cardToDeal.card);
    console.log("Player Card: -- CardId: ["+cardToDeal.card.cardId+"], cardurl ["+cardUrl+"]  rank[" + cardToDeal.card.rank +"]  suit[" + cardToDeal.card.suit +"]");
    pokerCards.handCardIdToPlayerEntity(cardToDeal.card.cardId, playerEntity, cardUrl);
};


/**
 * Deal a public card
 * @param {POKER_PROTOCOL.GameCard} gameCard
 */
PokerDealer.prototype.dealPublicCard = function(gameCard) {
    console.log("Deal Community Card: "+gameCard.cardId);
    var cardUrl = pokerCards.getCardUrl(gameCard);
    console.log(cardUrl)
    var card = pokerCards.addClientCardWithIdAndUrl(gameCard.cardId, cardUrl);
    console.log(card)
    view.communityCards.setClientCardAsCommunityCard(card);
};

	

/**
 * Handle ExposePrivateCards
 * @param {POKER_PROTOCOL.ExposePrivateCards} exposePrivateCards
 */
PokerDealer.prototype.exposePrivateCards = function(exposeData) {
	var cardUrl = pokerCards.getCardUrl(exposeData.card);
    pokerCards.setClientCardDivImageUrl(exposeData.card.cardId, cardUrl)
};

PokerDealer.prototype.addPlayerCardsComponent = function(playerEntity) {
    playerEntity.cards = {};
};

PokerDealer.prototype.createDealerButton = function() {
    var dealerButtonEntity = entityHandler.addEntity(this.dealerButtonEntityId);
    entityHandler.addUiComponent(dealerButtonEntity, "D", "dealer_button", null);
//    uiElementHandler.createDivElement(dealerButtonEntity.ui.divId, "dealer_button_label", "D", "dealer_button_label", null);

    entityHandler.addSpatial("body", dealerButtonEntity, 0, 0);
    console.log(dealerButtonEntity)
};

PokerDealer.prototype.moveDealerButton = function(seatId) {
	var dealerButton = entityHandler.getEntityById(this.dealerButtonEntityId);
    var seatEntity = entityHandler.getEntityById(view.seatHandler.getSeatEntityIdBySeatNumber(seatId));
    if (!seatEntity) return;

    var targetDivId = seatEntity.ui.dealerButtonSlotDivId;
    console.log("Move Dealer Button")
    console.log(dealerButton)

    uiElementHandler.setDivElementParent(dealerButton.ui.divId, targetDivId);
};


PokerDealer.prototype.startNewHand = function() {
    console.log("Starting New Hand - Clearing all Cards!")
    view.textFeedback.clearAllSeatSpaceTextFeedback();
    view.table.clearPot();
    pokerCards.clearAllCards();

};
