var express = require('express');
var router = express.Router();
var com = require('./com');
/* GET users listing. */
router.get('/', function(req, res, next) {
	if(com.isLogined(req.session) )
	{
	
		var userId = req.session.userInfo.Id;
		var Id = req['query']['Id'];
		var nextId = req['query']['nextId'];
		var selectSQL ="select CollectionSet from user_info where Id =";
			selectSQL += userId;
			
		com.executeSQL(selectSQL, function(err, rows) {
		    if (err)
		    {
		    	console.log(err);
		    }
		    else
		    {
		    	var collection = rows[0]['CollectionSet'].split(',');
		    	var strCollection = "";
		    	for(var i = 0;i<collection.length;i++)
		    	{
		    		if(collection[i]!=Id)
		    		{
			    		if(strCollection.length == 0)
			    		{
			    			strCollection += collection[i];
			    		}
			    		else
			    		{
			    			strCollection += ",";
			    			strCollection += collection[i];
			    		}
			    	}
		    	}
		    	var updateSQL ="update user_info set CollectionSet= '";
		    	
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
				    	req.session.userInfo.CollectionSet = strCollection;
				    	if(req.session.curPage == "collectionSet")
				    	{
				    		if(nextId != "orderByStyle")
					    		res.redirect('detail?Id='+nextId);
					    	else
					    	{
					    		res.redirect('orderByStyle');
					    	}
				    	}
				    	else
				    	{
				    		res.redirect('detail?Id='+Id);
				    	}
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
