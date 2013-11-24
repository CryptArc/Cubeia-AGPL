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
//        alert(segments);
        if(segments.length==0) {
            return;
        }

        // Go through the segments pair wise.
        for (var i = 0; i < segments.length/2; i++) {
            var viewName = segments[i * 2];
            var arg = segments[i * 2 + 1];
            if(viewName!=null) {
                var handler = this.views.get(viewName);
                if(handler!=null) {
                    var removeHash = false;
                    if(arg != "undefined") {
                        removeHash = handler.apply(this, [arg]);
                    } else  {
                        removeHash = handler.call(this);
                    }
                    if(removeHash==true) {
                        document.location.hash="";
                    }
                }
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
        if(typeof(sectionName)=="undefined") {
            return true;
        }
        if (sectionName == "sitandgo") {
            setTimeout("$('#sitAndGoMenu').click()", 200);
        }
        return false;
    }

});