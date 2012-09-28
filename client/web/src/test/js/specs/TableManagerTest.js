describe("Poker.TableManager Test", function(){
    var tableManager = null;
    beforeEach(function() {
        tableManager = new Poker.TableManager();
    });

    it("test create table", function(){
        var mockTableListener = jasmine.createSpyObj('mockTableListener',['onTableCreated']);

        tableManager.createTable(1,10, [mockTableListener]);
        expect(tableManager.getTableId()).toEqual(1);
        expect(tableManager.getTable().capacity).toEqual(10);
        expect(mockTableListener.onTableCreated).toHaveBeenCalled();
    });

});