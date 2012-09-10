TextFeedback = function() {
    this.entityId = "text_log_id"
    this.textRows = ["", "", "", "", "", "", "", ""];
    this.textEvents = 0;
};

TextFeedback.prototype.initTextFeedback = function() {

    var textLogEntity = entityHandler.addEntity(this.entityId)
    textLogEntity.watchingEntities = [];
    entityHandler.addUiComponent(textLogEntity, "", "text_log", null)

    var posX = 1;
    var posY = 68;

    entityHandler.addSpatial(view.containerId, textLogEntity, posX, posY);
    uiElementHandler.setDivElementParent(textLogEntity.ui.divId, textLogEntity.spatial.transform.anchorId)


    view.spatialManager.positionVisualEntityAtSpatial(textLogEntity)

    textLogEntity.ui.textFieldDivId = textLogEntity.ui.divId+"_box"
    uiElementHandler.createDivElement(textLogEntity.ui.divId, textLogEntity.ui.textFieldDivId, this.textRows, "text_log_rows", null);

    document.getElementById(textLogEntity.ui.textFieldDivId).style.height = "60";
    document.getElementById(textLogEntity.ui.textFieldDivId).style.width = "220px";

    this.addLogText("Text log...")
};

TextFeedback.prototype.addLogText = function(text) {
    var entity = entityHandler.getEntityById(this.entityId);
    var textBoxDivId = entity.ui.textFieldDivId

    this.textRows.push(text);
    var textstring = "";
    for (var i = 0; i < this.textRows.length; i++) {
        textstring = ""+textstring+""+this.textRows[i]+"</br>";
        if (i >= 8) {
            this.textRows.shift();
        }
    }
    document.getElementById(textBoxDivId).innerHTML = textstring;
};

TextFeedback.prototype.showSeatSpaceTextFeedback = function(pid, action, value, actionType) {
    console.log(action)

    var playerEntityId = playerHandler.getPlayerEntityIdByPid(pid);
    var playerEntity = entityHandler.getEntityById(playerEntityId);

    var seatEntity = entityHandler.getEntityById(view.seatHandler.getSeatEntityIdBySeatNumber(playerEntity.state.seatId));
    if (!seatEntity) return;

    var betFieldDivId = seatEntity.ui.betFieldDivId;
    var valueString = "";

    if (value) {
        if(actionType== ACTIONS.RAISE) {
            $(".user-action").addClass("action-inactive");
        }
        valueString = "&euro;<span style='color:#FFF;'>" + value + '</span><div class="user-action '+actionType+'"></div>'
    }
    var betText =  $("#"+seatEntity.ui.betTextDivId);
    betText.html(action);

    if(action && action!="") {
       betText.show();
    }  else {
        betText.hide();
    }

    document.getElementById(betFieldDivId).innerHTML = valueString;
//    this.addSeatEventText(pid, action)
};


TextFeedback.prototype.addSeatEventText = function(pid, textString) {

    var playerEntityId = playerHandler.getPlayerEntityIdByPid(pid);
    var playerEntity = entityHandler.getEntityById(playerEntityId);
    var seatEntity = entityHandler.getEntityById(view.seatHandler.getSeatEntityIdBySeatNumber(playerEntity.state.seatId));
    if (!seatEntity) return;
    var tableEntity = entityHandler.getEntityById(view.table.entityId);


    var key = "seat_event_key"+this.textEvents+"";


    uiElementHandler.createDivElement(tableEntity.ui.divId, key, "", "text_log", null);
    uiElementHandler.createDivElement(key, key+"_text", textString, "event_text", null);

    var fontSize = 38;


    document.getElementById(key).style.webkitTransitionProperty = "top, opacity, left";
    document.getElementById(key).style.webkitTransitionTimingFunction = "ease-in";
    document.getElementById(key).style.webkitTransitionDuration = "5s";
    document.getElementById(key+"_text").style.color = "#ff9730";
    document.getElementById(key+"_text").style.fontSize = fontSize;
    document.getElementById(key).style.opacity = 1;
    document.getElementById(key).style.width = 30+"%";
    document.getElementById(key).style.top = seatEntity.spatial.transform.pos.y - 2 +"%";
    document.getElementById(key).style.left = seatEntity.spatial.transform.pos.x - 15 +"%";

    this.textEvents += 1;

    var t = setTimeout(function() {
        view.textFeedback.triggerTextAnimation(key);
    }, 100);


    /*

    if (this.textEvents >= 4) {
        this.textEvents = 1;
    }

    this.randomDir = Math.random()* 5 + this.textEvents;
    key = "seat_event_key"+this.textEvents+"";

    document.getElementById(key).style.webkitTransitionDuration = "0s";
    document.getElementById(key).style.color = "#697";
    document.getElementById(key).innerHTML = "";
    document.getElementById(key).style.opacity = 1;
    document.getElementById(key).style.top = 0;
    document.getElementById(key).style.left = -88 + this.randomDir * 9;

    */
};

TextFeedback.prototype.triggerTextAnimation = function(key) {


    document.getElementById(key).style.opacity = 0.1;
    document.getElementById(key).style.left = document.getElementById(key).style.left + (Math.random()*4) - 2 +"%";


    var t = setTimeout(function() {
        uiElementHandler.removeElements(0.95, key)
    }, 800);

}

TextFeedback.prototype.clearAllSeatSpaceTextFeedback = function() {
    var tableEntity = entityHandler.getEntityById(view.table.entityId);
    for (index in tableEntity.seats) {
        var seatEntity = entityHandler.getEntityById(view.seatHandler.getSeatEntityIdBySeatNumber(index));
        var betFieldDivId = seatEntity.ui.betFieldDivId;
        $("#"+seatEntity.ui.betFieldDivId).html("");
        var text = $("#"+seatEntity.ui.betTextDivId);
        text.hide();
        text.html("");
    }

}

TextFeedback.prototype.tick = function(currentTime) {

};
