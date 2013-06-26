"use strict";
var Poker = Poker || {};

Poker.AchievementManager = Class.extend({

    init : function() {

    },
    handleAchievement : function(tableId, playerId, message) {
        //Poker.AppCtx.getNotificationsManager().notify(new Poker.Notification("test title", message));
        console.log("player " + playerId + " received", message);
    }
});