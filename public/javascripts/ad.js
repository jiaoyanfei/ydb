$.ajax({
	type:"get",
	url:"ad",
	data:{data:"I am foreground"},
	error:function(err){alert(err);},
	success:loadAd
});


function loadAd(data,status)
{
	$("#billRank").text(data);
}