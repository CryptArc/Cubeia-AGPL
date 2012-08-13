UserInput = function() {
    this.entityId = "userInputEntityId";
};

UserInput.prototype.setCheckAvailable = function(playerAction) {
    this.showCheck(playerAction)
};

UserInput.prototype.setCallAvailable = function(playerAction) {
    this.showCall(playerAction)
};

UserInput.prototype.setBetAvailable = function(playerAction) {
    this.showPlaceBet(playerAction)
};

UserInput.prototype.setRaiseAvailable = function(playerAction) {

    this.showRaiseBet(playerAction)
};

UserInput.prototype.setFoldAvailable = function() {
    this.showFold()
};


UserInput.prototype.setupUserInput = function() {
    var entity = entityHandler.addEntity(this.entityId);
    entityHandler.addUiComponent(entity, "", "user_input_frame", null);

    this.initPlayerGameActionUi(entity)
};

UserInput.prototype.initPlayerGameActionUi = function(parentEntity) {
    var entity = entityHandler.addEntity(this.entityId+"_player_actions")
    var userInputEntity = entityHandler.getEntityById(this.entityId);

    entityHandler.addUiComponent(entity, "", "player_actions_frame", null, userInputEntity.ui.divId);

    var userProgressFrameId = "user_input_progressbar_frame";
    var userInputProgressbar = userProgressFrameId+"_bar";
    uiElementHandler.createDivElement(parentEntity.ui.divId, userProgressFrameId, "", "seat_timer_frame", null);
    document.getElementById(userProgressFrameId).style.left = 27+"%";
    document.getElementById(userProgressFrameId).style.right = 27+"%";
    document.getElementById(userProgressFrameId).style.top = 2+"px";
    document.getElementById(userProgressFrameId).style.height = 4+"px";
    document.getElementById(userProgressFrameId).style.width = "auto";
    uiElementHandler.createDivElement(userProgressFrameId, userInputProgressbar, "", "horizontal_progressbar", null);
    parentEntity.ui.userProgressBarDivId = userInputProgressbar;

    var placeBet = function() {
        userInput.clickPlaceBetButton();
    }

    var raise = function() {
        userInput.clickRaiseButton();
    }

    var call = function() {
        userInput.clickCallButton();
    }

    var check = function() {
        userInput.clickCheckButton();
    }

    var fold = function() {
        userInput.clickFoldButton();
    }

    this.inpuButtons = {
        betButton: {label: "Bet", posX: 5, posY: 27, height: 65, width: 80, hasValue:true, clickFunction:placeBet},
        raiseButton: {label: "Raise", posX: 23, posY: 11, height: 65, width: 80, hasValue:true, clickFunction:raise},
        callButton: {label: "Call", posX: 42, posY: 8, height: 65, width: 80, hasValue:true, clickFunction:call},
        checkButton: {label: "Check", posX: 61, posY: 11, height: 65, width: 80, hasValue:false, clickFunction:check},
        foldButton: {label: "Fold", posX: 79, posY: 27, height: 65, width: 80, hasValue:false, clickFunction:fold}
    };

    uiUtils.createActionButton(this.inpuButtons.betButton, entity.ui.divId);
    uiUtils.createActionButton(this.inpuButtons.raiseButton, entity.ui.divId);
    uiUtils.createActionButton(this.inpuButtons.callButton, entity.ui.divId);
    uiUtils.createActionButton(this.inpuButtons.checkButton, entity.ui.divId);
    uiUtils.createActionButton(this.inpuButtons.foldButton, entity.ui.divId);

    this.hideActionButtons()
}




UserInput.prototype.clearUserEntityTimeToAct = function() {
    var userEntity = entityHandler.getEntityById(playerHandler.getPlayerEntityIdByPid(playerHandler.myPlayerPid));
    console.log(userEntity)
    userEntity.state.timeToAct = 1;
    var seatEntity = entityHandler.getEntityById(view.seatHandler.getSeatEntityIdBySeatNumber(userEntity.state.seatId));
    view.seatHandler.setSeatEntityToPassive(seatEntity);

}

