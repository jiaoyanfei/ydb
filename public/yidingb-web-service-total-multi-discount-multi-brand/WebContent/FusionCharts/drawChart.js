var system = require('system');
var outputImageFileName = system.args[1];
var page = require('webpage').create();
var delay = 2000;
page.open('http://localhost/yidingbaodhh_total/FusionCharts/drawChart.html', function () {
	window.setTimeout(function () {
		page.render(outputImageFileName);
		phantom.exit();
	}, delay);
	 
});