var express = require('express');
var router = express.Router();
var com = require('./com');
/* GET users listing. */
router.post('/', function(req, res, next) {
	if(com.isLogined(req.session) )
	{
		console.log(req.cookies);
		var Id = req['body']['Id'];
		var selectSQL ="select * from all_orders where ProductId =";
			selectSQL += Id;
			selectSQL += " and CustomerId = ";
			selectSQL += req.session.userInfo.Id;
			

		com.executeSQL(selectSQL, function(err, rows) {
		    if (!err)
		    {
		    	if(rows.length != 0)
		    	{
		    		var selectSQL1 ="delete from all_orders where ProductId =";
					
					selectSQL1 += Id;
					selectSQL1 += " and CustomerId = ";
					selectSQL1 += req.session.userInfo.Id;
			    	com.executeSQL(selectSQL1, function(err1, rows1) {
					    if (!err1)
					    {

					    	dbAdd(req,res);
					    }
					    else
					    {
					    	console.log(err1);
					    	res.redirect('orderByStyle');
					    }
			    
					});
		    	}
		    	else
		    	{
		    		dbAdd(req,res);
		    	}
		    	
		    }
		    else
		    {
		    	console.log(err);
		    	res.redirect('orderByStyle');
			    
		  	}
		    
		});
	}
	else
	{
		

		res.redirect('/');

	}
	
	
});


