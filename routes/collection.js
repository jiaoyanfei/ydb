var express = require('express');
var router = express.Router();
var com = require('./com');
/* GET users listing. */
router.get('/', function(req, res, next) {

	

	if(com.isLogined(req.session) )
	{
		
		var userId = req.session.Id;
		var Id = req['query']['Id'];
		var selectSQL ="select CollectionSet from user_info where Id =";
			selectSQL += userId;
			
			

		com.executeSQL(selectSQL, function(err, rows) {
		    if (err)
		    {
		    	console.log(err);
		    }
		    else
		    {
		    	var strCollection = "";
		    	var updateSQL ="update user_info set CollectionSet= '";
		    	if(rows[0]['CollectionSet'] != "" && rows[0]['CollectionSet'] != null)
					strCollection = rows[0]['CollectionSet']+",";
				strCollection += Id;
				updateSQL += strCollection;


				updateSQL += "' ";
				updateSQL += "where Id="
				updateSQL += userId;
				
				
		    	com.executeSQL(updateSQL, function(err1, rows1) {
				    if (err)
				    {
				    	console.log(err);
				    }
				    else
				    {
				    	
				    	req.session.CollectionSet = strCollection;
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
