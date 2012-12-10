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

    it("Format cent string", function(){

        var c = Poker.Utils.formatCentString("");
        expect(c).toEqual("-");


        var c = Poker.Utils.formatCentString("0");
        expect(c).toEqual("-");


        var c = Poker.Utils.formatCentString("1");
        expect(c).toEqual("0.01");

        c = Poker.Utils.formatCentString("10");
        expect(c).toEqual("0.10");

        c = Poker.Utils.formatCentString("100");
        expect(c).toEqual("1");

        c = Poker.Utils.formatCentString("1000");
        expect(c).toEqual("10");
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