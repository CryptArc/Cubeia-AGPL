LocalAchievements = function() {
    this.achievementList = {};
};

LocalAchievements.prototype.createFakeAchievement = function(number) {
    var pointValues = [5, 10, 25]

    var achievement = {
        game:"GameId",
        achievementNameId: new Date().getTime()+"_"+number,
        name: "NameFor"+number,
        description:"The text description for this achievement",
        playerId: "PlayerName",
        imageUrl: null,
        timestamp: "yy/mm/dd",
        rank:Math.ceil(Math.random()*3),
        value:pointValues[Math.floor(Math.random()*3)],
        currentCount:Math.floor(Math.random()*100),
        targetCount: 100,
        reward:"Some Reward ",
        achieved:Math.round(Math.random())
    };

    return achievement;
};

LocalAchievements.prototype.createFakeAchievements = function(amount) {
    for (var i = 0; i < amount; i++) {
        var achi = this.createFakeAchievement(i);
        this.addAchievement(achi);
    }
};

LocalAchievements.prototype.addAchievement = function(achievement) {
    this.achievementList[achievement.achievementNameId] = achievement;
    console.log(this.achievementList)
};

LocalAchievements.prototype.addAchievementArray = function(achievementArray) {

    for (var i = 0; i < achievementArray.length; i++) {
         this.addAchievement(achievementArray[i])
    }

}

LocalAchievements.prototype.setAchievementList = function(achievementList) {
    this.achievementList = achievementList;
};

LocalAchievements.prototype.resetAchievementList = function() {
    this.setAchievementList({})
};

LocalAchievements.prototype.getAchievementList = function() {
    return this.achievementList;
};

LocalAchievements.prototype.getListedAchievement = function(achievementNameId) {
    return this.achievementList[achievementNameId];
};