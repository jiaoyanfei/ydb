var express = require('express');
var router = express.Router();
var com = require('./com');
/* GET users listing. */
router.post('/', function(req, res, next) {

	

	if(com.isLogined(req.session) )
	{
		
		var LoginUserName = req.session.LoginUserName;
		var Name = req.session.Name;
		var Id = req['body']['Id'];
		var selectSQL ="select * from all_orders where Id =";
			selectSQL += Id;
			selectSQL += " and CustomerId = ";
			selectSQL += req.session.Id;
			

		com.executeSQL(selectSQL, function(err, rows) {
		    if (!err)
		    {
		    	if(rows.length != 0)
		    	{
		    		var selectSQL1 ="delete from all_orders where Id =";
					selectSQL1 += 1234546789;
					// selectSQL1 += Id;
					selectSQL1 += " and CustomerId = ";
					selectSQL1 += req.session.Id;
			    	com.executeSQL(selectSQL1, function(err1, rows1) {
					    if (!err1)
					    {
					    	res.redirect('orderByStyle');

					    	dbAdd(req);
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
		    		dbAdd(req);
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


function dbAdd(req)
{
	var Id = req['body']['Id'];
	var userId = req.session.Id;
	var reqOrder = req['body'];
	com.executeSQL("select * from products where Id = "+Id,function(err,row){
		var order = [];
		var dbOrder = [];
		var size = row[0]['size'].split(',');
		var color = row[0]['color'].split(',');
		for(var i = 0;i<color.length;i++)
		{
			order.push({color:color[i],size:[]});
			dbOrder.push({color:color[i],size:"",UserOrderNumber:0});
			for(var j = 0 ; j<size.length;j++)
			{

				order[i].size.push({size:size[j],value:reqOrder['color'+i+'size'+j]});
				dbOrder[i].size += size[j]+":";
				dbOrder[i].size += reqOrder['color'+i+'size'+j];
				dbOrder.UserOrderNumber += reqOrder['color'+i+'size'+j];
				if(j!= size.length-1)
					dbOrder[i].size += ",";
			}
		}
		
		for(var i = 0;i<color.length;i++)
		{
			
			
			console.log(order[i]);
		}

		console.log(dbOrder);
		var insertSQL = 'insert into all_orders values(';

		insertSQL += '';
		insertSQL += userId;
		insertSQL += ',';

		insertSQL += '';
		insertSQL += Id;
		insertSQL += ',';

		insertSQL += '"';
		insertSQL += row[0]['ProductType'];
		insertSQL += '",';

		insertSQL += '"';
		insertSQL += row[0]['ProductSerial'];
		insertSQL += '",';

		insertSQL += '"';
		insertSQL += row[0]['ProductClass'];
		insertSQL += '",';

		insertSQL += '"';
		insertSQL += row[0]['ProductName'];
		insertSQL += '",';

		
		insertSQL += '"';
		insertSQL += row[0]['ProductPrice'];
		insertSQL += '",';

		
		insertSQL += ')';
		com.executeSQL(insertSQL, function(err, rows) {
			res.redirect('detail?Id='+req['body']['Id']);
		});
	});

}
module.exports = router;

