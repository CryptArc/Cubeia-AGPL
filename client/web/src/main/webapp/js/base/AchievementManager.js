"use strict";
var Poker = Poker || {};

Poker.AchievementManager = Class.extend({

    init : function() {

    },
    handleAchievement : function(tableId, playerId, message) {
        console.log("player " + playerId + " received", message);
        if(playerId == Poker.MyPlayer.id) {
            if(message.type=="achievement") {
                var n = new Poker.TextNotifcation(message.achievement.name + ' ' + i18n.t("achievement.completed"),
                    message.achievement.description,message.achievement.imageUrl);
                Poker.AppCtx.getNotificationsManager().notify(n);
            } else if(message.type=="xp" && message.subType == "levelUp") {
                var level = message.attributes.level;
                var totalXp = message.attributes.totalXp;
                var profileManager = Poker.AppCtx.getProfileManager();
                if(profileManager.myPlayerProfile.level!=level){
                    var n = new Poker.LevelUpNotification(level);
                    Poker.AppCtx.getNotificationsManager().notify(n, {time : 60000, class_name : "gritter-dark level-up"});
                    profileManager.updateLevel(parseInt(level),parseInt(totalXp));
                } else {
                    console.log("Ignoring level up, same level");
                }
            } else if(message.type == "xp" && message.subType =="increase")  {
                var level = message.attributes.level;
                var totalXp = message.attributes.totalXp;
                Poker.AppCtx.getProfileManager().updateXp(parseInt(totalXp),parseInt(level));
            }
        }

    }
});