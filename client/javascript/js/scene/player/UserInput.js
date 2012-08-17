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

//    var userProgressFrameId = "user_input_progressbar_frame";
//    var userInputProgressbar = userProgressFrameId+"_bar";
//    uiElementHandler.createDivElement(parentEntity.ui.divId, userProgressFrameId, "", "seat_timer_frame", null);
//    document.getElementById(userProgressFrameId).style.left = 27+"%";
//    document.getElementById(userProgressFrameId).style.right = 27+"%";
//    document.getElementById(userProgressFrameId).style.top = 2+"px";
//    document.getElementById(userProgressFrameId).style.height = 4+"px";
//    document.getElementById(userProgressFrameId).style.width = "auto";
//    uiElementHandler.createDivElement(userProgressFrameId, userInputProgressbar, "", "horizontal_progressbar", null);
//    parentEntity.ui.userProgressBarDivId = userInputProgressbar;

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

    var buttonSide = 100;
    this.inputButtons = {
        foldButton: {label: "Fold", posX: 0, posY: 0, height: buttonSide, width: buttonSide, hasValue:false, clickFunction:fold},
        checkButton: {label: "Check", posX: 20, posY: 0, height: buttonSide, width: buttonSide, hasValue:false, clickFunction:check},
        callButton: {label: "Call", posX: 20, posY: 0, height: buttonSide, width: buttonSide, hasValue:false, clickFunction:call},
        betButton: {label: "Bet", posX: 40, posY: 0, height: buttonSide, width: buttonSide, hasValue:false, clickFunction:placeBet},
        raiseButton: {label: "Raise", posX: 40, posY: 0, height: buttonSide, width: buttonSide, hasValue:false, clickFunction:raise}
    };

    uiUtils.createActionButton(this.inputButtons.betButton, entity.ui.divId);
    uiUtils.createActionButton(this.inputButtons.raiseButton, entity.ui.divId);
    uiUtils.createActionButton(this.inputButtons.callButton, entity.ui.divId);
    uiUtils.createActionButton(this.inputButtons.checkButton, entity.ui.divId);
    uiUtils.createActionButton(this.inputButtons.foldButton, entity.ui.divId);

    this.hideActionButtons();
}

UserInput.prototype.clearUserEntityTimeToAct = function() {
    var userEntity = entityHandler.getEntityById(playerHandler.getPlayerEntityIdByPid(playerHandler.myPlayerPid));
    console.log(userEntity);
    userEntity.state.timeToAct = 1;
    var seatEntity = entityHandler.getEntityById(view.seatHandler.getSeatEntityIdBySeatNumber(userEntity.state.seatId));
    view.seatHandler.setSeatEntityToPassive(seatEntity);

}

UserInput.prototype.endUserTurn = function() {
    this.hideActionButtons();
    this.clearUserEntityTimeToAct();

}

UserInput.prototype.hideActionButtons = function() {
    for (index in this.inputButtons) {
        console.log(this.inputButtons[index]);
        console.log("#### HIDING " + this.inputButtons[index].divId);
         document.getElementById(this.inputButtons[index].divId).style.visibility = "hidden";
    }
};

UserInput.prototype.showPlaceBet = function(playerAction) {
    this.setBetValue(playerAction.minAmount);
    document.getElementById(this.inputButtons.betButton.divId).style.visibility = "visible";
};

UserInput.prototype.showRaiseBet = function(playerAction) {
    this.setRaiseValue(playerAction.minAmount);
    document.getElementById(this.inputButtons.raiseButton.divId).style.visibility = "visible";
};

UserInput.prototype.showCall = function(playerAction) {
    this.setCallValue(playerAction.minAmount);
    document.getElementById(this.inputButtons.callButton.divId).style.visibility = "visible";
};

UserInput.prototype.showCheck = function(playerAction) {
    document.getElementById(this.inputButtons.checkButton.divId).style.visibility = "visible";
};

UserInput.prototype.showFold = function() {
    document.getElementById(this.inputButtons.foldButton.divId).style.visibility = "visible";
};

UserInput.prototype.setBetValueMinMax = function(min, valueMax) {

}

UserInput.prototype.setCallValue = function(value) {
//    document.getElementById(this.inputButtons.callButton.valueDivId).innerHTML = currencyFormatted(value);
};

UserInput.prototype.setRaiseValue = function(value) {
//    document.getElementById(this.inputButtons.raiseButton.valueDivId).innerHTML = currencyFormatted(value);
};

UserInput.prototype.setBetValue = function(value) {
//    document.getElementById(this.inputButtons.betButton.valueDivId).innerHTML = currencyFormatted(value);
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
//    var userEntity = entityHandler.getEntityById(playerHandler.getPlayerEntityIdByPid(playerHandler.myPlayerPid));
//    if (!userEntity || view.seatHandler.activeSeatEntity == null) {
//        console.log("no user entity yet!");
//        return;
//    }
//    if (userEntity.id == view.seatHandler.activeSeatEntity.occupant.id) {
//        console.log("User Has time to act");
//        var percentRemaining = playerHandler.getPlayerEntityActionTimePercentRemaining(userEntity, currentTime);
//    } else {
//        console.log("User is out of time");
//        percentRemaining = 0;
//    }
//
//    var uiEntity = entityHandler.getEntityById(this.entityId);
//    document.getElementById(uiEntity.ui.userProgressBarDivId).style.width = percentRemaining+"%";

};

UserInput.prototype.tick = function(currentTime) {
    this.setUserActionProgressBar(currentTime)


};
