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
           this.showHandIds(tableId,popup.container);
        } else {
            popup = window.open(contextPath + "/poker/skin/" + Poker.SkinConfiguration.name + "/hand-history/" + tableId,"Hand history for " + tableId,
                "width=500,height=600,directories=no,menubar=no",false);
            this.popups.put(tableId,{window : popup, container : null });
        }
    },
    showHandIds : function(tableId,handIds) {
        var popup = this.popups.get(tableId);
        console.log(popup.container);
        var html = this.handIdsTemplate.render({handIds : handIds});
        popup.container.find(".hand-ids").html(html);
        var self = this;
        $.each(handIds,function(i,e){
            popup.container.find("#hand-"+ e.id).click(function(el){
                new Poker.HandHistoryRequestHandler(tableId).requestHand(e.id);
            });
        });
        new Poker.HandHistoryRequestHandler(tableId).requestHand(handIds[handIds.length-1].id);
    },
    showHand : function(hand) {
        console.log("hand = ");
        console.log(hand);
        var container = this.popups.get(hand.table.tableId).container;
        var log = container.find(".hand-log");
        container.find(".hand-ids .active").removeClass("active");
        container.find("#hand-"+hand.id).addClass("active");
        log.empty();
        log.append(this.handLogTemplate.render(hand));

    },
    ready : function(tableId,container) {
        this.popups.get(tableId).container = container;
        console.log("Hand history popup for table " + tableId + " is ready");
        new Poker.HandHistoryRequestHandler(tableId).requestHandIds(10);

    }
});