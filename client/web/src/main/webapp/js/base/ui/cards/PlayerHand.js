"use strict";
var Poker = Poker || {};

/**
 * Handles a poker hand in the UI
 * @type {Poker.PlayerHand}
 */
Poker.PlayerHand = Class.extend({

    cards:null,
    discards:null,
    minDiscards:1,
    maxDiscards:1,

    init : function() {
        this.cards = new Poker.Map();
        this.discards = new Poker.Map();
    },

    enableDiscards : function(minDiscards,maxDiscards) {
        this.minDiscards = minDiscards;
        this.maxDiscards = maxDiscards;
    },

    disableDiscards : function() {

    },

    addCard : function(card) {
        this.cards.put(card.id, card);
        var self = this;
        card.getJQElement().click(function() {
            self.toggleDiscardedCard(card.id);
        });
    },
    discardCards : function(cardsToDiscard) {
        for(var c in cardsToDiscard) {
            var card = this.cards.remove(cardsToDiscard[c]);
            card.getJQElement().off().remove();
        }
    },
    removeAllCards : function() {
        this.cards = new Poker.Map();
        this.discards = new Poker.Map();
    },

    toggleDiscardedCard : function(cardId) {
        if (this.discards.contains(cardId)) {
            var card = this.discards.remove(cardId);
        } else {
            if ( this.discards.size() < this.maxDiscards ) {
                this.discards.put(cardId, cardId);
            }
        }
    },
    getDiscards : function() {
        var discards = this.discards.values();
        console.log("DISCARDS: %O", discards);
        return discards;
    }
});