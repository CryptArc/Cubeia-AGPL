PokerDealer = function() {
    this.dealerButtonEntityId = "dealer_button_entity";
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
    console.log(cardUrl);
    var card = pokerCards.addClientCardWithIdAndUrl(gameCard.cardId, cardUrl);
    var cardElement = document.getElementById(card.divId);
    console.log("Comm card: " + card.divId + " el " + cardElement);
    TweenMax.to(cardElement, 0.6, {css:{opacity:1}});
    TweenMax.to(cardElement, 0.4, {css:{top:0}});
    console.log(card);
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

    // Set the button's position as fixed and use its global coordinates.
    button.css('position','fixed');
    button.css('left', offset.left);
    button.css('top', offset.top);

    // Create an onComplete-function that will detach the element from the old parent and add it to the new.
    function onComplete() {
        button.detach();
        button.appendTo(target);

        // Clear the css properties so it get the parent's properties instead.
        button.css('position','');
        button.css('left', '');
        button.css('top', '');
    }

    // Animate the coordinates to the global coordinates of the destination.
    TweenMax.to(button, 0.4, {css:{top:targetOffset.top, left:targetOffset.left}});
};


PokerDealer.prototype.startNewHand = function() {
    view.textFeedback.clearAllSeatSpaceTextFeedback();
    view.table.clearPot();
    pokerCards.clearAllCards();

};
