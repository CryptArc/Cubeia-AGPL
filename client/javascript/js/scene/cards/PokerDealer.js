PokerDealer = function() {
    this.dealerButtonEntityId = "dealer_button_entity";
};

/**
 * Deal a card
 * @param {Number} pid
 * @param {POKER_PROTOCOL.CardToDeal} cardToDeal
 */
PokerDealer.prototype.dealCardIdToPid = function(cardToDeal) {
    var playerEntity = entityHandler.getEntityById(playerHandler.getPlayerEntityIdByPid(cardToDeal.player));
    var cardUrl = pokerCards.getCardUrl(cardToDeal.card);
    pokerCards.handCardIdToPlayerEntity(cardToDeal.card.cardId, playerEntity, cardUrl);

    var playerEntityId = playerHandler.getPlayerEntityIdByPid(cardToDeal.player);
    if(playerEntityId) {
	    var seatEntity = view.table.getSeatBySeatedEntityId(playerEntityId);
	    if (seatEntity) {
	    	document.getElementById(seatEntity.spatial.transform.anchorId).style.opacity = 1;     	
	    }
    }
};


/**
 * Deal a public card
 * @param {POKER_PROTOCOL.GameCard} gameCard
 */
PokerDealer.prototype.dealCommunityCard = function(gameCard) {
    console.log("Deal Community Card: "+gameCard.cardId);
    var cardUrl = pokerCards.getCardUrl(gameCard);
    var card = pokerCards.addClientCardWithIdAndUrl(gameCard.cardId, cardUrl);
    var cardElement = document.getElementById(card.divId);
    animator.addAnimation(new Animation(cardElement,0.3,{opacity: 1, top: 0}));

    view.communityCards.setClientCardAsCommunityCard(card);
};

	

/**
 * Handle ExposePrivateCards
 * @param {POKER_PROTOCOL.ExposePrivateCards} exposePrivateCards
 */
PokerDealer.prototype.exposePrivateCards = function(exposeData) {
	var cardUrl = pokerCards.getCardUrl(exposeData.card);
    pokerCards.setClientCardDivImageUrl(exposeData.card.cardId, cardUrl);
};

PokerDealer.prototype.addPlayerCardsComponent = function(playerEntity) {
    playerEntity.cards = {};
};

PokerDealer.prototype.createDealerButton = function() {
    var dealerButtonEntity = entityHandler.addEntity(this.dealerButtonEntityId);
    entityHandler.addUiComponent(dealerButtonEntity, "D", "dealer_button", null);
    entityHandler.addSpatial("body", dealerButtonEntity, 0, 0);

};

PokerDealer.prototype.moveDealerButton = function(seatId) {
	var dealerButton = entityHandler.getEntityById(this.dealerButtonEntityId);
    var seatEntity = entityHandler.getEntityById(view.seatHandler.getSeatEntityIdBySeatNumber(seatId));
    if (!seatEntity) return;

    var button = $('#' + dealerButton.ui.divId);
    var offset = button.offset();
    var target = $('#' + seatEntity.ui.dealerButtonSlotDivId);
    var targetOffset = target.offset();

    animator.addAnimation(new Animation(button.get(0),0.6,{top: targetOffset.top, left: targetOffset.left}));
};


PokerDealer.prototype.startNewHand = function() {
    view.textFeedback.clearAllSeatSpaceTextFeedback();
    view.table.clearPot();
    pokerCards.clearAllCards();

};
