var express = require('express');
var router = express.Router();
var com = require('./com');
/* GET home page. */
router.get('/', function(req, res, next) {
	req.session.curPage = "orderByMatch";
	// console.log( req.session);
	var peername = req['client']['_peername'];
	var ip = peername['address'];
	var reqPage = req['query']['page'];

	if(reqPage == undefined || reqPage == "")
		reqPage = 1;

	if(com.isLogined(req.session) )
	{
		
		var selectSQL = "select * from matches";
		
		
		com.executeSQL(selectSQL, function(err, rows) {
		    if (err)
		    {
		    	res.redirect('err');
		    	console.log(err);
		    }
		    else
		    {
				res.render("orderByMatch",{
					Name:req.session.userInfo.Name,
					data:rows,
					pageLength:52,
					page:reqPage,
					curPage:req.session.curPage,
					reqBrand:undefined,
					reqProductType:undefined,
					reqMucai:undefined,
					reqProductClass:undefined,
					reqProductSerial:undefined,
					reqOrderAmount:undefined,
					reqOrderNumber:undefined,
					reqProductCode:undefined
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
