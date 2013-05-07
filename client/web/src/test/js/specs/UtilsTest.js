describe("Poker.Utils Test", function(){

    beforeEach(function() {
        Poker.Utils.currencySymbol="&euro;";
    });

    it("Format currency", function(){
        var c = Poker.Utils.formatCurrency(100);
        expect(c).toEqual("1");

        c = Poker.Utils.formatCurrency(12050);
        expect(c).toEqual("120.50");


        c = Poker.Utils.formatCurrency(12000050);
        expect(c).toEqual("120,000.50");

        c = Poker.Utils.formatCurrency(12012000050);
        expect(c).toEqual("120,120,000.50");
    });

    it("Format currency string", function(){

        var c = Poker.Utils.formatCurrencyString(100);
        expect(c).toEqual("&euro;1");
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
});