"use strict";
var Poker = Poker || {};

Poker.FutureActionType = {
    CHECK : {
        id : "check",
        text : "Check"
    },
    CHECK_OR_FOLD : {
       id : "check-or-fold",
       text : "Check/fold"
   },
   CALL_CURRENT_BET : {
        id : "call-current-bet",
        text : "Call {{amount}}"
   },
   CHECK_OR_CALL_ANY : {
      id : "check-or-call-any",
      text : "Call any"
  },
  FOLD : {
      id : "fold",
      text : "Fold"
  },
  RAISE : {
      id : "bet",
      text : "Bet {{amount}}"
  },
  RAISE_ANY : {
      id : "bet-or-raise-any",
      text : "Bet/raise any"
  }
};