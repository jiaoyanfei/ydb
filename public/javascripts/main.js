var clipboard = [];
var clipboardTotal = 0;
function copy(obj)
{
	var size = 0;
	clipboard = [];
	clipboardTotal = 0;
	do
	{
		var id = "color"+ obj.id +"size"+size;
		clipboard.push(document.getElementById(id).value);
		clipboardTotal += Number(document.getElementById(id).value);
		size ++;

	}while(document.getElementById(id) != null);
}


function changeOrder()
{
	var total = 0;

	for(var i = 0;i<50;i++)
	{
		var rolTotal = 0;
		for(var j = 0;j<50;j++)
		{
			var id = "color"+ i +"size"+j;
			if(document.getElementById(id) != null)
			{
				rolTotal += Number(document.getElementById(id).value);
				total += Number(document.getElementById(id).value);

			}
			else
			{
				break;
			}
		}
		document.getElementById("rolTotal"+i).innerHTML = rolTotal;
		
		document.getElementById("total").innerHTML = total;
	}
}

function paste(obj)
{
	
	var size = 0;
	try
	{
		while(clipboard.length != 0)
		{
			var id = "color"+ obj.id +"size"+size;
			document.getElementById(id).value = clipboard[size];
			document.getElementById("rolTotal"+obj.id).innerHTML = clipboardTotal;
			
			size ++;
			if(document.getElementById(id) == null || size > 200)
			{

				break;
			}
		}
	}
	catch(e)
	{
		changeOrder();
	}
	finally
	{
		changeOrder();
	}
	
}
