var express = require('express');
var router = express.Router();
var com = require('./com');
/* GET home page. */
router.get('/', function(req, res, next) {
	req.session.curPage = "collectionSet";
	
	var peername = req['client']['_peername'];
	var ip = peername['address'];
	var reqPage = req['query']['page'];


	var reqBrand = req['query']['Brand'];
	var reqProductType = req['query']['ProductType'];
	var reqMucai = req['query']['Mucai'];
	var reqProductClass = req['query']['ProductClass'];
	var reqProductSerial = req['query']['ProductSerial'];
	var reqOrderAmount = req['query']['OrderAmount'];
	var reqOrderNumber = req['query']['OrderNumber'];
	var reqProductCode = req['query']['ProductCode'];

	if(reqPage == undefined || reqPage == "")
		reqPage = 1;
	
	console.log(peername);
	
	
	var LoginUserName = req.session.LoginUserName;
	var Name = req.session.Name;

	if(com.isLogined(req.session) )
	{
		
		var flag = 0;
		// var selectSQL = "select * from products where CustomerId = ";
		// selectSQL += req.session.Id;
		var selectSQL = "select * from products where  ";
		
		if(reqBrand != undefined && reqBrand != "全部品牌")
		{
			if(flag > 0)
				selectSQL += " and ";
			selectSQL += " Brand='";
			selectSQL += reqBrand;
			selectSQL += "' ";
			flag ++;
		}
		if(reqProductType != undefined && reqProductType != "全部款别")
		{
			if(flag > 0)
				selectSQL += " and ";
			selectSQL += " ProductType='";
			selectSQL += reqProductType;
			selectSQL += "' ";
			flag ++;
		}
		if(reqMucai != undefined && reqMucai != "全部波段")
		{
			if(flag > 0)
				selectSQL += " and ";
			selectSQL += " Mucai='";
			selectSQL += reqMucai;
			selectSQL += "' ";
			flag ++;
		}
		if(reqProductClass != undefined && reqProductClass != "全部品类")
		{
			if(flag > 0)
				selectSQL += " and ";
			selectSQL += " ProductClass='";
			selectSQL += reqProductClass;
			selectSQL += "' ";
			flag ++;
		}
		if(reqProductSerial != undefined && reqProductSerial != "全部主题")
		{
			if(flag > 0)
				selectSQL += " and ";
			selectSQL += " ProductSerial='";
			selectSQL += reqProductSerial;
			selectSQL += "' ";
			flag ++;
		}
		if(reqProductCode != undefined && reqProductCode != "")
		{
			if(flag > 0)
				selectSQL += " and ";
			selectSQL += " ProductCode like '%";
    		selectSQL += reqProductCode;
    		selectSQL += "%'";
		}

		var ids = req.session.CollectionSet.split(',');
    	if(ids.length != 0)
    	{
    		if(flag > 0)
			{
				selectSQL += " and  ";
			}
			
			if(flag > 0)
    			selectSQL += " ( ";
    		for(var i = 0;i<ids.length;i++)
    		{
    			if(i > 0)
    			{
    				selectSQL += " or ";
    			}
    			
    			selectSQL += " Id =  ";
				selectSQL += ids[i];
    		}
    		if(flag > 0)
    			selectSQL += " ) ";
    	}
		var flagOrder = 0;
		if(reqOrderAmount != undefined && reqOrderAmount == "定量up")
		{
			selectSQL += " order by ProductId asc , UserOrderAmount asc";
			flagOrder ++;
		}
		else if(reqOrderAmount != undefined && reqOrderAmount == "定量down")
		{
			selectSQL += " order by ProductId asc , UserOrderAmount desc";
			flagOrder ++;
		}
		if(reqOrderNumber != undefined && reqOrderNumber == "编号up")
		{
			if(flagOrder > 0)
			{
				selectSQL += " , UserOrderNumber asc";
			}
			else
			{
				selectSQL += " order by ProductId asc , UserOrderNumber asc";
			}
			
		}
		else if(reqOrderNumber != undefined && reqOrderNumber == "编号down")
		{
			if(flagOrder > 0)
			{
				selectSQL += " , UserOrderNumber desc";
			}
			else
			{
				selectSQL += " order by ProductId asc , UserOrderNumber desc";
			}
			
		}
		
		if(reqBrand == undefined)
		{
			reqBrand = "全部品牌";
			reqProductType = "全部款别";
			reqMucai = "全部波段";
			reqProductClass = "全部品类";
			reqProductSerial = "全部主题";
			reqOrderAmount = "";
			reqOrderNumber = "";
			reqProductCode = "";
		}
		
		
		
    	if(req.session.CollectionSet != null && req.session.CollectionSet != "")		    	
    	{
    		

	    	com.executeSQL(selectSQL,function(err,rows){
	    		
	    		var selectSQL2 = "select * from all_orders where Invalid=0 and CustomerId="+req.session.Id;
	    		com.executeSQL(selectSQL2,function(err2,rows2){
					res.render("productList",{
						ordered:rows2,
						Name:Name,
						data:rows,
						pageLength:52,
						page:reqPage,
						curPage:req.session.curPage,
						reqBrand:reqBrand,
						reqProductType:reqProductType,
						reqMucai:reqMucai,
						reqProductClass:reqProductClass,
						reqProductSerial:reqProductSerial,
						reqOrderAmount:reqOrderAmount,
						reqOrderNumber:reqOrderNumber,
						reqProductCode:reqProductCode
					});
				});
	    	});
    	}
    	else
    	{
	    	
	    	res.render("productList",{
				ordered:[],
				Name:Name,
				data:[],
				pageLength:52,
				page:reqPage,
				curPage:req.session.curPage,
				reqBrand:reqBrand,
				reqProductType:reqProductType,
				reqMucai:reqMucai,
				reqProductClass:reqProductClass,
				reqProductSerial:reqProductSerial,
				reqOrderAmount:reqOrderAmount,
				reqOrderNumber:reqOrderNumber,
				reqProductCode:reqProductCode
			});
			
    	}
				
		    
	    
		
	}
	else
	{
		
		res.redirect('/');
	}
});

module.exports = router;
