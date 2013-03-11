var Poker = Poker || {};
Poker.TournamentView = Poker.TabView.extend({
    layoutManager : null,
    init :function(viewElementId,name,layoutManager) {
        this._super(viewElementId,name);
        this.layoutManager = layoutManager;
    },
    getTournamentId : function() {
        return this.layoutManager.tournamentId;
    },
    onViewActivated : function() {
        Poker.AppCtx.getTournamentManager().activateTournamentUpdates(this.layoutManager.tournamentId);
        this.activateTab();
    },
    onViewDeactivated : function() {
        Poker.AppCtx.getTournamentManager().deactivateTournamentUpdates(this.layoutManager.tournamentId);
        this.deactivateTab();
    }
});