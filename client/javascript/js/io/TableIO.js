function getUrlParam(name) {
  name = name.replace(/[\[]/,"\\\[").replace(/[\]]/,"\\\]");
  var regexS = "[\\?&]"+name+"=([^&#]*)";
  var regex = new RegExp( regexS );
  var results = regex.exec( window.location.href );
  if( results == null )
    return "";
  else
    return results[1];
}
		
function handleSeatInfo(seatInfoPacket) {
	console.log("seatInfo pid[" + seatInfoPacket.player.pid + "]  seat[" +  seatInfoPacket.seat +"]");

	seatPlayer(seatInfoPacket.player.pid, seatInfoPacket.seat, seatInfoPacket.player.nick);
}

function handleNotifyLeave(notifyLeavePacket) {
	playerHandler.unseatPlayer(notifyLeavePacket.pid);
}

function handleNotifyJoin(notifyJoinPacket) {
    console.log("NOTIFY JOIN!!");
	seatPlayer(notifyJoinPacket.pid, notifyJoinPacket.seat, notifyJoinPacket.nick);
}

function handleJoinResponse(joinResponsePacket) {
	seatPlayer(pid, joinResponsePacket.seat, screenname);
}

function seatPlayer(pid, seat, nick) {
    console.log("Seating player " + nick);
	playerHandler.addWatchingPlayer(pid, nick);
    playerHandler.seatPlayerIdAtTable(pid, seat);
};


function leaveTable() {
	connector.leaveTable(tableid);
};

function handlePacket(protocolObject) {
	switch (protocolObject.classId) {
		case FB_PROTOCOL.NotifyJoinPacket.CLASSID :
			handleNotifyJoin(protocolObject);
			break;
		case FB_PROTOCOL.NotifyLeavePacket.CLASSID :
			handleNotifyLeave(protocolObject);
			break;
		case FB_PROTOCOL.SeatInfoPacket.CLASSID :
			handleSeatInfo(protocolObject);
			break;
		case FB_PROTOCOL.JoinResponsePacket.CLASSID :
			view.table.setLeaveTableFunction();
			handleJoinResponse(protocolObject);
			break;
		case FB_PROTOCOL.GameTransportPacket.CLASSID :
			handleGameDataPacket(protocolObject);
			break;
		case FB_PROTOCOL.WatchResponsePacket.CLASSID :
			view.table.enableJoinTable();
			break;
		case FB_PROTOCOL.LeaveResponsePacket.CLASSID :
			callReload();
			break;
	}
};

function handleGameDataPacket(packet) {
	pokerProtocolHandler.handleGameTransportPacket(packet);
};


function currencyFormatted(amount) {
	return parseFloat(amount/100).toFixed(2);
};

function joinGame() {
	connector.joinTable(tableid, -1);
};


function sendAction(seq, actionType, betAmount, raiseAmount) {
	var performAction = new POKER_PROTOCOL.PerformAction();
	performAction.player = pid;
	performAction.action = new POKER_PROTOCOL.PlayerAction();
	console.log("sending action type=" + actionType);
	performAction.action.type = actionType;
	performAction.action.minAmount = 0;
	performAction.action.maxAmount = 0;
	performAction.betAmount = betAmount;
	performAction.raiseAmount = raiseAmount || 0;
	performAction.timeOut = 0;
	performAction.seq = seq;
	
	sendGameTransportPacket(performAction);
};

function sendGameTransportPacket(gamedata) {
//	var byteArray = gamedata.save();
    connector.sendStyxGameData(0, tableid, gamedata);
//	connector.sendGameTransportPacket(0, tableid, gamedata.classId(), byteArray);
};


function suppressBackspace(evt) {
    evt = evt || window.event;
    var target = evt.target || evt.srcElement;

    if (evt.keyCode == 8 && !/input|textarea/i.test(target.nodeName)) {
        return false;
    }
};
