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
        this.mountHandler("filter", this.handleFilter);
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
                    if(arg != "undefined") {
                        handler.apply(this, [arg]);
                    } else  {
                        handler.call(this);
                    }
                    document.location.hash="";
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
            return;
        }
        if (sectionName == "sitandgo") {
            setTimeout("$('#sitAndGoMenu').click()", 200);
        }
    },
    handleFilter : function(filter) {
        console.log("Clicking filter " + filter);
        if (filter == 'sitandgo-xoc') {
            // Note, just doing this because I had problems with scoping the filter variable within the setTimeout call, feel free to improve.
            setTimeout("$('#sit-and-go-xoc').click()", 300);
        }
    }

});