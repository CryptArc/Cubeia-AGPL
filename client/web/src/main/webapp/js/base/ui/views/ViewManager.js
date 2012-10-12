var Poker = Poker || {};
Poker.ViewManager = Class.extend({
    currentId : 0,
    views : null,
    activeView : null,
    tabsContainer: null,
    lobbyView : null,
    loginView : null,
    cssAnimator : null,
    swiper : null,
    init : function(tabsContainerId) {
        var self = this;
        this.tabsContainer = $("#"+tabsContainerId);
        this.views = [];
        this.loginView = this.addView(new Poker.View("#loginView","Login"));
        this.lobbyView = this.addView(new Poker.View("#lobbyView","Lobby"));
        this.cssAnimator = new Poker.CSSAnimator();

        this.activateView(this.loginView);

    },
    getNextView : function() {
        for(var i = 0; i<this.views.length; i++) {
            if(this.views[i].id == this.activeView.id) {
                return this.views[(i+1)%this.views.length];
            }
        }
        return null;
    },
    nextView : function() {
        this.activateView(this.getNextView());
    },
    getPreviousView : function() {
        for(var i = 0; i<this.views.length; i++) {
            if(this.views[i].id == this.activeView.id) {
                return this.views[(i-1+this.views.length)%this.views.length];
            }
        }
        return null;
    },
    previousView : function() {
        this.activateView(this.getPreviousView());
    },
    nextId : function() {
        return this.currentId++;
    },
    onLogin : function(){
        var self = this;
        this.activateView(this.lobbyView);
        this.loginView.close();
        this.views.splice(0,1);

        this.swiper = new Poker.ViewSwiper($(".view-container"),
            function(){
                self.nextView();
            },
            function(){
                self.previousView();
            });
    },
    requestTableFocus : function(tableId) {
       var v = this.findViewByTableId(tableId);
       if(v!=null) {
           v.requestFocus();
       }
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
      //TODO: move code to this class
      updateTableViews();
    },
    activateView : function(view) {
        console.log("ACTIVATING VIEW");
        if(this.activeView!=null) {
            this.activeView.deactivate();
        }
        this.activeView = view;
        view.activate();

        if(this.swiper!=null) {
            this.swiper.setElements(
                this.getPreviousView(),
                this.activeView,
                this.getNextView()
            );
        }

    },
    addView : function(view) {
        if(view.id==null) {
            view.id = this.nextId();
        }
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
