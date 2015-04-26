var express = require('express');
var router = express.Router();
var com = require('./com');
router.post('/',function (req,res){

	var LoginUserName = req.body['LoginUserName'];
	var password = req.body['password'];
	var peername = req['client']['_peername'];

	
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
	    			req.session.userInfo = rows[i];
	    			
	    			flag = 1;
	    			break;
	    		}
	    	}
	    	
			if(flag == 1 && password == req.session.userInfo['Password'])
			{
				
				
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
