var Poker = Poker || {};
/**
 * In charge for the main views.
 * Handles the following:
 *  * Showing/hiding views
 *  * Setting view sizes for fixed sized views (full window views, such as the tale view)
 *  * Swiping between views
 *  * Tabs
 * @type {Poker.ViewManager}
 */
Poker.ViewManager = Class.extend({
    currentId : 0,
    views : null,
    activeView : null,
    tabsContainer: null,
    lobbyView : null,
    loginView : null,
    cssAnimator : null,
    swiper : null,
    toolbar : null,
    baseWidth : 1024,
    mobileDevice : false,
    init : function(tabsContainerId) {
        var self = this;
        this.tabsContainer = $("#"+tabsContainerId);
        this.views = [];
        this.loginView = this.addView(new Poker.LoginView("#loginView","Login"));
        this.lobbyView = this.addView(new Poker.TabView("#lobbyView","Lobby"));
        this.cssAnimator = new Poker.CSSUtils();
        this.toolbar = $("#toolbar");
        this.activateView(this.loginView);

        $(window).resize(function(){
            self.setViewDimensions();
        });
        $(document).ready(function(){
           self.setViewDimensions();
        });

    },
    /**
     * Gets the next view null if there are no more views
     * @return {Poker.View}
     */
    getNextView : function() {
        for(var i = 0; i<this.views.length; i++) {
            if(this.views[i].id == this.activeView.id) {
                if(i==(this.views.length-1)){
                    return null;
                } else {
                    return this.views[i+1];
                }
            }
        }
        return null;
    },
    /**
     * Activates the next view
     */
    nextView : function() {
        this.activateView(this.getNextView());
    },
    /**
     * Gets the previous view, null if there are no previous view
     * @return {Poker.View}
     */
    getPreviousView : function() {
        for(var i = 0; i<this.views.length; i++) {
            if(this.views[i].id == this.activeView.id) {
                if(i==0) {
                    return null;
                } else {
                    return this.views[i-1];
                }
            }
        }
        return null;
    },
    /**
     * Activates the previous view
     */
    previousView : function() {
        this.activateView(this.getPreviousView());
    },
    /**
     * Return a unique view id
     * @return {Number}
     */
    nextId : function() {
        return this.currentId++;
    },
    /**
     * Called when user log in.
     * Shows the appropriate views and menus
     */
    onLogin : function(){
        var self = this;
        this.toolbar.show();
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
    /**
     * Will change a views tab to get the users attention
     * @param tableId - the id for the table who's view to request focus for
     */
    requestTableFocus : function(tableId) {
       var v = this.findViewByTableId(tableId);
       if(v!=null) {
           v.requestFocus();
       }
    },
    updateTableInfo : function(tableId,data)  {
        var v = this.findViewByTableId(tableId);
        if(v!=null) {
            v.updateInfo(data);
        }
    },
    /**
     * Removes a table view and activates the previous view
     * @param tableId - the id for the table who's view to close
     */
    removeTableView : function(tableId) {
        for(var i = 0; i<this.views.length; i++) {
            var v = this.views[i];
            if(typeof(v.getTableId)!="undefined" && v.getTableId()==tableId) {
                var pv = this.getPreviousView();
                v.close();
                this.views.splice(i,1);
                this.activeView = null;
                this.activateView(pv);
            }
        }
    },
    removeTournamentView : function(tournamentId) {
        for(var i = 0; i<this.views.length; i++) {
            var v = this.views[i];
            if(v instanceof Poker.TournamentView && v.getTournamentId()==tournamentId) {
                var pv = this.getPreviousView();
                v.close();
                this.views.splice(i,1);
                this.activeView = null;
                this.activateView(pv);
            }
        }
    },
    /**
     * Activates a Poker.TableView by it's table id
     * @param tableId
     */
    activateViewByTableId : function(tableId) {
        var v = this.findViewByTableId(tableId);
        if(v!=null) {
            this.activateView(v);
        }
    },
    activateViewByTournamentId : function(tournamentId) {
        var v = this.findViewByTournamentId(tournamentId);
        if(v!=null) {
            this.activateView(v);
        }
    },
    findViewByTournamentId : function(tournamentId) {
        for(var i = 0; i<this.views.length; i++) {
            var v = this.views[i];
            if(v instanceof Poker.TournamentView && v.getTournamentId()==tournamentId) {
                return v;
            }
        }
    },
    /**
     * Find a TableView by it's table id
     * @param tableId
     * @return {Poker.TableView}
     */
    findViewByTableId : function(tableId) {
        for(var i = 0; i<this.views.length; i++) {
            var v = this.views[i];
            if(typeof(v.getTableId)!="undefined" && v.getTableId()==tableId) {
                return v;
            }
        }

    },
    /**
     * Adds a Poker.TableView and activates it
     * @param tableLayoutManager - the layout manager to handle the UI for this view
     * @param name - name of the view, to be displayed on the tab
     */
    addTableView : function(tableLayoutManager,name) {
      var view = this.addView(new Poker.TableView(tableLayoutManager,name));
      view.fixedSizeView = true;
      this.activateView(view);
      this.setViewDimensions();
    },
    addTournamentView : function(viewElementId,name,layoutManager) {
        var view = this.addView(new Poker.TournamentView(viewElementId,name,layoutManager));
        this.activateView(view);
    },
    /**
     * Sets the dimensions of all views that are set to fixedSizedViews
     * and updates the body's font-size. Usually called on resize window event
     */
    setViewDimensions : function(){
        if(window.matchMedia) {
            var mq1 = window.matchMedia("(max-width:700px)")
            var mq2 = window.matchMedia("(max-height: 400px)");
            if(mq1.matches || mq2.matches) {
                this.mobileDevice = true;
            } else {
                this.mobileDevice = false;
            }
        } else {
            this.mobileDevice = false;
        }

        var w = $(window);

        //iphone - statusbar = 8/5
        var maxAspectRatio = 4/3;
        if(this.mobileDevice) {
            maxAspectRatio = 8/5;
        }
        var views = this.getFixedSizedViews();

        if(w.width()/ w.height() > maxAspectRatio) {
            var width = w.height() * maxAspectRatio;
            for(var i = 0; i<views.length; i++) {
                views[i].viewElement.css({
                    width: Math.round(width) +  "px",
                    height : Math.round(w.height()-40)+"px",
                    marginLeft :Math.round((w.width()-width)/2)  + "px"
                });
            }

            var targetFontSize =  Math.round(90* width / this.baseWidth);
            if(targetFontSize>130) {
                targetFontSize=130;
            }
            $("body").css({fontSize : targetFontSize+"%"});
        } else {

            for(var i = 0; i<views.length; i++) {
                views[i].viewElement.css({width:"100%", height: (w.height()-40)+"px", marginLeft : 0+"px"});
            }

            var targetFontSize =  Math.round(90 * w.width() / this.baseWidth);
            if(targetFontSize>130) {
                targetFontSize=130;
            }
            $("body").css({fontSize : targetFontSize+"%"});
        }
    },
    /**
     * Retrieves a array of the views currently available that
     * are fixed size views
     * @return {Array}
     */
    getFixedSizedViews : function() {
        var views = [];
        for(var x in this.views) {
            if(this.views[x].fixedSizeView==true){
                views.push(this.views[x]);
            }
        }
        return views;
    },
    /**
     * Activate a view.
     * Will:
     *  * Highlight the views tab.
     *  * Hide the previously active view
     *  * Set up the swipe-to-change-tab elements
     * @param view
     */
    activateView : function(view) {
        if(this.activeView!=null) {
            this.activeView.deactivate();
        }
        this.activeView = view;
        view.activate();

        if(view.fixedSizeView==true){
            $(".view-port").scrollTop(0).css("overflow-y","hidden");
        } else {
            $(".view-port").css("overflow-y","");
        }
        if(this.swiper!=null) {
            this.swiper.setElements(
                this.getPreviousView(),
                this.getActiveView(),
                this.getNextView()
            );
        }

    },
    /**
     * Retrieves the current active view
     * @return {Poker.View}
     */
    getActiveView : function() {
        return this.activeView;
    },
    /**
     * Adds and activates a view
     * @param view
     * @return {Poker.View}
     */
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
        this.tabsContainer.find("li").css({width : (100/count)-2 + "%" });

        return view;
    },
    /**
     * Get the the nr of views that has a visible tab
     * @return {Number}
     */
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
