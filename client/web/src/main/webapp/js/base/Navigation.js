"use strict";

var Poker = Poker || {};

Poker.Navigation = Class.extend({
    /**
     * @type {Poker.Map}
     */
    views : null,
    init : function() {
        this.views = new Poker.Map();
        this.mountHandler("tournament", this.handleTournament);
        this.mountHandler("table", this.handleTable);
        this.mountHandler("section", this.handleSection);
    },
    mountHandler : function(id,handler) {
        this.views.put(id,handler);
    },
    onLoginSuccess : function() {
        this.navigate();
    },
    navigate : function() {
        var segments = purl().fsegment();
        alert(segments);
        if(segments.length==0) {
            return;
        }
        var viewName = segments[0];
        var args = segments.slice(1);
        if(viewName!=null) {
            var handler = this.views.get(viewName);
            if(handler!=null) {
                if(args.length>0) {
                    handler.apply(this,args);
                } else  {
                    handler.call(this);
                }
                document.location.hash="";
            }
        }
    },
    handleTournament : function(name) {
        if(typeof(name)=="undefined") {
            return;
        }
        var tournamentManager = Poker.AppCtx.getTournamentManager();
        tournamentManager.openTournamentLobbyByName(name);
    },
    handleTable : function(tableId) {

        if(typeof(tableId)=="undefined") {
            return;
        }
        tableId = parseInt(tableId);
        //TODO: we need snapshot to get capacity
        new Poker.TableRequestHandler(tableId).openTable(10);
    },
    handleSection : function(sectionName) {
        alert("Section: " + sectionName);
        if(typeof(sectionName)=="undefined") {
            return;
        }
        if (sectionName == "sitandgo") {
            $("#sitAndGoMenu").click();
        }
    }

});