UserInput.prototype.endUserTurn = function() {
    this.hideActionButtons()
    this.clearUserEntityTimeToAct()

}

UserInput.prototype.hideActionButtons = function() {
    for (index in this.inpuButtons) {
         document.getElementById(this.inpuButtons[index].buttonDivId).style.visibility = "hidden";
    }
};

UserInput.prototype.showPlaceBet = function(playerAction) {
    this.setBetValue(playerAction.minAmount);
    document.getElementById(this.inpuButtons.betButton.buttonDivId).style.visibility = "visible";
};

UserInput.prototype.showRaiseBet = function(playerAction) {
    this.setRaiseValue(playerAction.minAmount);
    document.getElementById(this.inpuButtons.raiseButton.buttonDivId).style.visibility = "visible";
};

UserInput.prototype.showCall = function(playerAction) {
    this.setCallValue(playerAction.minAmount);
    document.getElementById(this.inpuButtons.callButton.buttonDivId).style.visibility = "visible";
};

UserInput.prototype.showCheck = function(playerAction) {
    document.getElementById(this.inpuButtons.checkButton.buttonDivId).style.visibility = "visible";
};

UserInput.prototype.showFold = function() {
    document.getElementById(this.inpuButtons.foldButton.buttonDivId).style.visibility = "visible";
};

UserInput.prototype.setBetValueMinMax = function(min, valueMax) {

}

UserInput.prototype.setCallValue = function(value) {
    document.getElementById(this.inpuButtons.callButton.valueDivId).innerHTML = currencyFormatted(value);
};

UserInput.prototype.setRaiseValue = function(value) {
    document.getElementById(this.inpuButtons.raiseButton.valueDivId).innerHTML = currencyFormatted(value);
};

UserInput.prototype.setBetValue = function(value) {
    document.getElementById(this.inpuButtons.betButton.valueDivId).innerHTML = currencyFormatted(value);
};




UserInput.prototype.clickPlaceBetButton = function() {
	playerActions.bet(view.table.validActions[POKER_PROTOCOL.ActionTypeEnum.BET].minAmount);
    this.endUserTurn();
    console.log("User Action Button:   Bet");
};

UserInput.prototype.clickRaiseButton = function() {
    playerActions.raise(view.table.validActions[POKER_PROTOCOL.ActionTypeEnum.RAISE].minAmount);
    this.endUserTurn();
    console.log("User Action Button:   Raise ");
};

UserInput.prototype.clickCallButton = function() {
    playerActions.call();
    this.endUserTurn();
    console.log("User Action Button:   Call ");
};

UserInput.prototype.clickCheckButton = function() {
    playerActions.check();
    this.endUserTurn();
    console.log("User Action Button:   Check ");
};

UserInput.prototype.clickFoldButton = function() {
    playerActions.fold();
    this.endUserTurn();
    console.log("User Action Button:   Fold ");
};



UserInput.prototype.setUserActionProgressBar = function(currentTime) {
    var userEntity = entityHandler.getEntityById(playerHandler.getPlayerEntityIdByPid(playerHandler.myPlayerPid));
    if (!userEntity || view.seatHandler.activeSeatEntity == null) {
        console.log("no user entity yet!");
        return;
    }
    if (userEntity.id == view.seatHandler.activeSeatEntity.occupant.id) {
        console.log("User Has time to act");
        var percentRemaining = playerHandler.getPlayerEntityActionTimePercentRemaining(userEntity, currentTime);
    } else {
        console.log("User is out of time");
        percentRemaining = 0;
    }

    var uiEntity = entityHandler.getEntityById(this.entityId);
    document.getElementById(uiEntity.ui.userProgressBarDivId).style.width = percentRemaining+"%";

};

UserInput.prototype.tick = function(currentTime) {
    this.setUserActionProgressBar(currentTime)


};
