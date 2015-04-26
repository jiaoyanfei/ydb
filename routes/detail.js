var express = require('express');
var router = express.Router();
var com = require('./com');
/* GET users listing. */
var gI = 0;

router.get('/', function(req, res, next) {

	var id = req['query']['Id'];
	var curPage = req['query']['curPage'];

	if(curPage == "orderByStyle")
	{
		req.session.curPage = "orderByStyle";
	}
	if(com.isLogined(req.session) )
	{
		var selectSQL ='select * from products ';
		
		switch(req.session.curPage)
		{
			case 'alreadyOrdered':
			{
				selectSQL = "select * from products where Id in ( select ProductId from all_orders where CustomerId = ";
				selectSQL += req.session.userInfo.Id;
				selectSQL += " and Invalid = 0 )";
			}
			break;
			case 'notOrder':
			{
				selectSQL = "select * from products where Id not in (select ProductId from all_orders where CustomerId = ";
				selectSQL += req.session.userInfo.Id;
				selectSQL += " and Invalid = 0 )";
			}
			break;
			case 'invalidPd':
			{
				selectSQL = "select * from products where Deleted = 1";
			}
			break;
			case 'collectionSet':
			{
				selectSQL = "select * from products where ";
				var ids = req.session.userInfo.CollectionSet.split(',');
				// console.log(ids);
		    	if(ids.length != 0)
		    	{
		    		
					
		    		
		    		for(var i = 0;i<ids.length;i++)
		    		{
		    			if(i > 0)
		    			{
		    				selectSQL += " or ";
		    			}
		    			
		    			selectSQL += " Id =  ";
						selectSQL += ids[i];
		    		}

		    		
		    	}
		    	
			   
			}
			break;
			case 'deleted':
			{
				selectSQL = "select * from products where Id in ( select ProductId from all_orders where CustomerId = ";
				selectSQL += req.session.userInfo.Id;
				selectSQL += " and Invalid = 1 )";
			}
			break;
			case 'detailMatch':
			{
				if(req.session.MatchProductIDs != undefined)
				{
					selectSQL = "select * from products ";

					matchIds = req.session.MatchProductIDs;
					if(matchIds.length != 0)
					{
						
						selectSQL += " where ";
						for(var i = 0;i<matchIds.length;i++)
						{
							if(i > 0)
							{
								selectSQL += " or ";
							}
							
							selectSQL += " Id =  ";
							selectSQL += matchIds[i];
						}

						
					}
				}
			}
			break;
			case 'detailDisplay':
			{
				if(req.session.ProductIds != undefined)
				{
					selectSQL = "select * from products where ";

					matchIds = req.session.ProductIds;
					if(matchIds.length != 0)
					{
						
						
						for(var i = 0;i<matchIds.length;i++)
						{
							if(i > 0)
							{
								selectSQL += " or ";
							}
							
							selectSQL += " Id =  ";
							selectSQL += matchIds[i];
						}

						
					}
				}
			}
			break;
		}
		
		com.executeSQL(selectSQL, function(err, rows) {
			
		    if (err || rows.length == 0)
		    {
		    	console.log(err);
		    	res.redirect('orderByStyle');
		    }
		    else
		    {
		    	// console.log(rows);
		    	//res.send(rows);
		    	
		    		com.executeSQL("select * from comments where ProductId="+id,function(err3,rows3){
		    			var selectSQL4 = 'select * from all_orders where Invalid =';
		    			if(req.session.curPage == 'deleted')
		    			{
		    				selectSQL4 += ' 1 ';
		    			}
		    			else
		    			{
		    				selectSQL4 += ' 0 ';
		    			}
		    			selectSQL4 += ' and CustomerId = ';
		    			selectSQL4 += req.session.userInfo.Id;
		    			selectSQL4 += ' and ProductId = ';
		    			selectSQL4 += id;
						com.executeSQL(selectSQL4,function(err4,rows4){
								var current = undefined;
								var last =undefined;
								var nex = undefined;						    	
						    	for(var i = 0; i < rows.length;i++)
						    	{
						    		
						    		// console.log("i ="+i+"    id="+id+"rows.id  "+rows[i]['Id']);
						    		if(rows[i]['Id'] == id)
						    		{
						    			current = rows[i];
						    			if(i != 0)
						    				last = rows[i-1];
						    			if(i!= rows.length-1)
						    				nex = rows[i+1];
						    			// console.log("i ="+i);
						    			// console.log(last);
						    			// console.log(current);
						    			// console.log(nex);
						    			break;
						    		}
						    		// else
						    		// {
						    		// 	current = "";

						    		// 	last = "";
						    		// 	next = "";
						    		// }
						    	}
								
						    	if(!err4)
						    	{
							    	var matches = String(current['MatchIds']).split(',');
							    	var selectSQL1 = 'select * from products where ';
							    	// console.log(current['MatchIds']);
							    	for(var i = 0;i<matches.length;i++)
						    		{
						    			if(i == 0)
						    			{
						    				selectSQL1 += 'Id = ';
						    			}
						    			else
						    			{
						    				selectSQL1 += ' or Id = ';
						    			}
						    			selectSQL1 += matches[i];
						    			
						    		}
							    	
							    	com.executeSQL(selectSQL1,function(err1,rows1){
							    		// console.log(rows1);
							    		res.render('detail',{
											Name:req.session.userInfo.Name,
											curPage:req.session.curPage,
											data:current,
											last:last,
											next:nex,
											reccomendData:rows1,
											comments:rows3,
											ordered:rows4,
											userInfo:req.session.userInfo
										});							    	
						   			});
								}
						    	else
						    	{

									
		    						res.send('The page you are trying to access does not exist!');
						    	}
						});
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
