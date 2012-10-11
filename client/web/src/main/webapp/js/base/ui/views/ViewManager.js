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
        var self = this;

        $$(".view-container").swiping(function(e){

        });

    },
    nextView : function() {
        for(var i = 0; i<this.views.length; i++) {
            if(this.views[i] == this.activeView) {
                this.activateView(this.views[(i-1+this.views.length)%this.views.length]);
                break;
            }
        }
    },
    previousView : function() {
      for(var i = 0; i<this.views.length; i++) {
          if(this.views[i] == this.activeView) {
            this.activateView(this.views[(i+1)%this.views.length]);
            break;
          }
      }
    },
    onLogin : function(){
        this.activateView(this.lobbyView);
        this.loginView.close();
        this.views.splice(0,1);
    },
    removeTableView : function(tableId) {
        for(var i = 0; i<this.views.length; i++) {
            var v = this.views[i];
            if(typeof(v.getTableId)!="undefined" && v.getTableId()==tableId) {
                this.activateView(this.lobbyView);
                v.close();
                this.views.splice(i,1);
            }
        }
    },
    activateViewByTableId : function(tableId) {
        var v = this.findViewByTableId(tableId);
        if(v!=null) {
            this.activateView(v);
        }
    },
    findViewByTableId : function(tableId) {
        for(var i = 0; i<this.views.length; i++) {
            var v = this.views[i];
            if(typeof(v.getTableId)!="undefined" && v.getTableId()==tableId) {
                return v;
            }
        }

    },
    addTableView : function(tableLayoutManager,name) {
      var view = this.addView(new Poker.TableView(tableLayoutManager,name));
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

        var count = this.getVisibleTabCount();
        this.tabsContainer.find("li").css({width : (100/count) + "%" });

        return view;
    },
    getVisibleTabCount : function() {
        var count = 0;
        for(var i = 0; i<this.views.length;i++) {
            if(this.views[i].selectable == true) {
                count++;
            }
        }
        return count;
    }
});
