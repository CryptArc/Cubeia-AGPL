"use strict";
var Poker = Poker || {};

Poker.AchievementManager = Class.extend({

    init : function() {

    },
    handleAchievement : function(tableId, playerId, message) {
        console.log("player " + playerId + " received", message);
        if(message.type=="achievement" && playerId == Poker.MyPlayer.id) {
            var n = new Poker.TextNotifcation(message.achievement.name + ' ' + i18n.t("achievement.completed"),
                message.achievement.description,message.achievement.imageUrl);
            Poker.AppCtx.getNotificationsManager().notify(n);
        } else if(message.type=="xp" && playerId == Poker.MyPlayer.id && message.subType == "levelUp") {
            var level = message.attributes.level;
            var profileManager = Poker.AppCtx.getProfileManager();
            if(profileManager.myPlayerProfile.level!=level){
                var n = new Poker.LevelUpNotification(level);
                Poker.AppCtx.getNotificationsManager().notify(n, {time : 60000, class_name : "gritter-dark level-up"});
                profileManager.updateLevel(level);
            } else {
                console.log("Ignoring level up, same level");
            }

        }
    }
});