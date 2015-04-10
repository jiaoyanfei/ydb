var express = require('express');
var router = express.Router();
var com = require('./com');
/* GET users listing. */
router.post('/', function(req, res, next) {

	

	if(com.isLogined(req.session) )
	{
		console.log(req.query);
		var LoginUserName = req.session.LoginUserName;
		var Name = req.session.Name;
		var Id = req['body']['Id'];
		var selectSQL ="select * from products where Id ='";
			selectSQL += Id;
			selectSQL += "'";
			

		com.executeSQL(selectSQL, function(err, rows) {
		    if (err)
		    {
		    	console.log(err);
		    }
		    else
		    {
		    	
			    var selectSQL1 ="select * from products where Id ='";
				selectSQL1 += Id;
				selectSQL1 += "'";
		    	com.executeSQL(selectSQL1, function(err1, rows1) {
				    if (err)
				    {
				    	console.log(err);
				    }
				    else
				    {
				    	
				    	
				    	res.redirect('detail?Id='+Id);
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
