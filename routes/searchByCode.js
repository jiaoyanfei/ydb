var express = require('express');
var router = express.Router();
var com = require('./com');
/* GET home page. */
router.get('/', function(req, res, next) {
	req.session.curPage = "orderByStyle";
	var peername = req['client']['_peername'];
	var ip = peername['address'];
	var reqPage = req['query']['page'];

	var reqBrand = req['query']['Brand'];
	var reqProductType = req['query']['ProductType'];
	var reqMucai = req['query']['Mucai'];
	var reqProductClass = req['query']['ProductClass'];
	var reqProductSerial = req['query']['ProductSerial'];
	var reqOrderAmount = req['query']['OrderAmount'];
	var reqOrderNumber = req['query']['OrderNumber'];
	var reqProductCode = req['query']['ProductCode'];
	
	if(reqBrand == undefined)
	{
		reqBrand = "全部品牌";
		reqProductType = "全部款别";
		reqMucai = "全部波段";
		reqProductClass = "全部品类";
		reqProductSerial = "全部主题";
		reqOrderAmount = "";
		reqOrderNumber = "";
		
	}
	if(reqPage == undefined)
		reqPage = 1;
	
	console.log(peername);
	
	if(com.isLogined(req.session) )
	{
		var flag = 0;
		var selectSQL = "select * from products";
		
		if(reqProductCode != undefined)
    	{
    		selectSQL += " where ProductCode like '%";
    		selectSQL += reqProductCode;
    		selectSQL += "%'";
    	}
    	
		com.executeSQL(selectSQL, function(err, rows) {
		    if (err)
		    {
		    	res.redirect('err');
		    	console.log(err);
		    }
		    else
		    {
				var selectSQL1 = "select * from all_orders where Invalid=0 and CustomerId="+req.session.userInfo.Id;
		    	com.executeSQL(selectSQL1,function(err1,rows1){
		    		res.render("productList",{
		    			ordered:rows1,
						Name:req.session.userInfo.Name,
						data:rows,
						pageLength:52,
						page:reqPage,
						curPage:req.session.curPage,
						reqBrand:reqBrand,
						reqProductType:reqProductType,
						reqMucai:reqMucai,
						reqProductClass:reqProductClass,
						reqProductSerial:reqProductSerial,
						reqOrderAmount:reqOrderAmount,
						reqOrderNumber:reqOrderNumber,
						reqProductCode:reqProductCode
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
