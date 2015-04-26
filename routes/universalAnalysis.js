var express = require('express');
var router = express.Router();
var com = require('./com');
/* GET users listing. */
router.get('/', function(req, res, next) {
	req.session.curPage = "universalAnalysis";
	var budgetType = req['query']['budgetType'];
	if(com.isLogined(req.session) )
	{
		
		
		com.executeSQL("select * from all_orders where Invalid = 0 and  CustomerId = "+req.session.userInfo.Id,function(err2,rows2){
			// console.log(rows1);
			res.render('universalAnalysis',{
				Name:req.session.userInfo.Name,
				budgetType:budgetType,
				userInfo:req.session.userInfo,
				orders:rows2,
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
