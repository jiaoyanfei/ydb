var express = require('express');
var router = express.Router();
var com = require('./com');
/* GET users listing. */
router.get('/', function(req, res, next) {
	req.session.curPage = "universalAnalysis";
	var productCode = req['query']['ProductCode'];
	var LoginUserName = req.session.LoginUserName;
	var Name = req.session.Name;

	if(com.isLogined(req.session) )
	{
		var selectSQL ='select * from user_info where Id ='+req.session.Id;
		
		
		com.executeSQL(selectSQL, function(err, rows) {
		    if (err)
		    {
		    	console.log(err);
		    }
		    else
		    {
		    	
		    	selectSQL1 = "select * from all_orders where Invalid = 0 and CustomerId = "+req.session.Id;
		    	com.executeSQL(selectSQL1,function(err1,rows1){
		    		res.render('universalAnalysis',{
						Name:Name,
						userInfo:rows,
						orders:rows1,
						curPage:req.session.curPage
					});
		    	});
		    }
		    
		});
	}
	else
	{
		

		res.redirect('/');

	}
	
	
});

module.exports = router;
