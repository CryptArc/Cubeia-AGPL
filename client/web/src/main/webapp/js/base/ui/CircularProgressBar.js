var CircularProgressBar = function(containerId,animationManager) {
	this._initialize(containerId,animationManager);
};
CircularProgressBar.prototype = {
	containerId : null,
	secondPartCreated : false,
    slice : null,
    fill : null,
    pie : null,
    pieElement : null,
    animationManager : null,
	_initialize : function(containerId,animationManager) {
		if (containerId == null) {
			throw "CircularProgressBar: containerId must be set";
		}
		if (containerId.indexOf("#") != 0) {
			containerId = "#" + containerId;
		}
		var c = $(containerId);
		this.containerId = containerId;
		this._addContent();
        this.animationManager = animationManager;
	},
	show : function() {
		$(this.containerId).show();
	},
	hide : function() {
        this.running=false;
		$(this.containerId).hide();
        $(this.containerId).empty();
        this._addContent();
	},
	_addContent : function() {
		var progressBarHTML = ''
            + '<div class="cpb-timer cpb-animated">'
                + '<div class="cpb-slice">'
                    + '<div class="cpb-pie"></div>'
				    + '<div class="cpb-pie cpb-fill" style="display:none;"></div>'
				+ '</div>'
            + '</div>';

		var backgroundHTML = '<div class="cpb-timer cpb-background">'
				    + '<div class="cpb-slice cpb-gt50">'
				        + '<div class="cpb-pie"></div>'
				        + '<div class="cpb-pie cpb-fill"></div>'
                    + '</div>'
                + '</div>';

        $(this.containerId).append(backgroundHTML).append(progressBarHTML);
        this.slice = $(".cpb-animated .cpb-slice", this.containerId);
        this.fill = $(".cpb-animated .cpb-fill", this.containerId);
        this.pie = $('.cpb-animated .cpb-pie', this.containerId);
        this.pieElement = this.pie.get(0);
	},
    animation : null,
    startTime : null,
    animationTime : null,
    endTime : null,
    running : false,
	render : function(time) {

        var self = this;

        var anim = new Poker.TransformAnimation(this.pieElement);
        anim.addTransition("transform",(time/2000),"linear")
            .addTransform("rotate(180deg)").addCallback(function(){
                self.slice.addClass("cpb-gt50");
                self.fill.show();
            }).next().addTransform("rotate(360deg)");
        anim.start(this.animationManager);

        return;


    },
    detach : function() {
        $(this.containerId).empty();
    }
};