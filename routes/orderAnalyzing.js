var express = require('express');
var router = express.Router();
var com = require('./com');
/* GET users listing. */
router.get('/', function(req, res, next) {

	var productCode = req['query']['ProductCode'];
	var LoginUserName = req.session.LoginUserName;
	var Name = req.session.Name;

	if(com.isLogined(req.session) )
	{
		var selectSQL ='select * from products where Id >= (select Id  from products where ProductCode = "';
		selectSQL += productCode;
		selectSQL += '" )-1 order by Id asc limit 3';
		
		com.executeSQL(selectSQL, function(err, rows) {
		    if (err)
		    {
		    	console.log(err);
		    }
		    else
		    {
		    	//console.log(rows);
		    	//res.send(rows);
		    	var current;
		    	var last;
		    	var next;
		    	for(var i = 0; i < rows.length;i++)
		    	{
		    		if(rows[i]['ProductCode'] == productCode)
		    		{
		    			current = rows[i];
		    			last = rows[i-1];
		    			next = rows[i+1];
		    		}
		    	}
				var num = (Math.floor(Math.random()*10000)%26)+1;
		    		var str = String(num);
		    	//console.log(last);
		    	current['Pictures'] = "images/product"+str +".jpg";
		    	res.render('orderByStyle',{
					Name:Name,
					data:current,
					last:last,
					next:next
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
