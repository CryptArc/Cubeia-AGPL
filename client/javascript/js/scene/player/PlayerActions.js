PlayerActions = function() {

};

PlayerActions.prototype.getActionTextString = function(pid, action, value) {
    var playerEntityId = playerHandler.getPlayerEntityIdByPid(pid);
    var playerEntity = entityHandler.getEntityById(playerEntityId);
    var textString = ""+playerEntity.name+" "+action+"s";
    if (value) textString = textString+" "+value;
    textString = textString+".";
    return textString;
}

PlayerActions.prototype.handlePlayerActionFeedback = function(pid, action, value,actionType) {
    view.textFeedback.showSeatSpaceTextFeedback(pid, action, value,actionType);
    var textString = this.getActionTextString(pid, action, value);
    view.textFeedback.addLogText(textString);
};

PlayerActions.prototype.leaveTable = function() {
    console.log("Player pressed Leave Table");
    leaveTable();
    //window.close();
    //window.opener.location.reload(true)
//    window.opener.callReload();
};
var ACTIONS = {
   BET : "bet-action",
   CHECK : "check-action",
   FOLD : "fold-action",
   RAISE : "raise-action",
   CALL : 'call-action'
};

PlayerActions.prototype.bet = function(betValue) {
    view.textFeedback.showSeatSpaceTextFeedback(playerHandler.myPlayerPid, "Bet", betValue, ACTIONS.BET);
    view.textFeedback.addLogText("You Bet "+currencyFormatted(betValue)+".");
	sendAction(view.table.lastActionRequest.seq, POKER_PROTOCOL.ActionTypeEnum.BET, betValue, 0);
};

PlayerActions.prototype.check = function() {
    view.textFeedback.showSeatSpaceTextFeedback(playerHandler.myPlayerPid, "Check", null,ACTIONS.CHECK);
    view.textFeedback.addLogText("You Check.");
	sendAction(view.table.lastActionRequest.seq, POKER_PROTOCOL.ActionTypeEnum.CHECK, 0, 0);
};

PlayerActions.prototype.fold = function() {
    view.textFeedback.showSeatSpaceTextFeedback(playerHandler.myPlayerPid, "Fold", null, ACTIONS.FOLD);
    view.textFeedback.addLogText("You Check.");
	sendAction(view.table.lastActionRequest.seq, POKER_PROTOCOL.ActionTypeEnum.FOLD, 0, 0);
};

PlayerActions.prototype.raise = function(betValue) {
    view.textFeedback.showSeatSpaceTextFeedback(playerHandler.myPlayerPid, "Raise", betValue, ACTIONS.RAISE);
    view.textFeedback.addLogText("You Raise "+currencyFormatted(betValue)+".");
	sendAction(view.table.lastActionRequest.seq, POKER_PROTOCOL.ActionTypeEnum.RAISE, betValue, 0);
};

PlayerActions.prototype.call = function(betValue) {
    view.textFeedback.showSeatSpaceTextFeedback(playerHandler.myPlayerPid, "Call", null, ACTIONS.CALL);
    view.textFeedback.addLogText("You Call.");
	sendAction(view.table.lastActionRequest.seq, POKER_PROTOCOL.ActionTypeEnum.CALL, betValue, 0);
};

