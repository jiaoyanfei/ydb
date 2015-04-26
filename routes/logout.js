var express = require('express');
var router = express.Router();
var com = require('./com');
router.get('/',function (req,res){


	var peername = req['client']['_peername'];

	if(com.isLogined(req.session) )
	{
		
		req.session.userInfo = undefined;
		res.redirect('/');	 
	}
	else
	{
		
		res.redirect('/');
	}

});

module.exports = router;
