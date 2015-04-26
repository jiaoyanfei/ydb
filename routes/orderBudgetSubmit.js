var express = require('express');
var router = express.Router();
var com = require('./com');
/* GET users listing. */
router.post('/', function(req, res, next) {
	req.session.curPage = "orderBudget";
	var budgetType = req['body']['budgetType'];

	if(com.isLogined(req.session) )
	{
		console.log(req['body']);
		res.send("ok");
		
	}
	else
	{
		res.redirect('/');

	}
	
	
});

module.exports = router;
