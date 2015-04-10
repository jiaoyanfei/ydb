exports.executeSQL =  function(SQL,callback){
	console.log(SQL);
	if(callback != undefined)
    {
    	var mysql = require('mysql');
		var conn = mysql.createConnection({
		    host: 'localhost',
		    user: 'root',
		    password: '123456',
		    database:'yidingbao_total_one_brand',
		    port: 3306
		});
    	conn.connect();
		conn.query(SQL, callback);
		conn.end();
    	
    }
    else
    {
    	console.log("executeSQL 'callback' function not set!");
    }
	

};

exports.isLogined = function(session){


	// for debug ,delete when release
	return true;
	if(session.LoginUserName == undefined)
	{
		return false;
	}
	return true;
}
