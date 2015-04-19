var express = require('express');
var router = express.Router();
var com = require('./com');
/* GET users listing. */
router.get('/', function(req, res, next) {
	req.session.curPage = "myOrder";
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

		    		com.executeSQL("select max(OrderStyleNumber) as maxOrderStyleNumber,max(OrderProductNumber) as maxOrderProductNumber,max(OrderAmount) as maxOrderAmount from user_info",function(err2,rows2){
		    			com.executeSQL("select count(distinct ProductId,Color) as SKU from all_orders where Invalid = 0",function(err3,rows3){
		    				com.executeSQL("select count(distinct ProductId,Color) as MSKU from all_orders where Invalid = 0 and CustomerId = "+req.session.Id,function(err4,rows4){
		    					com.executeSQL("select count(*) as allType from products",function(err5,rows5){
		    						
		    						res.render('myOrder',{
										Name:Name,
										userInfo:rows,
										orders:rows1,
										maxUserInfo:rows2,
										SKU:rows3,
										MSKU:rows4,
										curPage:req.session.curPage,
										finishRate:req.session.FinishRate,
										allType:rows5[0]['allType']
									});
		    					});

		    				});
		    				
		    			});
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
