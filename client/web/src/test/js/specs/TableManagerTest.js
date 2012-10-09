describe("Poker.TableManager Test", function(){
    var tableManager = null;
    beforeEach(function() {
        tableManager = new Poker.TableManager();
    });

    it("test create table", function(){
        var mockTableListener = jasmine.createSpyObj('mockTableListener',['onTableCreated']);

        tableManager.createTable(1,10, "tableName", [mockTableListener]);
        expect(tableManager.getTableId()).toEqual(1);
        expect(tableManager.getTable().capacity).toEqual(10);
        expect(mockTableListener.onTableCreated).toHaveBeenCalled();
    });

    it("test add player", function(){
        var mockTableListener = jasmine.createSpyObj('mockTableListener',['onTableCreated','onPlayerAdded']);
        tableManager.createTable(1,10,"tableName", [mockTableListener]);

        tableManager.addPlayer(1,1,"name1");
        var table = tableManager.getTable();
        var player1 = table.getPlayerById(1);
        expect(player1.id).toEqual(1);
        expect(player1.name).toEqual("name1");

        tableManager.addPlayer(2,2,"name2");
        var table = tableManager.getTable();
        var player1 = table.getPlayerById(2);
        expect(player1.id).toEqual(2);
        expect(player1.name).toEqual("name2");
        expect(mockTableListener.onPlayerAdded).toHaveBeenCalled();

    });

});