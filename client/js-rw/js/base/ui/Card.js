"use strict";
var Poker = Poker || {};

/**
 * Handles a poker card in the UI
 * @type {Poker.Card}
 */
Poker.Card = Class.extend({
    cardString:null,
    id:-1,
    templateManager:null,
    cardImage : null,
    cardElement : null,
    init:function (id, cardString, templateManager) {
        this.templateManager = templateManager;
        this.id = id;
        if (cardString == "  ") {
            cardString = "back";
        }
        this.cardString = cardString;
    },
    /**
     * Creates and returns the html for the card, does not add anything
     * to the DOM
     * @return {*}
     */
    render : function () {
        var t = this.getTemplate();
        var output = Mustache.render(t, {domId:this.id, cardString:this.cardString});
        return output;
    },
    /**
     * Exposes a card, from displaying the back to the actual card
     * updates DOM
     * @param cardString
     */
    exposeCard : function(cardString) {
        this.cardString = cardString;
        this.cardImage.attr("src","images/cards/"+this.cardString+".svg");
    },
    /**
     * Returns the JQuery card element
     * @return {*}
     */
    getJQElement:function () {
        if(this.cardElement==null) {
            this.cardElement =  $("#" + this.getCardDivId());
            this.cardImage = this.cardElement.find("img");
        }
        return this.cardElement;
    },
    getDOMElement : function() {
      return this.getJQElement().get(0);
    },
    getTemplate:function () {
        return this.templateManager.getTemplate("playerCardTemplate");
    },
    getCardDivId:function () {
        return "playerCard-" + this.id;
    }
});