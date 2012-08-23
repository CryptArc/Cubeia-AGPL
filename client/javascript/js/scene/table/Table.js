Table = function() {
    this.entityId = "tableEntity";
};

Table.prototype.createTableOfSize = function(numberOfSeats, containerId) {

    /*
     * the entityHandler creates, stores and returns an empty object which becomes the "tableEntity"
     */

    var tableEntity = entityHandler.addEntity(this.entityId);

    tableEntity.ui = {};
    tableEntity.state = {};
    tableEntity.state.active = "NYA";
    tableEntity.ui.divId = this.createVisualTable(containerId);
    tableEntity.ui.stateDivId = this.createTableStateIndicator(tableEntity.ui.divId);
    var joinButtonDivs = this.createTableJoinButton(tableEntity.ui.divId);
    tableEntity.ui.joinButtonFrameDivId = joinButtonDivs[0];
    tableEntity.ui.joinButtonDivId = joinButtonDivs[1];
    tableEntity.ui.joinButtonLabelDivId = joinButtonDivs[2];
    tableEntity.ui.tablePotDivId = this.createTablePot(tableEntity.ui.divId);


    pokerDealer.createDealerButton();

    this.showCurrentState();

    tableEntity.seats = {};

    var seatPositions = this.getSeatLocationsForTableWithSize(numberOfSeats);
    for (var i = 0; i < seatPositions.length; i++) {
        var seatNr = i;
        var seatEntity = view.seatHandler.createSeatNumberOnTableEntityAtXY(seatNr, tableEntity, seatPositions[seatNr][0], seatPositions[seatNr][1]);
        view.seatHandler.setSeatEntityToPassive(seatEntity);
        tableEntity.seats[seatNr] = seatEntity;
    }
	console.log(entityHandler.entities);

    /*
    * Now we can use the generic "positionVisualEntityAtSpatial" method in the SpatialManager to
    * position the seats according to their transforms.
    */

    this.updateTableSeatPositions();
    this.addLineSeparator();
};

Table.prototype.addLineSeparator = function() {
        var tableEntity = entityHandler.getEntityById(this.entityId);
        var parent = document.getElementById(tableEntity.ui.divId);
        var index = parent.getElementsByTagName("*");
        var lineDiv = document.createElement('div', [index]);
        lineDiv.setAttribute('id', 'lineSeparator');
        lineDiv.className = 'line_separator';

        parent.appendChild(lineDiv);
}

Table.prototype.addSelf = function(name) {
    var tableEntity = entityHandler.getEntityById(this.entityId);
    var parent = document.getElementById(tableEntity.ui.divId);
    var index = parent.getElementsByTagName("*");
    var nameDiv = document.createElement('div', [index]);
    nameDiv.setAttribute('id', 'hud_player_name');
    nameDiv.className = 'hud_player_name';
    nameDiv.innerHTML = name;
    parent.appendChild(nameDiv);

    var balanceDiv = document.createElement('div', [index]);
    balanceDiv.setAttribute('id', 'hud_balance');
    balanceDiv.className = 'hud_balance';
    balanceDiv.innerHTML = "<span style='color:#9bba00'>&euro;</span>4.50";
    parent.appendChild(balanceDiv);
}

Table.prototype.updateOwnBalance = function(balance) {
    // TODO: HUD
//    createDivElement
//    document.getElementById
}

Table.prototype.updateTableSeatPositions = function() {
    /*
     * The only thing we know here is that we want to use the "this.entityId" to find out table.
     *
     * The remaining data should be available within the entity by now.
     */
    var tableEntity = entityHandler.getEntityById(this.entityId);

    for (index in tableEntity.seats) {
        var seatEntity = tableEntity.seats[index];
        view.spatialManager.positionVisualEntityAtSpatial(seatEntity);
    }
};

Table.prototype.createTableStateIndicator = function(tableDivId) {
    var indicatorDivId = tableDivId+"_TableState";
    uiElementHandler.createDivElement(tableDivId, indicatorDivId, "", "table_state", null);
    return indicatorDivId;
};

Table.prototype.createTablePot = function(tableDivId) {
    var potAreaDivId = tableDivId+"_TablePot_area"
    uiElementHandler.createDivElement(tableDivId, potAreaDivId, "", "table_pot_area", null);
    var potDivId = potAreaDivId+"_TablePot";
    uiElementHandler.createDivElement(potAreaDivId, potDivId, "", "table_pot_text", null);
    uiElementHandler.createDivElement(potAreaDivId, potAreaDivId+"_label", "Pot size", "table_pot_label", null);

    return potDivId;
};

