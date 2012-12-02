AchievementPresenter = function() {
    this.loadQueue = [];
    this.gainAnimations = 0;
};

AchievementPresenter.prototype.fileLoadComplete = function(path) {
    this.loadQueue.splice(this.loadQueue.indexOf(path), 1);
    if (this.loadQueue.length == 0) this.initScript();
}

AchievementPresenter.prototype.loadFile = function(baseUrl, path, filetype) {

    var file = baseUrl+"/"+path;
    this.loadQueue.push(file);
    if (filetype=="js"){ //if filename is a external JavaScript file
        var fileref=document.createElement('script')
        fileref.setAttribute("type","text/javascript")
        fileref.setAttribute("src", file)
    }
    else if (filetype=="css"){ //if filename is an external CSS file
        var fileref=document.createElement("link")
        fileref.setAttribute("rel", "stylesheet")
        fileref.setAttribute("type", "text/css")
        fileref.setAttribute("href", file)
    }

    instance = this;
    var isLoaded = function() {
        instance.fileLoadComplete(file)
    };

    fileref.onload = isLoaded
    if (typeof fileref!="undefined") {
        document.getElementsByTagName("head")[0].appendChild(fileref)
    }
    console.log(fileref)
};

AchievementPresenter.prototype.load = function(baseUrl) {
    this.loadFile(baseUrl, "js/achievements/DomHandler.js", "js")
    this.loadFile(baseUrl, "js/achievements/AchievementUiBuilder.js", "js")

    this.loadFile(baseUrl, "js/achievements/LocalAchievements.js", "js")
    this.loadFile(baseUrl, "js/achievements/AchievementLoader.js", "js")
    this.loadFile(baseUrl, "css/achievements.css", "css")
};

AchievementPresenter.prototype.initScript = function() {
    this.achievementLoader = new AchievementLoader();
    this.achievementUiBuilder = new AchievementUiBuilder(new DomHandler());
    this.localAchievements = new LocalAchievements();
};

AchievementPresenter.prototype.updateUserInParentElement = function(user, parentElement) {
    this.clearAchievementList(parentElement);
    this.achievementLoader.setUserId(user);
    var onLoad = function() {
        achievementPresenter.openAchievementList(parentElement);
    };

    this.loadAchievementList(onLoad);
};

AchievementPresenter.prototype.handleSocketEvent = function(message) {
    if (message.type == "achievement") {
        achievement = message.achievement;
        achievement.achieved = message.attributes.achieved;
        achievement.countTarget = message.attributes.countTarget;
        if (message.achievement.achieved) {
            console.log("------>>>> ", message)
            if (message.player == this.achievementLoader.getUserId()) {
                this.showAchievementGained(message.achievement, "base", document.body);
            } else {
                this.showAchievementGained(message.achievement, "tiny", document.body);
            }

        } else {
            this.updateAchievementProgress(message.achievement);
        }

    };

};

AchievementPresenter.prototype.loadAchievementList = function(onLoad) {
    this.achievementLoader.loadUserAchievements(onLoad)

};

AchievementPresenter.prototype.showAchievementGained = function(achievement, variant, parent) {
    var parent = parent;
    var divId = achievement.name+"_gained";
    var container = this.achievementUiBuilder.builUiComponent(parent, divId, "achievement_gained_container_"+variant);


    var divs = {};
    var idForUi = achievement.achievementNameId
    divs.frame = this.achievementUiBuilder.builUiComponent(container, idForUi, "achievement_container_frame");
    achievement.divs=divs;

    if (variant == "tiny") {
        divs.frame.className = "achievement_container_frame_tiny"
        console.log("--------->> build tiny achievement", achievement)
        this.buildTinyAchievement(achievement)
        this.animateGainAchievement(container, achievement);
    } else {

        this.buildBaseAchievement(achievement)
        if (achievement.currentCount) {
            this.showAchievementInProgress(achievement);
        }
        this.animateGainAchievement(container, achievement);
    }



 //   this.addAchievementToContainer(container, achi);
};

