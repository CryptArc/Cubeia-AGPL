PretendTraffic = function() {
    this.numberPlayers = 0;
};

PretendTraffic.prototype.makeSomeTraffic = function(numberOfSeats) {

    this.setTableEntityToWaitingState(1000);
    this.round = 0;
    setInterval(function() {
        var select = Math.random()*2;
            if (select <= 1) {
                clearInterval(pretendTraffic.dealSpam);
                pretendTraffic.setTableEntityToWaitingState(1000);
                pretendTraffic.clearCards(300);
            } else {
            //    clearInterval(pretendTraffic.dealSpam)
                pretendTraffic.setTableEntityToPlayingState(100);
                pretendTraffic.dealCards(300, 1);
            }
    }, 600);


    setTimeout(function() {
        view.initTableView(numberOfSeats);
    }, 100);

    var dummies = 11;
    var delay = 200;

    for (var i = 0; i < dummies; i++) {
        var pid = "pid"+i+"";

        this.addWatchingPlayer(pid, delay);

        delay = delay + 100;

    }


    for (var i = 0; i < dummies - 2; i++) {
        var pid = "pid"+i+"";

        if (pid != playerHandler.myPlayerPid) {
            this.seatPlayerIdAtTable(pid, delay);
        }
        delay = delay + 200;
    }




/*
    setTimeout(function() {
        playerHandler.addWatchingPlayer("pid_#2")
    }, 600)

    setTimeout(function() {
        playerHandler.seatPlayerIdAtTable("pid_#1")
    }, 1000)
*/
};

PretendTraffic.prototype.setTableEntityToWaitingState = function(delay) {
    setTimeout(function() {
        entityHandler.setTableEntityToWaitingState();
        view.table.enableJoinTable();
    }, delay);
};

PretendTraffic.prototype.setTableEntityToPlayingState = function(delay) {
    setTimeout(function() {
        entityHandler.setTableEntityToPlayingState();
        view.table.setLeaveTableFunction();
    }, delay);
};

PretendTraffic.prototype.addWatchingPlayer = function(pid, delay) {
    setTimeout(function() {
        playerHandler.addWatchingPlayer(pid);
    }, delay);
};

PretendTraffic.prototype.seatPlayerIdAtTable = function(pid, delay) {
    setTimeout(function() {
        playerHandler.seatPlayerIdAtTable(pid);
    }, delay);
};

PretendTraffic.prototype.getNameByPid = function(pid) {
    this.numberPlayers =+ 1;
    var name = "Name_"+this.numberPlayers+":"+pid;
    return name;
};

PretendTraffic.prototype.getACardId = function(color, cardNr) {
    var nr = cardNr+2;
    if (color == 1) return nr+"D";
    if (color == 2) return nr+"S";
    if (color == 3) return nr+"H";
    if (color == 4) return nr+"D";

};




PretendTraffic.prototype.dealCards = function(interval) {
    this.cardsDealt = 0;
    var tableEntity = entityHandler.getEntityById(view.table.entityId);
    this.round =+ 1;


    this.occupantPids = [];
    for (index in tableEntity.seats) {
        if (tableEntity.seats[index].occupant != null) {
            pretendTraffic.occupantPids.push(tableEntity.seats[index].occupant.pid);
        }
    }


    for (var i = 0; i < pretendTraffic.occupantPids.length; i++) {
        var color = Math.ceil(Math.random()*4);
        var cardId = pretendTraffic.getACardId(color, Math.ceil(Math.random()*10));

        var pid = pretendTraffic.occupantPids[i];
        console.log(cardId, color, i, pid);
        pretendTraffic.dealCardIdToPid(cardId, pid, this.round);
    }

    /*
        this.dealSpam = setInterval(function() {
            if (pretendTraffic.cardsDealt >= pretendTraffic.occupantPids.length) {


                if (round == 1) {
                    clearInterval(pretendTraffic.dealSpam);
                    console.log("Finished DealingRound 1")
                    pretendTraffic.dealCards(interval, 2)
                    return;
                }
                clearInterval(pretendTraffic.dealSpam);
                console.log("Finished DealingRound 2")

                return;
            }
            if (pretendTraffic.occupantPids.length == 0)  return;



            pretendTraffic.cardsDealt += 1;

        }, interval)
    */

};

PretendTraffic.prototype.dealCardIdToPid = function(cardId, pid, round) {
    pokerDealer.dealCardIdToPid(cardId, pid, round);

};