Table.prototype.createVisualTable = function(containerId) {
    // Add a visual component for the table entity and return the divId
    var tableDivId = containerId+"_Table";
    uiElementHandler.createDivElement(containerId, tableDivId, "", "poker_table", null);
//    uiElementHandler.createDivElement(tableDivId, tableDivId+"_logo", "", "poker_table_cubeia_logo", null);
//    uiElementHandler.createDivElement(tableDivId, tableDivId+"_blob", "", "table_center_blob", null);
    return tableDivId;
};

Table.prototype.createTableJoinButton = function(tableDivId) {

    var joinTable = function() {
        view.table.playerPressesJoinButton();
    }

    this.buttons = {
        inputButtons: {label: "Join", posX: 82.7, posY: 83, height: 120, width: 120, hasValue:false, clickFunction:joinTable}
    };


    var divIds = uiUtils.createActionButton(this.buttons.inputButtons, tableDivId);

    return divIds;
};

Table.prototype.playerPressesJoinButton = function() {
    var playerPid = playerHandler.myPlayerPid;
    joinGame();
    console.log(playerPid);
};

Table.prototype.showCurrentState = function() {
    var tableEntity = entityHandler.getEntityById(this.entityId);
    var divId = tableEntity.ui.stateDivId;
    var currentState = tableEntity.state.active;
//    document.getElementById(divId).innerHTML = currentState;

    if (currentState == "waiting") {
        document.getElementById(divId).style.backgroundColor = "9c4";
    } else if (currentState == "playing") {
        document.getElementById(divId).style.backgroundColor = "64c";
    }

};


Table.prototype.getSeatBySeatNumber = function(seatNr) {
	var tableEntity = entityHandler.getEntityById(this.entityId);
	return tableEntity.seats[seatNr];
};


Table.prototype.findSeatForNewPlayer = function() {
    var tableEntity = entityHandler.getEntityById(this.entityId);

    for (index in tableEntity.seats) {
        var occupant = tableEntity.seats[index].occupant;
        if (!occupant) {
            return tableEntity.seats[index];
        }
    }

};



Table.prototype.enableJoinTable = function() {
    var availableSeat = this.findSeatForNewPlayer();
    if (availableSeat == undefined) {
        this.showTableFull();
        return;
    }

    this.showJoinButton();

};

Table.prototype.showTableFull = function() {
    var tableEntity = entityHandler.getEntityById(this.entityId);
    var frameDivId = tableEntity.ui.joinButtonFrameDivId;
    var buttonDivId = tableEntity.ui.joinButtonDivId;
    var buttonLabelDivId = tableEntity.ui.joinButtonLabelDivId;

    document.getElementById(frameDivId).style.visibility = "visible";
    document.getElementById(buttonLabelDivId).innerHTML = "Full Table";


    if (document.getElementById(buttonDivId)) {
        document.getElementById(buttonDivId).style.visibility = "hidden";
    }
};

Table.prototype.showJoinButton = function() {
    var tableEntity = entityHandler.getEntityById(this.entityId);
    var buttonLabelDivId = tableEntity.ui.joinButtonLabelDivId;
    var buttonDivId = tableEntity.ui.joinButtonDivId;

    var joinTable = function() {
        view.table.playerPressesJoinButton();
    }

    document.getElementById(buttonLabelDivId).innerHTML = "Join";
    var buttonDivId = tableEntity.ui.joinButtonFrameDivId;
    document.getElementById(buttonDivId).style.visibility = "visible";
    document.getElementById(buttonDivId).onclick = function(e) {
    	joinTable();
    }
};

Table.prototype.hideJoinButton = function() {
    var tableEntity = entityHandler.getEntityById(this.entityId);
    var buttonDivId = tableEntity.ui.joinButtonFrameDivId;
    document.getElementById(buttonDivId).style.visibility = "hidden";
}

Table.prototype.setLeaveTableFunction = function() {
    var tableEntity = entityHandler.getEntityById(this.entityId);
//    var frameDivId = tableEntity.ui.joinButtonFrameDivId;
    var buttonDivId = tableEntity.ui.joinButtonDivId;
    var buttonLabelDivId = tableEntity.ui.joinButtonLabelDivId;
    view.table.hideJoinButton();
    var leaveFunction = function() {
        view.table.hideJoinButton();
        playerActions.leaveTable();

    }

//    document.getElementById(buttonLabelDivId).innerHTML = "Leave Table";
//    document.getElementById(buttonDivId).onclick = function(e) {
//        leaveFunction();
//    }


};

