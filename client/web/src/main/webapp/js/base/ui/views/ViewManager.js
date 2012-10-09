var Poker = Poker || {};
Poker.ViewManager = Class.extend({
    views : null,
    activeView : null,
    tabsContainer: null,
    lobbyView : null,
    loginView : null,
    init : function(tabsContainerId) {
        this.tabsContainer = $("#"+tabsContainerId);
        this.views = [];
        this.loginView = this.addView(new Poker.View("#loginView","Login"));
        this.lobbyView = this.addView(new Poker.View("#lobbyView","Lobby"));
        this.activateView(this.loginView);


    },
    onLogin : function(){
        this.activateView(this.lobbyView);
        this.loginView.setSelectable(false);
    },
    addTableView : function(id,name) {
      var view = this.addView(new Poker.View(id,name));
      this.activateView(view);
    },
    activateView : function(view) {
        if(this.activeView!=null) {
            this.activeView.deactivate();
        }
        this.activeView = view;
        view.activate();
    },
    addView : function(view) {
        this.views.push(view);
        this.tabsContainer.append(view.tabElement);
        var self = this;
        view.tabElement.click(function(e){
            self.activateView(view);
        });
        return view;
    }
});