AchievementPresenter.prototype.loadFakeAchievementList = function() {
    this.localAchievements.createFakeAchievements(6);
};


AchievementPresenter.prototype.openAchievementList = function(parentElement) {
    var achievements = this.localAchievements.getAchievementList();
    var container = this.achievementUiBuilder.builUiComponent(parentElement, "achievement_list_container", "achievement_list_container");
    this.listAchievements(container, achievements);
};

AchievementPresenter.prototype.clearAchievementList = function(parentElement) {
    this.localAchievements.resetAchievementList();
    this.achievementUiBuilder.clearList(parentElement);
};

AchievementPresenter.prototype.listAchievements = function(container, achievements) {
    var obtained = {};
    var progressing = {};
    var notStarted = {};

    for (index in achievements) {
        if (achievements[index].achieved) {
            obtained[index] = achievements[index];
        } else if (achievements[index].currentCount > 0) {
            progressing[index] = achievements[index];
        } else {
            notStarted[index] = achievements[index];
        }
    };

    this.listObtainedAchievements(container, obtained)
    this.listProgressingAchievements(container, progressing)
    this.listNotStartedAchievements(container, notStarted)

};

AchievementPresenter.prototype.listObtainedAchievements = function(container, achievements) {
    for (index in achievements) {
        this.addAchievementToContainer(container, achievements[index])
    };
};

AchievementPresenter.prototype.listProgressingAchievements = function(container, achievements) {
    for (index in achievements) {
        this.addAchievementToContainer(container, achievements[index])
    };
};

AchievementPresenter.prototype.listNotStartedAchievements = function(container, achievements) {
    for (index in achievements) {
        this.addAchievementToContainer(container, achievements[index])
    };
};

AchievementPresenter.prototype.addAchievementToContainer = function(container, achievement) {
    var divs = {};
    var idForUi = achievement.achievementNameId
    divs.frame = this.achievementUiBuilder.builUiComponent(container, idForUi, "achievement_container_frame")
    achievement.divs=divs;
    this.buildBaseAchievement(achievement)

    if (achievement.currentCount) {
        this.showAchievementInProgress(achievement);

        if (achievement.achieved) {

            this.showAchievementAsCompleted(achievement);
        }
    }
};

AchievementPresenter.prototype.buildBaseAchievement = function(achievement) {

    var divs = achievement.divs;
    var idForUi = achievement.achievementNameId;

    divs.name = this.achievementUiBuilder.builUiComponent(divs.frame, idForUi+"_name", "achievement_name_label")
    divs.name.innerHTML = achievement.name;
    divs.image = this.achievementUiBuilder.builUiComponent(divs.frame, idForUi+"_image", "achievement_image_frame")
    divs.image.style.backgroundImage = "url("+achievement.imageUrl+")";
    divs.description = this.achievementUiBuilder.builUiComponent(divs.frame, idForUi+"_description", "achievement_description_text")
    divs.description.innerHTML = achievement.description;

    divs.rank = this.achievementUiBuilder.builUiComponent(divs.frame, idForUi+"_rank", "achievement_rank_frame")
    this.achievementUiBuilder.addClass(divs.rank, "achievement_rank_frame_"+achievement.rank)

    divs.value = this.achievementUiBuilder.builUiComponent(divs.rank, idForUi+"_value", "achievement_value_label")
    divs.value.innerHTML = achievement.value;

    divs.reward = this.achievementUiBuilder.builUiComponent(divs.frame, idForUi+"_reward", "achievement_reward_label")

    if (achievement.rewardDescription) {
        divs.reward.innerHTML = "Reward: "+achievement.rewardDescription;
    }
}

