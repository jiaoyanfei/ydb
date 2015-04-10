var express = require('express');
var router = express.Router();
var com = require('./com');
/* GET users listing. */
router.get('/', function(req, res, next) {

	var productCode = req['query']['ProductCode'];
	var LoginUserName = req.session.LoginUserName;
	var Name = req.session.Name;

	if(com.isLogined(req.session) )
	{
		
		res.render('orderBudget',{
					Name:Name
					
				});
	}
	else
	{
		

		res.redirect('/');

	}
	
	
});

module.exports = router;