function dbAdd(req,res)
{
	var Id = req['body']['Id'];
	var userId = req.session.userInfo.Id;
	var reqOrder = req['body'];
	com.executeSQL("select * from products where Id = "+Id,function(err,rows){
		var discount = 10;
		if(req.session.userInfo.DiscountVisible[0] == 1)
		{
			var disStr = req.session.userInfo.Discount.split(',');
			for(var i = 0; i< disStr.length;i++)
			{
				disStr[i] = disStr[i].split(':');
				if(disStr[i][0] == rows[0]['ProductClass'])
				{
					discount = disStr[i][1];
					break;
				}
				else
				{
				
					if(disStr[i][0] == "通常")
						discount = disStr[i][1];
										
				}
			}
		}
		var price = rows[0]['Price'];
		var order = [];
		var dbOrder = [];
		var size = rows[0]['size'].split(',');
		var color = rows[0]['color'].split(',');
		var AllColorsOrderNumber = 0;
		var AllColorsOrderAmount = 0;
		for(var i = 0;i<color.length;i++)
		{
			order.push({color:color[i],size:[]});
			dbOrder.push({color:color[i],size:"",UserOrderNumber:0,UserOrderAmount:0});
			for(var j = 0 ; j<size.length;j++)
			{

				order[i].size.push({size:size[j],value:reqOrder['color'+i+'size'+j]});
				dbOrder[i].size += size[j]+":";
				dbOrder[i].size += reqOrder['color'+i+'size'+j];
				dbOrder[i].UserOrderNumber += Number(reqOrder['color'+i+'size'+j]);
				if(j!= size.length-1)
					dbOrder[i].size += ",";
			}
			dbOrder[i].UserOrderAmount = dbOrder[i].UserOrderNumber * price * discount / 10;
			AllColorsOrderNumber += dbOrder[i].UserOrderNumber;
			AllColorsOrderAmount += dbOrder[i].UserOrderAmount;
		}
		
		for(var i = 0;i<color.length;i++)
		{
			
			
			console.log(order[i]);
		}

		console.log(dbOrder);
		var insertSQL = 'insert into all_orders';
		insertSQL += '(CustomerId,'
		insertSQL += 'ProductId,'
		insertSQL += 'ProductType,'
		insertSQL += 'ProductSerial,'
		insertSQL += 'ProductClass,'
		insertSQL += 'ProductName,'
		insertSQL += 'ProductPrice,'
		insertSQL += 'UserOrderNumber,'
		insertSQL += 'UserOrderAmount,'
		insertSQL += 'UserOrderDetails,'
		insertSQL += 'ProductCode,'
		
		insertSQL += 'Mucai,'
		insertSQL += 'PriceZone,'
		insertSQL += 'Invalid,'
		insertSQL += 'AreaId,'
		insertSQL += 'AreaName,'
		insertSQL += 'InvalidPd,'
		insertSQL += 'SuperAreaId,'
		insertSQL += 'Color,'
		insertSQL += 'SKC,'
		insertSQL += 'ProductStyle,'
		insertSQL += 'Brand,'
		insertSQL += 'AllColorsOrderNumber,'
		insertSQL += 'InvalidColor,'
		insertSQL += 'LeastPrice,'
		insertSQL += 'LeastNumberForLeastPrice)';
		insertSQL += ' values';
		for(var i = 0;i<dbOrder.length;i++)
		{
			insertSQL += '(';

			
			insertSQL += userId;
			insertSQL += ',';

			insertSQL += '';
			insertSQL += Id;
			insertSQL += ',';

			insertSQL += '"';
			insertSQL += rows[0]['ProductType'];
			insertSQL += '",';

			insertSQL += '"';
			insertSQL += rows[0]['ProductSerial'];
			insertSQL += '",';

			insertSQL += '"';
			insertSQL += rows[0]['ProductClass'];
			insertSQL += '",';

			insertSQL += '"';
			insertSQL += rows[0]['ProductName'];
			insertSQL += '",';

			insertSQL += '"';
			insertSQL += rows[0]['Price'];
			insertSQL += '",';

			insertSQL += '"';
			insertSQL += dbOrder[i]['UserOrderNumber'];
			insertSQL += '",';

			insertSQL += '"';
			insertSQL += dbOrder[i]['UserOrderAmount'];
			insertSQL += '",';

			insertSQL += '"';
			insertSQL += dbOrder[i]['size'];
			insertSQL += '",';

			insertSQL += '"';
			insertSQL += rows[0]['ProductCode'];
			insertSQL += '",';

			insertSQL += '"';
			insertSQL += rows[0]['Mucai'];
			insertSQL += '",';

			insertSQL += '"';
			insertSQL += rows[0]['PriceZone'];
			insertSQL += '",';

			
			insertSQL += '0';
			insertSQL += ',';

			insertSQL += req.session.userInfo.AreaId;
			insertSQL += ',';

			insertSQL += '"';
			insertSQL += req.session.userInfo.AreaName;
			insertSQL += '",';

			insertSQL += rows[0]['Deleted'][0];
			insertSQL += ',';

			insertSQL += '';
			insertSQL += req.session.userInfo['SuperAreaId'];
			insertSQL += ',';

			insertSQL += '"';
			insertSQL += dbOrder[i]['color'];
			insertSQL += '",';

			insertSQL += dbOrder.length;
			insertSQL += ',';

			insertSQL += '"';
			insertSQL += rows[0]['ProductStyle'];
			insertSQL += '",';

			insertSQL += '"';
			insertSQL += rows[0]['Brand'];
			insertSQL += '",';

			insertSQL += AllColorsOrderNumber;
			insertSQL += ',';

			insertSQL += '0';
			insertSQL += ',';

			insertSQL += '"';
			insertSQL += rows[0]['LeastPrice'];
			insertSQL += '",';

			insertSQL += '"';
			insertSQL += rows[0]['LeastNumberForLeastPrice'];
			insertSQL += '"';

			insertSQL += ')';
			if(i!= dbOrder.length-1)
				insertSQL += ',';
		}
		com.executeSQL(insertSQL, function(err, rows) {
			com.executeSQL('update products set OrderNumber = OrderNumber+'+AllColorsOrderNumber+',OrderAmount = OrderAmount+'+AllColorsOrderAmount,
				function(err1,rows1){
				com.executeSQL('update user_info set OrderAmount = OrderAmount+'+AllColorsOrderAmount+',OrderStyleNumber = OrderStyleNumber+1 ,OrderProductNumber = OrderProductNumber+'+AllColorsOrderNumber,function(err2,rows2){
					req.session.userInfo.OrderAmount += AllColorsOrderAmount;
					req.session.userInfo.OrderStyleNumber += 1;
					req.session.userInfo.OrderProductNumber += AllColorsOrderNumber;
					if(req.session.curPage != 'deleted')
					{
						res.redirect('detail?Id='+req['body']['Id']);
					}
					else
					{
						res.redirect('deleted');
					}
				});
			});
		});
	});

}
module.exports = router;

