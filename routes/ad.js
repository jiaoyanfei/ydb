var express = require('express');
var router = express.Router();
var com = require('./com');
/* GET home page. */
router.get('/', function(req, res, next) {
	
	if(com.isLogined(req.session) )
	{
		console.log(req.query);
		res.writeHead(200, {"Content-Type": "application/octet-stream"});
		res.end("I am the background。。。");
	}
	else
	{
		

		res.redirect('/');

	}

});




module.exports = router;
