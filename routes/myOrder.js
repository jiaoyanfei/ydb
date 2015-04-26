var express = require('express');
var router = express.Router();
var com = require('./com');
/* GET users listing. */
router.get('/', function(req, res, next) {
	req.session.curPage = "myOrder";

	if(com.isLogined(req.session) )
	{
		    	
    	selectSQL1 = "select * from all_orders where Invalid = 0 and CustomerId = "+req.session.userInfo.Id;
    	com.executeSQL(selectSQL1,function(err1,rows1){

    		com.executeSQL("select max(OrderStyleNumber) as maxOrderStyleNumber,max(OrderProductNumber) as maxOrderProductNumber,max(OrderAmount) as maxOrderAmount from user_info",function(err2,rows2){
    			com.executeSQL("select count(distinct ProductId,Color) as SKU from all_orders where Invalid = 0",function(err3,rows3){
    				com.executeSQL("select count(distinct ProductId,Color) as MSKU from all_orders where Invalid = 0 and CustomerId = "+req.session.userInfo.Id,function(err4,rows4){
    					com.executeSQL("select count(*) as allType from products",function(err5,rows5){
    						var FinishRate = (req.session.userInfo['OrderAmount']/(req.session.userInfo['BudgetAmount']*10000)*100).toFixed(2);
    						res.render('myOrder',{
								Name:req.session.userInfo.Name,
								userInfo:req.session.userInfo,
								orders:rows1,
								maxUserInfo:rows2,
								SKU:rows3,
								MSKU:rows4,
								curPage:req.session.curPage,
								finishRate:FinishRate,
								allType:rows5[0]['allType']
							});
    					});

    				});
    				
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
