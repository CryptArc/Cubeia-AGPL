AchievementLoader = function() {
    this.setBaseUrl("localhost:8080/rest/player");
    this.setUserId("UserId");
    this.setGame("poker");
    this.updateUrl();
};

AchievementLoader.prototype.setBaseUrl = function(baseUrl) {
    this.baseUrl = baseUrl;
};

AchievementLoader.prototype.setGame = function(game) {
    this.game = game;
};

AchievementLoader.prototype.setUserId = function(userId) {
    this.userId = userId;
};

AchievementLoader.prototype.updateUrl = function() {
    var url = this.baseUrl+"/"+this.userId+"/achievements/"+this.game;
    this.setUrl(url);
    return url;
};

AchievementLoader.prototype.setUrl = function(url) {
    this.url = url;
};



AchievementLoader.prototype.loadUserAchievements = function(onLoad) {
    var url = this.updateUrl();
    this.loadAchievementData(url, onLoad);
};


AchievementLoader.prototype.loadAchievementData = function(url, onLoad) {
    onLoad = onLoad;
        var url = "http://"+url;
        var instance = this;
        var xobj = new XMLHttpRequest();
        xobj.open('GET', url, true);
        xobj.onreadystatechange = function () {
            if (xobj.readyState == 4) {
                var data = xobj.responseText;
                instance.setAchievementData(data, onLoad);
            }
        }
        xobj.send(null);

}

AchievementLoader.prototype.setAchievementData = function(data, onLoad) {
    var achievements = JSON.parse(data)
    achievementPresenter.localAchievements.addAchievementArray(achievements);
    onLoad();
}
