"use strict";
var Poker = Poker || {};
/**
 *
 * @extends {Poker.AbstractTableButtons}
 * @type {Poker.TableButtons}
 */
Poker.TableButtons = Poker.AbstractTableButtons.extend({
    init : function(view,actionCallback) {
        this._super(view,actionCallback);

        this._addTableButton($(".action-join",view),Poker.ActionType.JOIN,actionCallback);
        this._addTableButton($(".action-leave",view),Poker.ActionType.LEAVE,actionCallback);
        this._addTableButton($(".action-sit-in",view),Poker.ActionType.SIT_IN,actionCallback);
    },

    _addTableButton : function(elId,actionType,callback) {
        this.buttons.put(actionType.id,new Poker.ActionButton(elId,actionType,callback,false));
    }
});