var express = require('express');
var router = express.Router();

/* GET home page. */
router.get('/', function(req, res, next) {
	res.render('index', {
		title: '易订宝',
		LoginUserName: '',
		inputClass:'inputPassword',
		passwordState:'请输入密码'
		});
});




module.exports = router;
