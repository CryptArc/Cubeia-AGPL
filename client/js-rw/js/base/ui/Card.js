"use strict";
var Poker = Poker || {};

Poker.Card = Class.extend({
   cardString : null,
   id :-1,
   templateManager : null,
   init : function(id, cardString,templateManager) {
        this.templateManager = templateManager;
        this.id = id;
        if(cardString=="  ") {
            cardString="back";
        }
        this.cardString = cardString;
   },
   render : function() {
       var t = this.getTemplate();
       var output = Mustache.render(t,{domId : this.id, cardString:this.cardString});
       return output;
   },
    getDOMElement : function() {
      var el = $("#"+this.getCardDivId());
      if(el) {
            return el.get(0);
      } else {
          return null;
      }

    },
    getTemplate : function() {
        return this.templateManager.getTemplate("playerCardTemplate");
    },
    getCardDivId : function() {
        return "playerCard-"+this.id;
    }
});