var Poker = Poker || {};

describe("Poker.FutureActions Test", function(){


    beforeEach(function() {

    });
    afterEach(function(){

    });

    it("test future actions call", function(){
        var futureActions = new Poker.FutureActions($("#futureActionsTestContainer"));

        futureActions.setFutureActions([Poker.FutureActionType.CALL_CURRENT_BET],10,0);

        futureActions.setSelectedFutureAction(Poker.FutureActionType.CALL_CURRENT_BET);

        var callAction = new Poker.Action(Poker.ActionType.CALL,10,10);
        var foldAction = new Poker.Action(Poker.ActionType.FOLD,0,0);
        var resultAction = futureActions.getAction([callAction,foldAction]);
        expect(resultAction).toBeDefined();
        expect(resultAction.type.id).toEqual(Poker.ActionType.CALL.id);

    });

    it("test future actions transitions", function(){
        var futureActions = new Poker.FutureActions($("#futureActionsTestContainer"));

        futureActions.setFutureActions([Poker.FutureActionType.CHECK_OR_FOLD],0,0);
        //player preselects check or fold
        futureActions.setSelectedFutureAction(Poker.FutureActionType.CHECK_OR_FOLD);

        //some one bets, player gets new future actions
        futureActions.setFutureActions([
                Poker.FutureActionType.CALL_CURRENT_BET,
                Poker.FutureActionType.FOLD
            ],10,0);

        //check or fold should now transition to fold since player can't check anymore
        expect(futureActions.selectedFutureActionType.id).toEqual(Poker.FutureActionType.FOLD.id);

        futureActions.clear();

        expect(futureActions.selectedFutureActionType).toBeNull();

    });

    it("test future actions transitions RAISE/RAISE_ANY", function(){
        var futureActions = new Poker.FutureActions($("#futureActionsTestContainer"));

        expect(futureActions.selectedFutureActionType).toBeNull();

        var types = [
            Poker.FutureActionType.FOLD,
            Poker.FutureActionType.CALL_CURRENT_BET,
            Poker.FutureActionType.RAISE,
            Poker.FutureActionType.RAISE_ANY
        ];
        //player has to call 10 or raise 20
        futureActions.setFutureActions(types,10,20);

        //player selects RAISE
        futureActions.setSelectedFutureAction(Poker.FutureActionType.RAISE);

        //some one raised
        futureActions.setFutureActions(types,20,30);

        //players future action "RAISE" is not longer valid
        expect(futureActions.selectedFutureActionType).toBeNull();
        futureActions.clear();

        //player has to call 10 or raise 20
        futureActions.setFutureActions(types,10,20);
        //player selects raise any
        futureActions.setSelectedFutureAction(Poker.FutureActionType.RAISE_ANY);
        //someone else raises
        futureActions.setFutureActions(types,20,30);
        //player should still have raise any selected
        expect(futureActions.selectedFutureActionType).toEqual(Poker.FutureActionType.RAISE_ANY);



    });

    it("test future actions transitions CALL ANY", function(){
        var futureActions = new Poker.FutureActions($("#futureActionsTestContainer"));

        expect(futureActions.selectedFutureActionType).toBeNull();

        var types = [
            Poker.FutureActionType.FOLD,
            Poker.FutureActionType.CALL_CURRENT_BET,
            Poker.FutureActionType.CHECK_OR_CALL_ANY
        ];
        //player has to call 10 or raise 20
        futureActions.setFutureActions(types,10,20);

        //player selects RAISE
        futureActions.setSelectedFutureAction(Poker.FutureActionType.CALL_CURRENT_BET);

        //some one raised
        futureActions.setFutureActions(types,20,30);

        //players future action "RAISE" is not longer valid
        expect(futureActions.selectedFutureActionType).toBeNull();
        futureActions.clear();

        //player has to call 10 or raise 20
        futureActions.setFutureActions(types,10,20);
        //player selects raise any
        futureActions.setSelectedFutureAction(Poker.FutureActionType.CHECK_OR_CALL_ANY);
        //someone else raises
        futureActions.setFutureActions(types,20,30);
        //player should still have raise any selected
        expect(futureActions.selectedFutureActionType).toEqual(Poker.FutureActionType.CHECK_OR_CALL_ANY);



    });


});