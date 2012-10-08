class Player
  balance: 0
  tableStatus: null
  lastActionType: null
  cards: []

  constructor: (@id, @name) ->
    @tableStatus = Poker.PlayerTableStatus.SITTING_IN

  ###
  Adds a card to the player
  @param card to add
  ###
  addCard: (card) ->
    @cards.push card

window.Poker.Player = Player