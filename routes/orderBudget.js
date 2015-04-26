var express = require('express');
var router = express.Router();
var com = require('./com');
/* GET users listing. */
router.get('/', function(req, res, next) {
	req.session.curPage = "orderBudget";
	var budgetType = req['query']['budgetType'];

	if(com.isLogined(req.session) )
	{
		com.executeSQL("select * from order_guide where Id = '"+req.session.userInfo['OrderGuideId']+"'",function(err1,rows1){
			// console.log(rows1);
			res.render('orderBudget',{
				Name:req.session.userInfo.Name,
				budgetType:budgetType,
				userInfo:req.session.userInfo,
				guideInfo:rows1,
				curPage:req.session.curPage
				
			});
		});
		
	}
	else
	{
		res.redirect('/');

	}
	
	
});

module.exports = router;
