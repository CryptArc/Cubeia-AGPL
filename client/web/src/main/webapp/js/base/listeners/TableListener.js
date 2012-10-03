var Poker = Poker || {};
/**
 * Table listener interface
 * @type {*}
 */
Poker.TableListener = Class.extend({
    init : function() {},
    onTableCreated : function() {
        console.log("Poker.TableListener.onTableCreated");
    },
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
    onRequestPlayerAction : function(player,allowedActions,timeToAct, mainPot){
        console.log("Poker.TableListener.onRequestPlayerAction");
    },
    /**
     * Notifies when a community card is dealt
     * @param cardId - the id of the card
     * @param cardString - the card string for the card being dealt
     */
    onDealCommunityCard : function(cardId,cardString) {
        console.log("Poker.TableListener.onDealCommunityCard");
    },
    /**
     * Notifies when a card should be exposed
     * @param cardId id of the card to expose
     * @param cardString the card string to use
     */
    onExposePrivateCard : function(cardId,cardString) {
        console.log("Poker.TableListener.onExposePrivateCard");
    },
    /**
     * Notifies when a hand strength for a specific player should be displayed
     * @param player the player to display the hand strength for
     * @param hand the hand to display
     */
    onPlayerHandStrength : function(player, hand) {
        console.log("Poker.TableListener.onPlayerHandStrength");
    },
    /**
     * Notifies when you left a table successfully,
     * used to clean up the UI
     */
    onLeaveTableSuccess : function() {
        console.log("Poker.TableListener.onLeaveTableSuccess");
    },
    /**
     * Notifies when a betting round is complete.
     * When a betting round is complete the bets are collected and put in the pot
     */
    onBettingRoundComplete : function() {
        console.log("Poker.TableListener.onBettingRoundComplete");
    },
    /**
     * When a player is removed from the table
     * @param playerId the id of the player that was removed
     */
    onPlayerRemoved : function(playerId) {
        console.log("Poker.TableListener.onPlayerRemoved");
    },
    /**
     * When the dealer button should be moved to a new seat
     * @param seatId which seat id to move the dealer button
     */
    onMoveDealerButton : function(seatId) {
        console.log("Poker.TableListener.onMoveDealerButton");
    },
    onPlayerToPotTransfers : function(transfers) {
        console.log("Poker.TableListener.onPlayerToPotTransfers");
    },
    onBuyInInfo : function(tableName,balanceInWallet, balanceOnTable, maxAmount, minAmount,mandatory) {
        console.log("Poker.TableListener.handleBuyInInfo");
    },
    onBuyInCompleted : function() {
        console.log("Poker.TableListener.onBuyInCompleted");
    },
    onBuyInError : function(msg) {

    }

});