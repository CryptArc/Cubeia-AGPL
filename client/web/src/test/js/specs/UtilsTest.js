describe("Poker.Utils Test", function(){

    beforeEach(function() {

    });

    it("Format currency", function(){
        var c = Poker.Utils.formatCurrency(100);
        expect(c).toEqual("1.00");

        c = Poker.Utils.formatCurrency(12050);
        expect(c).toEqual("120.50");
    });

    it("Format currency string", function(){
        var c = Poker.Utils.formatCurrencyString(100);
        expect(c).toEqual("&euro;1.00");
    });

    it("Format blinds", function(){
        //no 0 decimal in blinds displayed in lobby
        var c = Poker.Utils.formatBlinds(100);
        expect(c).toEqual("1");

        c = Poker.Utils.formatBlinds(200);
        expect(c).toEqual("2");

        c = Poker.Utils.formatBlinds(250);
        expect(c).toEqual("2.5");

        c = Poker.Utils.formatBlinds(25);
        expect(c).toEqual("0.25");
    });

    it("Calculate distance", function(){
        var elementA = {
            offset : function(){ return {top : 100, left : 200} },
            height : function() { return 25; },
            width : function(){ return 50; }
        };
        var elementB = {
            height : function() { return 25; },
            width : function(){ return 50; },
            offset : function(){ return { top : 200, left : 300 } }
        };
        var c = Poker.Utils.calculateDistance(elementA,elementB);
        expect(c.left).toEqual(200); //200% of src elements width
        expect(c.top).toEqual(400); //400% of src elements height

        c = Poker.Utils.calculateDistance(elementB,elementA);
        expect(c.left).toEqual(-200); //-200% of src elements width
        expect(c.top).toEqual(-400); //-400% of src elements height

    });

});