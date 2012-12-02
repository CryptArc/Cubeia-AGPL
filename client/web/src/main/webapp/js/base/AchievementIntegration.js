AchievementIntegration = function() {

};

AchievementIntegration.prototype.handleAchievementMessage = function(message) {
    console.log(">>>>>> ---- BONUS MESSAGE ----- >>>>> ",message)
        if (message.type == "achievement") {

            achievement = message.achievement;
            achievement.achieved = message.attributes.achieved;
            achievement.countTarget = message.attributes.countTarget;
            if (message.achievement.achieved) {
                console.log("------>>>> Achievement: ", message)
                var me = Poker.MyPlayer.id;
                console.log("------>>>> player: ", message.player, me)
                if (message.player == me) {
                    achievementPresenter.showAchievementGained(message.achievement, "base", document.body);
                } else {
                    var view = Poker.AppCtx.getViewManager().getActiveView()
                    console.log(view);
                    if (view instanceof Poker.TableView) {
                        var seat = view.layoutManager.getSeatByPlayerId(message.player);
                        if (!seat) {
                            console.log("------ SEAT NOT FOUND -- message: >>>", message)
                            return;
                        }
                        console.log("------SEAT-->>>", seat)
                        achievementPresenter.showAchievementGained(message.achievement, "tiny", seat.seatElement[0]);
                    }

                }

            }

        };



};