AchievementPresenter.prototype.showAchievementInProgress = function(achievement) {
    var divs = achievement.divs;
    var idForUi = achievement.achievementNameId;
    divs.progressFrame = this.achievementUiBuilder.builUiComponent(divs.frame, idForUi+"_progress_frame", "achievement_progress_frame")
    divs.progressBar = this.achievementUiBuilder.builUiComponent(divs.progressFrame, idForUi+"_progress_bar", "achievement_progress_bar")

    divs.progressLabel = this.achievementUiBuilder.builUiComponent(divs.progressFrame, idForUi+"_progress_label", "achievement_progress_label")
    this.showAchievementProgress(achievement);
};

AchievementPresenter.prototype.updateAchievementProgress = function(message) {
    var achievement = this.localAchievements.getListedAchievement(message.achievementNameId);
    if (!achievement) return
    achievement.currentCount = message.currentCount;
    this.showAchievementProgress(achievement)
}

AchievementPresenter.prototype.showAchievementProgress = function(achievement) {
    achievement.divs.progressBar.style.width = 100 * achievement.currentCount / achievement.targetCount+"%";
    achievement.divs.progressLabel.innerHTML = achievement.currentCount+" / "+achievement.targetCount;
}

AchievementPresenter.prototype.showAchievementAsCompleted = function(achievement) {
    var divs = achievement.divs;
    var idForUi = achievement.achievementNameId;
    if (achievement.achievedTimestamp) {
        divs.time = this.achievementUiBuilder.builUiComponent(divs.frame, idForUi+"_time", "achievement_timestamp_label")
        divs.time.innerHTML = achievement.achievedTimestamp;
    }


    this.achievementUiBuilder.addClass(divs.frame, "achievement_container_frame_achieved")
    this.achievementUiBuilder.addClass(divs.name, "achievement_name_label_achieved")
    if (divs.description) this.achievementUiBuilder.addClass(divs.description, "achievement_description_text_achieved")
    if (divs.progressBar) this.achievementUiBuilder.addClass(divs.progressBar, "achievement_progress_bar_achieved")
    if (divs.reward) this.achievementUiBuilder.addClass(divs.reward, "achievement_reward_label_achieved")
}

AchievementPresenter.prototype.animateGainAchievement = function(container, achievement) {

    var instance = this;
    achievement = achievement;
    setTimeout(function(){
        container.style.bottom = instance.gainAnimations*110+140+"px";
        container.style.opacity = 1;
        instance.gainAnimations += 1;
    }, 100)



    setTimeout(function(){
        instance.showAchievementAsCompleted(achievement);
    }, 1600)

 /*
    setTimeout(function(){
        container.style.bottom = "1px";
        container.style.opacity = 0;
        instance.gainAnimations -= 1;
    }, 6000)



    setTimeout(function(){
        instance.achievementUiBuilder.domHandler.removeElement(container);
        achievement.divs = {};

    }, 8000)
    */
}


AchievementPresenter.prototype.buildTinyAchievement = function(achievement) {
    var divs = achievement.divs;
    var idForUi = achievement.achievementNameId;

    divs.name = this.achievementUiBuilder.builUiComponent(divs.frame, idForUi+"_name", "achievement_name_label_tiny")
    divs.name.innerHTML = achievement.name;
    divs.image = this.achievementUiBuilder.builUiComponent(divs.frame, idForUi+"_image", "achievement_image_frame_tiny")
    divs.image.style.backgroundImage = "url("+achievement.imageUrl+")";
//    divs.description = this.achievementUiBuilder.builUiComponent(divs.frame, idForUi+"_description", "achievement_description_text")
//    divs.description.innerHTML = achievement.description;

    divs.rank = this.achievementUiBuilder.builUiComponent(divs.frame, idForUi+"_rank", "achievement_rank_frame_tiny")
    this.achievementUiBuilder.addClass(divs.rank, "achievement_rank_frame_"+achievement.rank)

    divs.value = this.achievementUiBuilder.builUiComponent(divs.rank, idForUi+"_value", "achievement_value_label_tiny")
    divs.value.innerHTML = achievement.value;

}