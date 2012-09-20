"use strict";
var Poker = Poker || {};
/**
 * Holds the a player states including id,name credit tableStatus and cards
 * @type Poker.Player
 */
Poker.Player = Class.extend({
    name : null,
    id :-1,
    balance : 0,
    tableStatus : null,
    lastActionType : null,
    cards : [],
    init : function(id,name) {
        this.name = name;
        this.id = id;
        this.tableStatus = Poker.PlayerTableStatus.SITTING_IN;
    },
    /**
     * Adds a card to the player
     * @param card to add
     */
    addCard : function(card) {
        this.cards.push(card);
    }
});