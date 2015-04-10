var express = require('express');
var router = express.Router();
var com = require('./com');
router.post('/',function (req,res){

	var LoginUserName = req.body['LoginUserName'];
	var password = req.body['password'];
	var peername = req['client']['_peername'];

	var dbPassword = "";
	var dbName = "";
	var dbId = "";
	var dbCollectionSet = "";
	var selectSQL = 'select * from user_info';
	
	com.executeSQL(selectSQL, function(err, rows) {
	    if (err)
	    {
	    	console.log(err);
	    }
	    else
	    {
	    	var flag = 0;
	    	for(var i = 0;i<rows.length;i++)
	    	{
	    		if(LoginUserName == rows[i]['LoginUserName'])
	    		{
	    			dbPassword = String(rows[i]['Password']);
	    			dbName = String(rows[i]['Name']);
	    			dbId = String(rows[i]['Id']);
	    			dbCollectionSet = String(rows[i]['CollectionSet']);
	    			flag = 1;
	    			break;
	    		}
	    	}
	    	
			if(flag == 1 && password == dbPassword)
			{
				
				req.session.LoginUserName = LoginUserName;
				req.session.Name = dbName;
				req.session.Id = dbId;
				req.session.CollectionSet = dbCollectionSet;
				// console.log(req.session);
				res.redirect('orderByStyle?page=1');
				
			}
			else
			{
				res.render('index', {
					title: '易订宝',
					LoginUserName: LoginUserName,
					passwordState: '帐户名或密码错误',
					inputClass:"inputPasswordWrong"});
			}
	    }
	    
	});


	
	
});

module.exports = router;