Table.prototype.getSeatBySeatedEntityId = function(entityId) {
    var tableEntity = entityHandler.getEntityById(this.entityId);

    for (index in tableEntity.seats) {
        var occupant = tableEntity.seats[index].occupant;
        if (occupant.id == entityId) {
            return tableEntity.seats[index];
        }
    }

};

/**
 * Show player timeoutbar etc.
 * @param {Number} pid player id
 * @param {Number}  timeToAct time to act in milliseconds
  */
Table.prototype.startPlayerCountDown = function(pid, timeToAct) {
	view.seatHandler.setCurrentPlayerActionTimeout(pid, timeToAct);
};

/**
 * Handle request action from server
 * @param {POKER_PROTOCOL.RequestAction} actionRequest
 */
Table.prototype.handleRequestAction = function(requestAction) {
    console.log(requestAction);
	

	this.potUpdated(requestAction.currentPotSize);
	this.startPlayerCountDown(requestAction.player, requestAction.timeToAct);
    console.log("player in " + requestAction.player +  " me: " + parseInt(pid));
	if ( requestAction.player == parseInt(pid) ) {
		this.clearButtonStates();
		// save it for later
		this.lastActionRequest = requestAction;
		this.validActions = [{},{},{},{},{},{},{},{}];
		for (var i = 0; i < requestAction.allowedActions.length; i ++) {
			var playerAction = requestAction.allowedActions[i];
			
			// Auto small/big blind
			if ( playerAction.type === POKER_PROTOCOL.ActionTypeEnum.SMALL_BLIND || 
					playerAction.type === POKER_PROTOCOL.ActionTypeEnum.BIG_BLIND ) {
				sendAction(requestAction.seq, playerAction.type, playerAction.minAmount);
				return;
	
			} else {
				this.handlePlayerActionRequest(playerAction);
			}
		}
	}

};

/**
 * Clear button states
 */
Table.prototype.clearButtonStates = function() {
	userInput.hideActionButtons();
};

/**
 * currentPotSize updated
 * @param {Number} current pot size
 */
Table.prototype.potUpdated = function(amount) {
	var tableEntity = entityHandler.getEntityById(this.entityId);
    document.getElementById(tableEntity.ui.tablePotDivId).innerHTML = "<span style='color: #9bba00;'>&euro;</span>" +
        currencyFormatted(amount);

};

/**
 * Handle hand end
 * @param {POKER_PROTOCOL.BestHand} bestHand
 */
Table.prototype.handleBestHand = function(bestHand) {
	console.log(bestHand.cards);
	var handString = "";
	var playerEntityId = playerHandler.getPlayerEntityIdByPid(bestHand.player);
	var playerEntity = entityHandler.getEntityById(playerEntityId);
	var playerName = playerEntity.name;
	
	switch ( bestHand.handType ) {
		case POKER_PROTOCOL.HandTypeEnum.HIGH_CARD: 
			handString = "High Card";
			break;
		case POKER_PROTOCOL.HandTypeEnum.PAIR: 
			handString = "Pair";
			break;
		case POKER_PROTOCOL.HandTypeEnum.TWO_PAIR: 
			handString = "Two Pair";
			break;
		case POKER_PROTOCOL.HandTypeEnum.THREE_OF_A_KIND: 
			handString = "Three of a Kind";
			break;
		case POKER_PROTOCOL.HandTypeEnum.STRAIGHT: 
			handString = "Straight";
			break;
		case POKER_PROTOCOL.HandTypeEnum.FLUSH: 
			handString = "Flush";
			break;
		case POKER_PROTOCOL.HandTypeEnum.FULL_HOUSE: 
			handString = "Full House";
			break;
		case POKER_PROTOCOL.HandTypeEnum.FOUR_OF_A_KIND: 
			handString = "Four of a Kind";
			break;
		case POKER_PROTOCOL.HandTypeEnum.STRAIGHT_FLUSH: 
			handString = "Straight Flush";
			break;
		case POKER_PROTOCOL.HandTypeEnum.ROYAL_STRAIGHT_FLUSH: 
			handString = "Royal Straight Flush";
			break;
			
	}
	view.textFeedback.addLogText("" + playerName + " shows " + handString+".")
    view.textFeedback.addSeatEventText(bestHand.player, handString);
	console.log("Player " + playerName + " shows " + handString);
	
};
/**
 * Handle perform action from server
 * @param {POKER_PROTOCOL.PerformAction} action
 */
