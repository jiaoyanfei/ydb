var express = require('express');
var router = express.Router();
var com = require('./com');
/* GET users listing. */
router.get('/', function(req, res, next) {

	
	var LoginUserName = req.session.LoginUserName;
	var Name = req.session.Name;

	if(com.isLogined(req.session) )
	{
		var selectSQL ='select * from user_info';
		
		selectSQL += ' order by OrderAmount desc limit 5';
		
		com.executeSQL(selectSQL, function(err, rows) {
		    if (err)
		    {
		    	console.log(err);
		    }
		    else
		    {
		    	
		    	
		    	console.log(rows);
		    	res.render('goldAgent',{
					Name:Name,
					data:rows
					
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
