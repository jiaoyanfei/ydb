var express = require('express');
var router = express.Router();
var com = require('./com');
/* GET home page. */
router.post('/', function(req, res, next) {
	
	if(com.isLogined(req.session) )
	{
		var insertSQL = 'insert into comments(ProductId , Author , Comments) values(';
		insertSQL += '"';
		insertSQL += req['body']['Id'];
		insertSQL += '",';
		insertSQL += '"';
		insertSQL += req.session.Name;
		insertSQL += '",';
		insertSQL += '"';
		insertSQL += req['body']['comments'];
		insertSQL += '"';
		insertSQL += ')';
		com.executeSQL(insertSQL, function(err, rows) {
			res.redirect('detail?Id='+req['body']['Id']);
		});

		
	}
	else
	{
		

		res.redirect('/');

	}

});




module.exports = router;
