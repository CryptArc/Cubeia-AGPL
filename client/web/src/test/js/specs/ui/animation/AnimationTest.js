describe("Poker.Utils Test", function(){
    var mockEl = null;
    beforeEach(function() {
        mockEl = {
            style : {}
        };
        //eq to a webkit browser
        mockEl.style["-webkit-transition"]="";
        mockEl.style["-webkit-transform"]="";
        mockEl.style["-webkit-transform-origin"]="";
    });

    it("Remaining time test", function(){
        var animation = new Poker.TransformAnimation(mockEl);

        animation.startTime = 0; //mock start time to 0
        animation.getNow = function(){ return 100 };// mock "now" time to be 100
        animation.setTimed(true);
        animation.addTransition("transform",1,"linear");

        var remaining = animation.getRemainingTime();

        expect(remaining).toEqual(0.9);

    });

    it("Scale timed animation test", function(){

        var animation = new Poker.TransformAnimation(mockEl);

        animation.startTime = 0; //mock start time to 0
        animation.getNow = function(){ return 500 };// mock "now" time to be 100
        animation.setTimed(true);
        animation.addTransition("transform",1,"linear");
        animation.addScale3d(0,0,1);
        animation.prepareElement();

        //start transform after 500 ms
        expect(mockEl.style["-webkit-transform"]).toEqual("scale3d(0.5,0.5,1)");
        animation.prepare();
        animation.animate();
        expect(mockEl.style["-webkit-transition"]).toEqual("-webkit-transform 0.5s linear");
        expect(mockEl.style["-webkit-transform"]).toEqual("scale3d(0,0,1)");


    });
    it("Translate timed animation test", function(){

        var animation = new Poker.TransformAnimation(mockEl);
        animation.startTime = 0; //mock start time to 0
        animation.getNow = function(){ return 500 }; //mock "now"
        animation.setTimed(true);
        //start values at time 0
        animation.addStartTranslate(0,0,0,"%");
        animation.addTransition("transform",1,"linear");
        animation.addTranslate3d(100,100,0,"%");
        //calculate the start values at time 500
        animation.prepareElement();

        //start transform after 500 ms
        expect(mockEl.style["-webkit-transform"]).toEqual("translate3d(50%,50%,0)");
        animation.prepare();
        animation.animate();
        //check complete values at time 1000 (transition complete)
        expect(mockEl.style["-webkit-transition"]).toEqual("-webkit-transform 0.5s linear");
        expect(mockEl.style["-webkit-transform"]).toEqual("translate3d(100%,100%,0)");


    });
});