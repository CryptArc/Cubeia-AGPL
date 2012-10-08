class MyPlayer
  id: -1
  name: null
  betAmount: 0

  isLoggedIn: -> true unless id == -1

  onLogin: (playerId, name) ->
    console.log("COFFEEE!!!! Logging in, playerId = " + playerId + " name = " + name)
    this.id = playerId
    this.name = name

  clear: ->
    id = -1
    name = ""

window.Poker.MyPlayer = new MyPlayer()
