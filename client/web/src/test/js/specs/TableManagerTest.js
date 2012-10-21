describe("Poker.TableManager Test", function(){
    var tableManager = null;
    var table = null;
    beforeEach(function() {
        tableManager = new Poker.TableManager();
        table = null;
    });

    it("test create table", function(){
        var mockTableListener = jasmine.createSpyObj('mockTableListener',['onTableCreated']);

        tableManager.createTable(1,10, "tableName", [mockTableListener]);
        table = tableManager.getTable(1);
        expect(table.id).toEqual(1);
        expect(tableManager.getTable(1).capacity).toEqual(10);
        expect(mockTableListener.onTableCreated).toHaveBeenCalled();
    });

    it("test add player", function(){
        var mockTableListener = jasmine.createSpyObj('mockTableListener',['onTableCreated','onPlayerAdded']);
        tableManager.createTable(1,10,"tableName", [mockTableListener]);

        tableManager.addPlayer(1,1,1,"name1");
        table = tableManager.getTable(1);
        var player1 = table.getPlayerById(1);
        expect(player1.id).toEqual(1);
        expect(player1.name).toEqual("name1");

        tableManager.addPlayer(1,2,2,"name2");
        var table = tableManager.getTable(1);
        var player1 = table.getPlayerById(2);
        expect(player1.id).toEqual(2);
        expect(player1.name).toEqual("name2");
        expect(mockTableListener.onPlayerAdded).toHaveBeenCalled();
        return mockTableListener;

    });

    var setUpTableAnPlayers = function(mockTableListener) {


    };

    it("test remove player", function(){
        var mockTableListener = jasmine.createSpyObj('mockTableListener',
            ['onTableCreated','onPlayerAdded','onPlayerRemoved']);

        tableManager.createTable(1,10,"tableName", [mockTableListener]);
        tableManager.addPlayer(1,1,1,"name1");
        table = tableManager.getTable(1);
        tableManager.addPlayer(1,2,2,"name2");

        tableManager.removePlayer(1,1);

        expect(table.getNrOfPlayers()).toEqual(1);
        expect(table.getPlayerById(1)).toEqual(null);

        expect(mockTableListener.onPlayerAdded).toHaveBeenCalled();
        expect(mockTableListener.onPlayerRemoved).toHaveBeenCalled();
    });


    it("Update player status", function(){
        var mockTableListener = jasmine.createSpyObj('mockTableListener',
            ['onTableCreated','onPlayerAdded','onPlayerRemoved','onPlayerUpdated']);

        tableManager.createTable(1,10,"tableName", [mockTableListener]);
        tableManager.addPlayer(1,1,1,"name1");
        table = tableManager.getTable(1);
        tableManager.addPlayer(1,2,2,"name2");
        tableManager.updatePlayerStatus(1,1,Poker.PlayerTableStatus.SITTING_IN);
        tableManager.updatePlayerStatus(1,2,Poker.PlayerTableStatus.SITTING_IN);

        expect(table.getPlayerById(1).tableStatus.id).toEqual(Poker.PlayerStatus.SITTING_IN.id);
        expect(table.getPlayerById(2).tableStatus.id).toEqual(Poker.PlayerStatus.SITTING_IN.id);

        expect(mockTableListener.onPlayerAdded).toHaveBeenCalled();
        expect(mockTableListener.onPlayerUpdated).toHaveBeenCalled();

    });

    it("Update player balance", function(){
        var mockTableListener = jasmine.createSpyObj('mockTableListener',
            ['onTableCreated','onPlayerAdded','onPlayerRemoved','onPlayerUpdated']);

        tableManager.createTable(1,10,"tableName", [mockTableListener]);
        tableManager.addPlayer(1,1,1,"name1");
        table = tableManager.getTable(1);
        tableManager.addPlayer(1,2,2,"name2");
        tableManager.updatePlayerBalance(1,1,1000);
        tableManager.updatePlayerBalance(1,2,2000);

        expect(table.getPlayerById(1).balance).toEqual(1000);
        expect(table.getPlayerById(2).balance).toEqual(2000);

        expect(mockTableListener.onPlayerAdded).toHaveBeenCalled();
        expect(mockTableListener.onPlayerUpdated).toHaveBeenCalled();

    });

    it("Deal cards", function(){
        var mockTableListener = jasmine.createSpyObj('mockTableListener',
            ['onTableCreated','onPlayerAdded','onPlayerUpdated','onDealPlayerCard']);

        tableManager.createTable(1,10,"tableName", [mockTableListener]);
        tableManager.addPlayer(1,1,1,"name1");
        table = tableManager.getTable(1);
        tableManager.addPlayer(1,2,2,"name2");
        tableManager.updatePlayerBalance(1,1,1000);
        tableManager.updatePlayerBalance(1,2,2000);

        tableManager.dealPlayerCard(1,1,1,"  ");
        tableManager.dealPlayerCard(1,1,2,"  ");

        tableManager.dealPlayerCard(1,2,3,"  ");
        tableManager.dealPlayerCard(1,2,4,"  ");

        expect(mockTableListener.onDealPlayerCard).toHaveBeenCalled();
        expect(mockTableListener.onPlayerAdded).toHaveBeenCalled();
        expect(mockTableListener.onPlayerUpdated).toHaveBeenCalled();

    });

});