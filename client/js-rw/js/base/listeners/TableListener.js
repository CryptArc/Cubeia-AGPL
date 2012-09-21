var Poker = Poker || {};
/**
 * Table listener interface
 * @type {*}
 */
Poker.TableListener = Class.extend({
    init : function() {},
    onTableCreated : function() {},
    /**
     * When a player is added to the table
     * @param seat  - the seat Number
     * @param player the Poker.Player that was added
     */
    onPlayerAdded : function(seat,player) {
        console.log("Poker.TableListener.onPlayerAdded");
    },
    /**
     * Notifies that a player has been updated
     * @param player - the updated Poker.Player
     */
    onPlayerUpdated : function(player){
        console.log("Poker.TableListener.onPlayerUpdated");
    },
    /**
     * Notifies when a new hand is about to start
     * @param dealerSeatId what seat will be the dealer of the new hand
     */
    onStartHand : function(dealerSeatId) {
        console.log("Poker.TableListener.onStartHand");
    },
    /**
     * Notifies when a player has made an action
     * @param {Poker.Player} player - the  who did the action
     * @param {Poker.Action} action -  action the player did
     * @param {Number} amount - the amount related to the action
     */
    onPlayerActed : function(player,actionType,amount){
        console.log("Poker.TableListener.onPlayerAction");
    },
    /**
     * Notifies when a player gets a card dealt
     * @param {Poker.Player} player - the player to deal
     * @param {String} cardString - the card string
     */
    onDealPlayerCard : function(player,cardId,cardString) {
        console.log("Poker.TableListener.onDealPlayerCard");
    },
    /**
     * Notifies when the main pot is updated
     * @param {Poker.Pot} pot the main pot
     */
    onMainPotUpdate : function(pot) {
        console.log("Poker.TableListener.onMainPotUpdate");
    },
    /**
     * Notifies that a player has been requested to act
     *
     * @param {Poker.Player} player - the player who's requested to act
     * @param {Array<Poker.Action>} allowedActions the allowed actions the player can take
     * @param {int} timeToAct - the time the player has to act
     */
    onRequestPlayerAction : function(player,allowedActions,timeToAct){

    },
    onDealCommunityCard : function(cardId,cardString) {

    },
    onExposePrivateCard : function(cardId,cardString) {

    },
    onPlayerHandStrength : function(player, hand) {

    },
    onLeaveTableSuccess : function() {

    },
    onBettingRoundComplete : function() {

    },
    onPlayerRemoved : function(playerId) {

    },
    onMoveDealerButton : function(seatId) {

    }

});