Table.prototype.handlePerformAction = function(performAction) {
    console.log(performAction);

	if ( performAction.player == parseInt(pid) ) {
		this.clearButtonStates();
		
	} else {
		switch ( performAction.action.type) {
			case POKER_PROTOCOL.ActionTypeEnum.CHECK:
                playerActions.handlePlayerActionFeedback(performAction.player, "Check", null);
				// player performAction.pid checks
				break;
			case POKER_PROTOCOL.ActionTypeEnum.CALL:
                playerActions.handlePlayerActionFeedback(performAction.player, "Call", null);
				// player performAction.pid calls
				break;
			case POKER_PROTOCOL.ActionTypeEnum.BET:
                var value = currencyFormatted(performAction.betAmount);
                playerActions.handlePlayerActionFeedback(performAction.player, "Bet", value);
				// player performAction.pid bets performAction.action.minAmount
				break;
			case POKER_PROTOCOL.ActionTypeEnum.RAISE:
                var value = currencyFormatted(performAction.betAmount);
                playerActions.handlePlayerActionFeedback(performAction.player, "Raise", value);
				// player performAction.pid raises performAction.action.minAmount
				break;
			case POKER_PROTOCOL.ActionTypeEnum.FOLD:
                playerActions.handlePlayerActionFeedback(performAction.player, "Fold", null);
				// player performAction.pid folds
				break;
		
		}
	}
};

/**
 * Handle one action request
 * @param {POKER_PROTOCOL.PlayerAction} playerAction
 */
Table.prototype.handlePlayerActionRequest = function(playerAction) {
	console.log(playerAction);
	
	switch (playerAction.type) {
		case POKER_PROTOCOL.ActionTypeEnum.CHECK:
            userInput.setCheckAvailable(playerAction);
			break;
		case POKER_PROTOCOL.ActionTypeEnum.CALL:
            userInput.setCallAvailable(playerAction);
			break;
		case POKER_PROTOCOL.ActionTypeEnum.BET:
            userInput.setBetAvailable(playerAction);
			break;
		case POKER_PROTOCOL.ActionTypeEnum.RAISE:
            userInput.setRaiseAvailable(playerAction);
			break;
		case POKER_PROTOCOL.ActionTypeEnum.FOLD:
            userInput.setFoldAvailable();
			break;
	}
	
	this.validActions[playerAction.type] = playerAction;
	
};

Table.prototype.getSeatLocationsForTableWithSize = function(numberOfSeats) {
     /*
     *   XY Coordinates for seat anchor points in % of parent Div
     *
     *   This should with relative ease be made procedural.
     *   ...lets begin with "Switch Macka"
     *
     */
	var seatCoordinate = [];

	switch (numberOfSeats) {
		case 0:
			break;
		case 2:
			seatCoordinate = [[50, 70], [50, 25]];
			break;
		case 4:
			seatCoordinate = [[50, 70], [20, 40], [50, 25], [80, 40]];
			break;
		case 6:
            var zero = [50, 68];
            var first = [15, 58];
            var second = [15, 20];
            var third = [50, 9];
            var fourth = [100 - second[0], second[1]];
            var fifth = [100 - first[0], first[1]];
            seatCoordinate = [zero, first, second, third, fourth, fifth];
			break;
		case 8:
			var zero = [50, 68];
	        var first = [25, 55];
	        var second = [15, 40];
	        var third = [30, 30];
	        var fourth = [50, 25];
	        var fifth = [100 - third[0], third[1]];
	        var sixth = [100 - second[0], second[1]];
	        var seventh = [100 - first[0], first[1]];
	        seatCoordinate = [zero, first, second, third, fourth, fifth, sixth, seventh];
	        break;
		case 10:
//	        var zero = [50, 68];
	        var first = [11, 62];
	        var second = [11, 35];
	        var third = [11, 10];
	        var fourth = [30, 10];
	        var fifth = [50, 10];
	        var sixth = [100 - fourth[0], fourth[1]];
	        var seventh = [100 - third[0], third[1]];
	        var eigth = [100 - second[0], second[1]];
	        var ninth = [100 - first[0], first[1]];

	        seatCoordinate = [first, second, third, fourth, fifth, sixth, seventh, eigth, ninth];
	        break;
	}
    return seatCoordinate;
};
