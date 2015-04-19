var express = require('express');
var router = express.Router();
var com = require('./com');
/* GET users listing. */
router.get('/', function(req, res, next) {
	req.session.curPage = "universalAnalysis";
	var budgetType = req['query']['budgetType'];
	var LoginUserName = req.session.LoginUserName;
	var Name = req.session.Name;

	if(com.isLogined(req.session) )
	{
		
		var selectSQL = "select * from user_info where Id = "+req.session.Id;
		com.executeSQL(selectSQL,function(err,rows){
				com.executeSQL("select * from all_orders where Invalid = 0 and  CustomerId = "+req.session.Id,function(err2,rows2){
					// console.log(rows1);
					res.render('universalAnalysis',{
						Name:Name,
						budgetType:budgetType,
						userInfo:rows,
						
						orders:rows2,
						curPage:req.session.curPage
						
					});
				});

			
			
		});
		
	}
	else
	{
		res.redirect('/');

	}
	
	
});

module.exports = router;
