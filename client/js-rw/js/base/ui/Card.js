"use strict";
var Poker = Poker || {};

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
    render:function () {
        var t = this.getTemplate();
        var output = Mustache.render(t, {domId:this.id, cardString:this.cardString});
        return output;
    },
    exposeCard : function(cardString) {
        this.cardString = cardString;
        this.cardImage.attr("src","images/cards/"+this.cardString+".svg");
    },
    getDOMElement:function () {
        if(this.cardElement==null) {
            this.cardElement =  $("#" + this.getCardDivId());
            this.cardImage = this.cardElement.find("img");
        }
        return this.cardElement.get(0);


    },
    getTemplate:function () {
        return this.templateManager.getTemplate("playerCardTemplate");
    },
    getCardDivId:function () {
        return "playerCard-" + this.id;
    }
});