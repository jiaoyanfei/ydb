var express = require('express');
var router = express.Router();
var com = require('./com');
/* GET home page. */
router.get('/', function(req, res, next) {
	
	if(com.isLogined(req.session) )
	{
		console.log(req.query);
		res.writeHead(200, {"Content-Type": "application/octet-stream"});
		
		com.executeSQL("select Id , OrderNumber , Pictures from products ",function(err1,rows1){
			for(var i = 0;i<rows1.length;i++)
			{
				var tempR = rows1[i];
				for(j = i+1;j<rows1.length;j++)
				{
					if(rows1[j]['OrderNumber'] > rows1[i]['OrderNumber'])
					{
						tempR = rows1[j];
						rows1[j] = rows1[i];
						rows1[i] = tempR;
					}
				}
			}
			rows1.length = 20;
			
			com.executeSQL("select ProductId , UserOrderNumber from all_orders where CustomerId = "+req.session.userInfo.Id,function(err2,rows2){

				for(var i = 0;i<rows1.length;i++)
				{
					rows1[i].UserOrderNumber = 0;
					for(var j = 0;j<rows2.length;j++)
					{
						if(rows1[i]['Id'] == rows2[j]['ProductId'])
						{
							rows1[i].UserOrderNumber += rows2[j]['UserOrderNumber'];
						}
					}
				}
				
				var FinishRate = (req.session.userInfo['OrderAmount']/(req.session.userInfo['BudgetAmount']*10000)*100).toFixed(2);
				res.end(JSON.stringify({OrderStyleNumber:req.session.userInfo['OrderStyleNumber'],
					OrderAmount:req.session.userInfo['OrderAmount'],
					BudgetAmount:req.session.userInfo['BudgetAmount'],
					FinishRate:FinishRate,
					TopSell:rows1
				}));
			});
		});
		
	}
	else
	{
		res.redirect('/');
	}

});




module.exports = router;
