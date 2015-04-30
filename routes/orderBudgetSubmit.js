var express = require('express');
var router = express.Router();
var com = require('./com');
/* GET users listing. */
router.post('/', function(req, res, next) {
	req.session.curPage = "orderBudget";
	
	if(com.isLogined(req.session) )
	{
		var budgetType = req['body']['budgetType'];
		var data = req['body']['data'];
		var BudgetAmount = req['body']['BudgetAmount'];
		var updateSQL = 'update user_info set '+budgetType +'="'+data+'",BudgetAmount='+BudgetAmount;
		updateSQL += ' where Id='+req.session.userInfo.Id;
		com.executeSQL(updateSQL,function(err){
			if(!err)
			{
				req.session.userInfo[budgetType] = data;
				req.session.userInfo['BudgetAmount'] = BudgetAmount;
				console.log(budgetType+"   "+data);
				res.send("保存成功!");
			}
			else
			{
				res.send("保存失败！");
			}
		});
		
		
	}
	else
	{
		res.redirect('/');

	}
	
	
});

module.exports = router;
