var express = require('express');
var router = express.Router();
var com = require('./com');
/* GET users listing. */
router.get('/', function(req, res, next) {
	req.session.curPage = "goldAgent";

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
		    	
		    	
		    	// console.log(rows);
		    	res.render('goldAgent',{
					Name:req.session.userInfo.Name,
					data:rows,
					curPage:req.session.curPage
					
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
