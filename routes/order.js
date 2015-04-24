var express = require('express');
var router = express.Router();
var com = require('./com');
/* GET users listing. */
router.post('/', function(req, res, next) {

	

	if(com.isLogined(req.session) )
	{
		console.log(req.body);
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
					selectSQL1 += Id;
					selectSQL1 += " and CustomerId = ";
					selectSQL1 += req.session.Id;
			    	com.executeSQL(selectSQL1, function(err1, rows1) {
					    if (!err1)
					    {
					    	res.redirect('orderByStyle');
					    }
					    else
					    {
					    	console.log(err1);
					    	res.redirect('orderByStyle');
					    }
			    
					});
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

module.exports = router;
