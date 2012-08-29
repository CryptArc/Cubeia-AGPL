var CircularProgressBar = function(containerId) {
	this._initialize(containerId);
};
CircularProgressBar.prototype = {
	containerId : null,
	secondPartCreated : false,
	_initialize : function(containerId) {
		if (containerId == null) {
			throw "CircularProgressBar: containerId must be set";
		}
		if (containerId.indexOf("#") != 0) {
			containerId = "#" + containerId;
		}
		var c = $(containerId);
		c.css({fontSize:c.width()+"px"});
		this.containerId = containerId;
		this._addContent();
	},
	show : function() {
		console.log("showing progressbar");
		$(this.containerId).show();
	},
	hide : function() {
		console.log("hiding progressbar");
		$(this.containerId).hide();
	},
	_addContent : function() {
		var progressBarHTML = '<div class="cpb-timer cpb-animated">'
				+ '<div class="cpb-slice">' + '<div class="cpb-pie"></div>'
				+ '<div class="cpb-pie cpb-fill" style="display:none;"></div>'
				+ '</div>' + '</div>';

		var backgroundHTML = '<div class="cpb-timer cpb-background">'
				+ '<div class="cpb-slice cpb-gt50">'
				+ '<div class="cpb-pie"></div>'
				+ '<div class="cpb-pie cpb-fill"></div>' + '</div>' + '</div>';

		$(this.containerId).append(backgroundHTML).append(progressBarHTML);

	},
	reset : function() {
		$(".cpb-animated .cpb-slice", this.containerId).removeClass("cpb-gt50");
		$(".cpb-animated .cpb-fill", this.containerId).hide();
		this.secondPartCreated = false;
		this.render(0);
	},
	render : function(percent) {
		if(percent>=100) {
			percent=100;
		}
		if (percent > 50 && !this.secondPartCreated) {
			this.secondPartCreated = true;
			$(".cpb-animated .cpb-slice", this.containerId).addClass("cpb-gt50");
			$(".cpb-animated .cpb-fill", this.containerId).show();
		}

		var deg = 360 / 100 * percent;

		$('.cpb-animated .cpb-pie', this.containerId).css({
			'-moz-transform' : 'rotate(' + deg + 'deg)',
			'-webkit-transform' : 'rotate(' + deg + 'deg)',
			'-o-transform' : 'rotate(' + deg + 'deg)',
			'transform' : 'rotate(' + deg + 'deg)'
		});
	}
};