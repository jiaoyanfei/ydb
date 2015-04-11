var express = require('express');
var router = express.Router();
var com = require('./com');
/* GET users listing. */
var gI = 0;

router.get('/', function(req, res, next) {

	var id = req['query']['Id'];
	var curPage = req['query']['curPage'];
	var LoginUserName = req.session.LoginUserName;
	var Name = req.session.Name;

	if(curPage == "orderByStyle")
	{
		req.session.curPage = "orderByStyle";
	}
	if(com.isLogined(req.session) )
	{
		var selectSQL ='select * from products where Deleted = 0 ';
		
		switch(req.session.curPage)
		{
			case 'alreadyOrdered':
			{
				selectSQL = "select * from products where Id in ( select ProductId from all_orders where CustomerId = ";
				selectSQL += req.session.Id;
				selectSQL += " and Invalid = 0 )";
			}
			break;
			case 'notOrder':
			{
				selectSQL = "select * from products where Deleted = 0 and Id not in (select ProductId from all_orders where CustomerId = ";
				selectSQL += req.session.Id;
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
				selectSQL = "select * from products where Deleted = 0 ";
				var ids = req.session.CollectionSet.split(',');
				// console.log(ids);
		    	if(ids.length != 0)
		    	{
		    		
					selectSQL += " and ( ";
		    		
		    		for(var i = 0;i<ids.length;i++)
		    		{
		    			if(i > 0)
		    			{
		    				selectSQL += " or ";
		    			}
		    			
		    			selectSQL += " Id =  ";
						selectSQL += ids[i];
		    		}

		    		selectSQL += " ) ";
		    	}
		    	
			   
			}
			break;
			case 'deleted':
			{
				selectSQL = "select * from products where Id in ( select ProductId from all_orders where CustomerId = ";
				selectSQL += req.session.Id;
				selectSQL += " and Invalid = 1 )";
			}
			break;
			case 'detailMatch':
			{
				if(req.session.MatchProductIDs != undefined)
				{
					selectSQL = "select * from products where ";

					matchIds = req.session.MatchProductIDs;
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
		    if (err)
		    {
		    	console.log(err);
		    }
		    else
		    {
		    	// console.log(rows);
		    	//res.send(rows);
		    	
		    		com.executeSQL("select * from comments where ProductId="+id,function(err3,rows3){
		    			var selectSQL4 = "select * from all_orders where Invalid=0 and CustomerId = ";
		    			selectSQL4 += req.session.Id;
		    			selectSQL4 += " and ProductId = ";
		    			selectSQL4 += id;
						com.executeSQL(selectSQL4,function(err4,rows4){
							
							

							com.executeSQL("select * from user_info where Id = "+req.session.Id,function(err5,rows5){
								var reccomendData = [];
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
								
						    	try
						    	{
							    	var matches = String(current['MatchProductIds']).split(',');
							    	var selectSQL1 = 'select * from matches where ';
							    	for(var i = 0;i<matches.length;i++)
						    		{
						    			if(i == 0)
						    			{
						    				selectSQL1 += 'Id = ';
						    			}
						    			else
						    			{
						    				selectSQL1 += 'or Id = ';
						    			}
						    			selectSQL1 += matches[i];
						    			
						    		}
							    	
							    	com.executeSQL(selectSQL1,function(err1,rows1){
							    		
						    			gI = 0;
						    			if(rows1 != undefined && rows1 != [])
							    		for(var j = 0;j<rows1.length;j++)
							    		{
							    			reccomendData.push({match:rows1[j], matchProduct:{}});
								    		var matchIds = String(rows1[j]['MatchProductIDs']).split(',');

								    		var selectSQL2 = 'select * from products where ';
								    		for(var i = 0;i<matchIds.length;i++)
								    		{
								    			if(i == 0)
								    			{
								    				selectSQL2 += 'Id = ';
								    			}
								    			else
								    			{
								    				selectSQL2 += 'or Id = ';
								    			}
								    			selectSQL2 += matchIds[i];
								    			selectSQL2 += ' ';
								    		}
								    		
								    		
								    		com.executeSQL(selectSQL2,function(err2,rows2){
								    			// console.log(rows2);
								    			
								   				reccomendData[gI].matchProduct = rows2;
								   				// console.log(reccomendData[gI]);
								   				gI ++;
								    			if(gI == rows1.length)
								    			{
								    				// console.log("j:"+gI );
								    				// console.log("row1.length:"+rows1.length);	    				
													res.render('detail',{
														Name:Name,
														curPage:req.session.curPage,
														data:current,
														last:last,
														next:nex,
														reccomendData:reccomendData,
														comments:rows3,
														ordered:rows4,
														userInfo:rows5
													});
								    			}
								    		});
								    	}
								    	else
								    	{
								    		res.render('detail',{
												Name:Name,
												curPage:req.session.curPage,
												data:current,
												last:last,
												next:nex,
												reccomendData:reccomendData,
												comments:rows3,
												ordered:rows4,
												userInfo:rows5
											});
								    	}
							    	
						   			});
								}
						    	catch(e)
						    	{

									res.render('detail',{
										Name:Name,
										curPage:req.session.curPage,
										data:current,
										last:last,
										next:nex,
										reccomendData:reccomendData,
										comments:rows3,
										ordered:rows4,
										userInfo:rows5
									});
						    	}
			    			});
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
