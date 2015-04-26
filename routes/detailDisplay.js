var express = require('express');
var router = express.Router();
var com = require('./com');
/* GET home page. */
router.get('/', function(req, res, next) {
	req.session.curPage = "detailDisplay";
	// console.log( req.session);
	var peername = req['client']['_peername'];
	var ip = peername['address'];
	var reqPage = req['query']['page'];
	var id = req['query']['Id'];

	var reqBrand = req['query']['Brand'];
	var reqProductType = req['query']['ProductType'];
	var reqMucai = req['query']['Mucai'];
	var reqProductClass = req['query']['ProductClass'];
	var reqProductSerial = req['query']['ProductSerial'];
	var reqOrderAmount = req['query']['OrderAmount'];
	var reqOrderNumber = req['query']['OrderNumber'];
	var reqProductCode = req['query']['ProductCode'];

	if(reqPage == undefined || reqPage == "")
		reqPage = 1;
	
	console.log(peername);

	if(com.isLogined(req.session) )
	{
		
		var selectSQL2 ='select ProductIds from place_display where Id = '+id;
		
		com.executeSQL(selectSQL2,function(err,rows){
			if(err)
			{
				console.log(err);
			}
			else
			{

				var matchIds = rows[0]['ProductIds'].split(',');
				req.session.ProductIds = matchIds;
				var selectSQL = "select * from products where ";
				if(matchIds.length != 0)
		    	{
		    		
		    		
		    		for(var i = 0;i<matchIds.length;i++)
		    		{
		    			if(i > 0)
		    			{
		    				selectSQL += " or ";
		    			}
		    			
		    			selectSQL += " Id =  ";
						selectSQL += matchIds[i];
		    		}

		    		
		    	}
			
				if(reqBrand == undefined)
				{
					reqBrand = "全部品牌";
					reqProductType = "全部款别";
					reqMucai = "全部波段";
					reqProductClass = "全部品类";
					reqProductSerial = "全部主题";
					reqOrderAmount = "";
					reqOrderNumber = "";
					reqProductCode = "";
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
		});
	}
	else
	{
		
		res.redirect('/');
	}
});

module.exports = router;
