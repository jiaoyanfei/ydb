var express = require('express');
var router = express.Router();
var com = require('./com');
/* GET users listing. */
router.get('/', function(req, res, next) {

	

	if(com.isLogined(req.session) )
	{

		var Id = req['query']['Id'];
		var updateSQL ="update all_orders set Invalid = 1 ";
		    updateSQL += "where CustomerId="
			updateSQL += req.session.userInfo.Id;
			updateSQL += " and ProductId = ";
			updateSQL += Id;
		com.executeSQL(updateSQL, function(err) {
		    if (!err)
		    {
		    	var selectSQL = 'select UserOrderNumber,UserOrderAmount,AllColorsOrderNumber from all_orders where CustomerId=';
		    	selectSQL += req.session.userInfo.Id;
		    	selectSQL += " and ProductId = ";
				selectSQL += Id;
		    	com.executeSQL(selectSQL,function(err1,rows1){
		    		var orderNumber = 0;
		    		var orderAmount = 0;
		    		for(var i = 0 ;i< rows1.length;i++)
		    		{
		    			orderNumber += rows1[i]['UserOrderNumber'];
		    			orderAmount += rows1[i]['UserOrderAmount'];
		    		}
		    		com.executeSQL('update products set OrderNumber = OrderNumber-'+orderNumber+',OrderAmount = OrderAmount-'+orderAmount,function(err2,rows2){
		    			com.executeSQL('update user_info set OrderAmount = OrderAmount-'+orderAmount+',OrderStyleNumber = OrderStyleNumber-1 ,OrderProductNumber = OrderProductNumber-'+orderNumber,function(err3,rows3){
		    				req.session.userInfo.OrderAmount -= orderAmount;
							req.session.userInfo.OrderStyleNumber -= 1;
							req.session.userInfo.OrderProductNumber -= orderNumber;
							if(req.session.curPage != 'alreadyOrdered')
							{
								res.redirect('detail?Id='+Id);
							}
							else
							{
								res.redirect('alreadyOrdered');
							}
		    				
		    			});
		    		});
		    	});
		    	
		    }
		    else
		    {
		    	console.log(err);
		  	}
		    
		});
	}
	else
	{
		

		res.redirect('/');

	}
	
	
});

module.exports = router;
