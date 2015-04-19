$.ajax({
	type:"get",
	url:"ad",
	data:{data:"I am foreground"},
	error:function(err){alert(err);},
	success:loadAd
});


function loadAd(data,status)
{
	var userInfo = JSON.parse(data);

	var TopSell = userInfo['TopSell'];
	$("#OrderStyleNumber").text(userInfo['OrderStyleNumber']);
	$("#OrderAmount").text((userInfo['OrderAmount']/10000).toFixed(4));
	$("#BudgetAmount").text(userInfo['BudgetAmount']);
	$("#FinishRate").text(userInfo['FinishRate']);
	var marHTML0 = '<a class="mar"  href="detail?curPage=orderByStyle&Id=';
	var marHTML1 = '"><div class="mar"><div class="marLeft"><img src="';
	var marHTML2 = '" class="marLeft"></div><div class="marRight"><div class="marR1">编号：';
	var marHTML3 = 			'</div><div class="marR2">销量：</div><div class="marR3">第';
	var marHTML4 = 			'名</div><div class="marR4">我已订：</div><div class="marR5">';
	var marHTML5 = 			'件</div><div class="marR6">总订数：</div><div class="marR7">';
	var marHTML6 = 			'件</div></div></div></a>';

	var top1 = marHTML0 +TopSell[0]['Id']+ marHTML1 + TopSell[0]['Pictures'].split(',')[0]+
	marHTML2 + TopSell[0]['Id'] + marHTML3+ '1'
	+ marHTML4+ TopSell[0]['UserOrderNumber']+ marHTML5+ 
	TopSell[0]['OrderNumber']+ marHTML6;
	document.getElementById("top1").innerHTML = top1;
	var nextTop = "";
	for(var i = 1;i<TopSell.length;i++)
	{
			nextTop += (marHTML0 +TopSell[i]['Id']+ marHTML1 + TopSell[i]['Pictures'].split(',')[0]+
		marHTML2 + TopSell[i]['Id'] + marHTML3+ '1'
		+ marHTML4+ TopSell[i]['UserOrderNumber']+ marHTML5+ 
		TopSell[i]['OrderNumber']+ marHTML6);
	}
	document.getElementById("nextTop").innerHTML = nextTop;

	var rec = "";
	var marrecHTML0 = '<a class="mar" href="detail?curPage=orderByStyle&Id=';
	var marrecHTML1 = '"><div class="mar"><div class="marLeft"><img src="';
	
	marrecHTML2 = '" class="marLeft"></div><div class="marRight"><div class="marR1">编号：';
	marrecHTML3 = 			'</div><div class="marR2"></div><div class="marR3">';
	marrecHTML4 = 			'</div><div class="marR4">我已订：</div><div class="marR5">';
	marrecHTML5 = 			'件</div><div class="marR6">总订数：</div><div class="marR7">';
	marrecHTML6 = 			'件</div></div></div></a>';
	for(var i = TopSell.length - 1;i>0;i--)
	{
			rec += (marrecHTML0 +TopSell[i]['Id']+ marrecHTML1 + TopSell[i]['Pictures'].split(',')[0]+
		marrecHTML2 + TopSell[i]['Id'] + marrecHTML3+ '推荐款'
		+ marrecHTML4+ TopSell[i]['UserOrderNumber']+ marrecHTML5+ 
		TopSell[i]['OrderNumber']+ marrecHTML6);
	}
	document.getElementById("rec").innerHTML = rec;
}