var CircularProgressBar = function(containerId) {
	this._initialize(containerId);
};
CircularProgressBar.prototype = {
	containerId : null,
	secondPartCreated : false,
    slice : null,
    fill : null,
    pie : null,
    pieElement : null,
	_initialize : function(containerId) {
		if (containerId == null) {
			throw "CircularProgressBar: containerId must be set";
		}
		if (containerId.indexOf("#") != 0) {
			containerId = "#" + containerId;
		}
		var c = $(containerId);
		this.containerId = containerId;
		this._addContent();
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
        this.slice = $(".cpb-animated .cpb-slice", self.containerId);
        this.fill = $(".cpb-animated .cpb-fill", self.containerId);
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
        anim.start();

        return;


    },
    detach : function() {
        $(this.containerId).empty();
    }
};