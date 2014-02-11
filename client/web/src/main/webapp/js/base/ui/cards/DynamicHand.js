"use strict";
var Poker = Poker || {};

/**
 * Handles a poker hand in the UI
 * @type {Poker.DynamicHand}
 */
Poker.DynamicHand = Class.extend({

    cards:null,
    cardOder : null,
    discards:null,
    minDiscards:1,
    maxDiscards:1,

    handContainer : null,
    ratio:1.4,
    offset : 0.45,
    width : 0,
    height : 0,
    cardWidth: 90,
    maxCards : 5,
    align : 0,
    init : function(handContainer) {
        this.handContainer = handContainer;
        this.calculateCardDimensions();
        this.setup();
        var self = this;
        $(window).on('resizeEnd',function(){
            self.updateCardPositions();
        });
    },
    setAlignment : function(align) {
        this.align = align;
        this.updateCardPositions();
    },
    setup : function() {
        this.cards = new Poker.Map();
        this.cardOrder = [];
        this.discards = new Poker.Map();
        this.calculateCardProperties();
    },
    calculateCardProperties : function() {
        this.width = this.handContainer.width();
        this.height = this.handContainer.height();
    },
    calculateCardDimensions : function() {
        var w = this.handContainer.width();
        this.cardWidth = Math.floor(w/(1+(this.maxCards-1)*this.offset));
        this.handContainer.find("img").width(this.cardWidth).height(Math.floor(this.cardWidth*this.ratio))
    },
    getPositionForCard : function(num,total) {

       if(this.align == 0) {
           return this.getCenteredPosition(num,total);
       } else if(this.align < 0) {
           return this.getLeftPosition(num,total);
       } else  {
           return this.getRightPosition(num,total);
       }
    },
    exposeCards : function() {
        var cssUtils = new Poker.CSSUtils();
        cssUtils.setScale3d(this.handContainer,1,1,1, this.getTransformOrigin());
    },
    getTransformOrigin : function() {
        if(this.align == 0) {
            return "50% 0";
        } else if(this.align < 0) {
            return "0 0";
        } else {
            return "100% 0";
        }
    },
    getLeftPosition : function(num,total){
        var pxOffset = Math.floor(this.cardWidth*this.offset);
        return { x : (num-1)*pxOffset, y : 0};
    },
    getRightPosition : function(num,total){
        var pxOffset = Math.floor(this.cardWidth*this.offset);
        console.log("TOTALE: = " + (total-num));
        return { x : this.width-this.cardWidth-(total-num)*pxOffset, y : 0};
    },
    getCenteredPosition : function(num,total) {
        var pxOffset = Math.floor(this.cardWidth*this.offset);
        var allCardWidth = this.cardWidth + (total-1)*pxOffset;
        var firstCardLeft = (this.width - allCardWidth)/2;
        return { x : firstCardLeft + (num-1)*pxOffset, y : 0};
    },

    enableDiscards : function(minDiscards,maxDiscards) {
        this.minDiscards = minDiscards;
        this.maxDiscards = maxDiscards;
    },

    disableDiscards : function() {

    },

    /**
     * @param {Poker.Card} card
     */
    addCard : function(card) {
        this.cards.put(card.id, card);
        this.cardOrder.push(card.id);
        this.handContainer.append(card.render(this.cards.size()));
        var self = this;
        card.getContainerElement().click(function() {
            self.toggleDiscardedCard(card.id);
        });
        this.calculateCardDimensions();
        this.updateCardPositions();
    },

    updateCardPositions : function() {
        this.calculateCardProperties();
        if(this.cards.size()>0) {
            this.calculateCardDimensions();
        }
        for(var i = 0; i<this.cardOrder.length; i++) {
            var card = this.cards.get(this.cardOrder[i]);
            var cssUtils = new Poker.CSSUtils();
            var pos  = this.getPositionForCard(i+1,this.cardOrder.length);
            cssUtils.setTranslate3d(card.getContainerElement(),pos.x,pos.y,0,"px","0 0");
        }
    },
    discardCards : function(cardsToDiscard) {
        for(var c in cardsToDiscard) {
            var card = this.cards.remove(cardsToDiscard[c]);
            card.getContainerElement().off().remove();
        }
    },
    removeAllCards : function() {
        this.cards = new Poker.Map();
        this.discards = new Poker.Map();
    },
    clear : function() {
        this.setup();
        this.handContainer.empty();
    },

    toggleDiscardedCard : function(cardId) {
        var card = this.cards.get(cardId);
        if (this.discards.contains(cardId)) {
            this.discards.remove(cardId);
            card.getContainerElement().removeClass("discarded");
        } else {
            if ( this.discards.size() < this.maxDiscards ) {
                this.discards.put(cardId, cardId);
                card.getContainerElement().addClass("discarded");
            } else if(this.discards.size()>0) {
                this.toggleDiscardedCard(this.discards.values()[0]);
                this.discards.put(cardId, cardId);
                card.getContainerElement().addClass("discarded");
            }
        }
    },
    getDiscards : function() {
        var discards = this.discards.values();
        console.log("DISCARDS: %O", discards);
        return discards;
    }
});