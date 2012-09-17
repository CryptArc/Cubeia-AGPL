"use strict";
var Poker = Poker || {};
/**
 * Holds the a player states including id,name credit status and cards
 * @type Poker.Player
 */
Poker.Player = Class.extend({
    name : null,
    id :-1,
    balance : 0,
    status : null,
    lastActionType : null,
    cards : [],
    init : function(id,name) {
        this.name = name;
        this.id = id;
        this.status = Poker.PlayerStatus.SITTING_IN;
    },
    /**
     * Adds a card to the player
     * @param card to add
     */
    addCard : function(card) {
        this.cards.push(card);
    }
});