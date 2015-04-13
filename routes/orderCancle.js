var express = require('express');
var router = express.Router();
var com = require('./com');
/* GET users listing. */
router.get('/', function(req, res, next) {

	

	if(com.isLogined(req.session) )
	{
		console.log(req.query);
		var LoginUserName = req.session.LoginUserName;
		var Name = req.session.Name;
		var Id = req['query']['Id'];
		var updateSQL ="update all_orders set Invalid = 1 ";
		    updateSQL += "where CustomerId="
			updateSQL += req.session.Id;
			updateSQL += " and ProductId = ";
			updateSQL += Id;
		com.executeSQL(updateSQL, function(err) {
		    if (err)
		    {
		    	console.log(err);
		    }
		    else
		    {
		    	if(req.session.curPage == "notOrder")
		    		res.redirect('detail?Id='+Id);
		    	else
					res.redirect(req.session.curPage);
		  	}
		    
		});
	}
	else
	{
		

		res.redirect('/');

	}
	
	
});

module.exports = router;
