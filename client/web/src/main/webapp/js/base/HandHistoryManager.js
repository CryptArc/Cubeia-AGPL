"user strict";
var Poker = Poker || {};

Poker.HandHistoryManager = Class.extend({
    popups : null,
    handIdsTemplate : null,
    handLogTemplate : null,
    init : function() {
        this.popups = new Poker.Map();
        this.handIdToTableMap = new Poker.Map();
        var templateManager = Poker.AppCtx.getTemplateManager();
        this.handIdsTemplate = templateManager.getRenderTemplate("handHistoryIdsTemplate");
        this.handLogTemplate = templateManager.getRenderTemplate("handHistoryLogTemplate");
    },
    requestHandHistory : function(tableId) {
        var popup = this.popups.get(tableId);
        if(popup!=null && !popup.window.closed) {
            popup.window.location.reload();
            popup.window.focus();
        } else {
            popup = window.open(contextPath + "/poker/skin/" + Poker.SkinConfiguration.name + "/hand-history/" + tableId,"Hand history for " + tableId,
                "width=500,height=600,directories=no,menubar=no",false);
            this.popups.put(tableId,{window : popup, container : null });
        }
    },
    showHandSummaries : function(tableId,summaries) {
        var self = this;
        var popup = this.popups.get(tableId);
        summaries.reverse();
        console.log(popup.container);
        $.each(summaries,function(i,e){
            e.startTime = self.formatDateTime(e.startTime);
        });
        var html = this.handIdsTemplate.render({summaries : summaries});
        var handIdsContainer = popup.container.find(".hand-ids");
        handIdsContainer.html(html);
        var self = this;
        if(summaries.length>0) {
            $.each(summaries,function(i,e){
                popup.container.find("#hand-"+ e.id).click(function(el){
                    new Poker.HandHistoryRequestHandler(tableId).requestHand(e.id);
                });
            });
            handIdsContainer.scrollTop(handIdsContainer[0].scrollHeight);
            new Poker.HandHistoryRequestHandler(tableId).requestHand(summaries[summaries.length-1].id);
            popup.container.find(".no-hands").hide();
        } else {
            popup.container.find(".no-hands").show();
        }

    },
    showHand : function(hand) {
        hand = this.prepareHand(hand);
        console.log("hand = ");
        console.log(hand);
        var container = this.popups.get(hand.table.tableId).container;
        var log = container.find(".hand-log");
        container.find(".hand-ids .active").removeClass("active");
        container.find("#hand-"+hand.id).addClass("active");
        log.empty();
        log.append(this.handLogTemplate.render(hand));
        log.scrollTop(0);

    },
    formatDateTime : function(milis) {
        var date = new Date(milis);
        return  date.toLocaleDateString() + " " + date.toLocaleTimeString();
    },

    prepareHand : function(hand) {
        console.log(hand);
        var playerMap = new Poker.Map();
        for(var i = 0; i<hand.seats.length; i++)  {
            var seat = hand.seats[i];
            playerMap.put(seat.playerId,seat.name);
            $.extend(seat,{initialBalance : this.formatAmount(seat.initialBalance)});
        }
        for(var i = 0; i<hand.events.length; i++) {
            var event = hand.events[i]
            hand.startTime = this.formatDateTime(hand.startTime);
            if(typeof(event.playerId)!="undefined") {
                event = $.extend(event,{name : playerMap.get(event.playerId)});
            }
            if(typeof(event.action)!="undefined") {
                event = $.extend(event,{
                    action : this.getAction(hand.events[i].action)

                });
            }
            if(typeof(event.cards)!=undefined) {
                event = $.extend(event,{
                    cards : this.extractCards(event.cards)
                });

            }
            if(typeof(event.amount)!="undefined"){
                event= $.extend(event,{
                    amount :  {
                        amount : this.formatAmount(hand.events[i].amount.amount)
                    }
                });
            }
            if(event.type == "TableCardsDealt") {
                event = $.extend(event,{
                    cards : this.extractCards(event.cards),
                    tableCards : true
                });
            }
            if(event.type == "PlayerCardsExposed") {
                event = $.extend(event,{
                    playerCardsExposed : true
                });
            }
            if(event.type == "PlayerCardsDealt") {
                event = $.extend(event,{
                    playerCardsDealt : true
                });
            }
            if(event.type == "PlayerBestHand") {
                event = $.extend(event,{
                    bestHandCards : this.extractCards(event.bestHandCards),
                    name : playerMap.get(event.playerHand.playerId),
                    handDescription : Poker.Hand.fromName(event.handInfoCommon.handType).text
                });
            }

        }
        var results = [];
        for(var x in hand.results.results) {
            results.push(hand.results.results[x]);
        }
        $.extend(hand.results, { res : results});

        for(var i = 0; i<hand.results.res.length; i++) {
            var result = hand.results.res[i];
            result = $.extend(result,{
                name : playerMap.get(result.playerId),
                totalBet : this.formatAmount(result.totalBet),
                totalWin : this.formatAmount(result.totalWin,"0")
            });
        }

        return hand;

    },
    formatAmount : function(amount,emptyStr) {
        if(amount==0) {
            return emptyStr || "";
        } else {
            return Poker.Utils.formatCurrency(amount);
        }
    },
    ready : function(tableId,container) {
        this.popups.get(tableId).container = container;
        console.log("Hand history popup for table " + tableId + " is ready");
        new Poker.HandHistoryRequestHandler(tableId).requestHandSummaries(10);

    },
    getAction : function(actionEnumString) {
        var act = Poker.ActionType[actionEnumString];
        if(typeof(act)!="undefined"){
            return act.text;
        }
        return actionEnumString;
    },
    extractCards : function(cards) {
        if(typeof(cards)=="undefined") {
            return null;
        }
        for(var i = 0; i<cards.length; i++) {
            cards[i] = $.extend(cards[i],{text : this.getCard(cards[i])});
        }
        return cards;
    },
    getCard : function(card) {
        return this.getRank(card.rank) + this.getSuit(card.suit);
    },
    getSuit : function(suit){
        switch(suit) {
            case "CLUBS":
                return "c";
            case "DIAMONDS":
                return "d";
            case "HEARTS" :
                return "h";
            case "SPADES":
                return "s";
        }
    },
    getRank : function(rank) {
        switch (rank) {
            case "TWO":
                return "2";
            case "THREE":
                return "3";
            case "FOUR":
                return "4";
            case "FIVE":
                return "5";
            case "SIX":
                return "6";
            case "SEVEN":
                return "7";
            case "EIGHT":
                return "8";
            case "NINE":
                return "9";
            case "TEN":
                return "T";
            case "JACK":
                return "J";
            case "QUEEN":
                return "Q";
            case "KING":
                return "K";
            case "ACE":
                return "A";

        }
    }


});
