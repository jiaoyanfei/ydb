package resource;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriInfo;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

@Path("order")
public class OrderResource20141203 {
    // The @Context annotation allows us to have certain contextual objects
    // injected into this class.
    // UriInfo object allows us to get URI information (no kidding).
    @Context
    UriInfo uriInfo;
 
    // Another "injected" object. This allows us to use the information that's
    // part of any incoming request.
    // We could, for example, get header information, or the requestor's address.
    @Context
    Request request;
    
    @GET
    @Path("chart")
    @Produces(MediaType.APPLICATION_JSON)
	public JSONObject chart(@QueryParam("userId") String userId,@QueryParam("byFirst") String byFirst,@QueryParam("bySecond") String bySecond) throws JSONException, IOException, SQLException {
        JSONObject jsonObj = new JSONObject();  
        JSONObject chartJson = new JSONObject(); 
    	String sql,selectFirst=byFirst,selectSecond=bySecond;
    	if (byFirst.equals("Color"))
    		selectFirst = "right(Color,2) as Color";
    	else if (bySecond.equals("Color"))
    		selectSecond = "right(Color,2) as Color";
        
    	if (byFirst.equals("Size")) {
	    	if (userId.startsWith("zongjingli"))
				sql = String.format("select ProductPrice, UserOrderDetails, %s from all_orders where Invalid=0 and InvalidPd=0 ", selectSecond);
			else if (userId.contains("zongdai"))
				sql = String.format("select ProductPrice, UserOrderDetails, %s from all_orders where Invalid=0 and InvalidPd=0 and AreaId=%s  ", selectSecond, userId.split(":")[1]);
			else if (userId.startsWith("area"))
				sql = String.format("select ProductPrice, UserOrderDetails, %s from all_orders where Invalid=0 and InvalidPd=0 and SuperAreaId=%s  ", selectSecond, userId.split(":")[1]);
			else	
				sql = String.format("select ProductPrice, UserOrderDetails, %s from all_orders where Invalid=0 and InvalidPd=0 and CustomerId='%s' ", selectSecond,userId.split(":")[1]);
	    	chartJson = this.batchQueryForChartSize(sql, chartJson, byFirst, bySecond);
    	} else if (bySecond.equals("Size")) {
	    	if (userId.startsWith("zongjingli"))
				sql = String.format("select ProductPrice, UserOrderDetails, %s from all_orders where Invalid=0 and InvalidPd=0 ", selectFirst);
			else if (userId.contains("zongdai"))
				sql = String.format("select ProductPrice, UserOrderDetails, %s from all_orders where Invalid=0 and InvalidPd=0 and AreaId=%s  ", selectFirst, userId.split(":")[1]);
			else if (userId.startsWith("area"))
				sql = String.format("select ProductPrice, UserOrderDetails, %s from all_orders where Invalid=0 and InvalidPd=0 and SuperAreaId=%s  ", selectFirst, userId.split(":")[1]);
			else	
				sql = String.format("select ProductPrice, UserOrderDetails, %s from all_orders where Invalid=0 and InvalidPd=0 and CustomerId='%s' ", selectFirst,userId.split(":")[1]);
	    	chartJson = this.batchQueryForChartSize(sql, chartJson, byFirst, bySecond);
    	} else {
    		if (userId.startsWith("zongjingli"))
				sql = String.format("select %s, %s, sum(UserOrderAmount) as OrderAmount from all_orders where Invalid=0 and InvalidPd=0 group by %s,%s", selectFirst,selectSecond, byFirst,bySecond);
			else if (userId.contains("zongdai"))
				sql = String.format("select %s, %s, sum(UserOrderAmount) as OrderAmount from all_orders where Invalid=0 and InvalidPd=0 and AreaId=%s group by %s,%s", selectFirst,selectSecond, userId.split(":")[1], byFirst,bySecond);
			else if (userId.startsWith("area"))
				sql = String.format("select %s, %s, sum(UserOrderAmount) as OrderAmount from all_orders where Invalid=0 and InvalidPd=0 and SuperAreaId=%s group by %s,%s", selectFirst,selectSecond, userId.split(":")[1], byFirst,bySecond);
			else	
				sql = String.format("select %s,%s, sum(UserOrderAmount) as OrderAmount from all_orders where Invalid=0 and InvalidPd=0 and CustomerId='%s' "
						+ " group by %s,%s", selectFirst,selectSecond,userId.split(":")[1], byFirst,bySecond);
	    	 
    		chartJson = this.batchQueryForChart(sql, chartJson, byFirst, bySecond);
    	}
		if (chartJson.has("chart")){
			String host="/usr/share/tomcat/webapps/yidingbaodhh_total";
	    	String jsonFile = host+"/FusionCharts/drawChart.Data.json";
		    FileWriter fw = new FileWriter(jsonFile); 
		    try {
		    	fw.write(chartJson.toString()); 
			    fw.close(); 
		    } catch (Exception e) { 
			    e.printStackTrace(); 
		    } finally { 
			    try { 
				    fw.close(); 
			    } catch (Exception e) { 
			    	e.printStackTrace(); 
			    } 
		    } 
		    
		    String phantomjs = host+"/FusionCharts/phantomjs";
		    String jsScript = host+"/FusionCharts/drawChart.js";
		    String imageFile= host+"/pictures/analysis_charts/"+userId.split(":")[1]+byFirst+bySecond+".jpg";
		    String cmd = String.format("%s %s %s", phantomjs, jsScript, imageFile);
		    Process p = Runtime.getRuntime().exec(cmd); 
		    BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));   
	        String readLine = br.readLine();   
	        while (readLine != null) { 
	            readLine = br.readLine(); 
	        } 
	        if(br!=null){ 
	            br.close(); 
	        } 
	        p.destroy(); 
	        p=null; 
	        
	        jsonObj.put("ChartPath", "pictures/analysis_charts/"+userId.split(":")[1]+byFirst+bySecond+".jpg");
		} else
			jsonObj.put("ChartPath", "");
        return jsonObj;
    }

    
    @GET
    @Path("refresh")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject refresh() throws JSONException {
	    JSONObject jsonObj = new JSONObject();  
	    jsonObj.put("ResultCode", "0");
	    DBConnPool pool = new DBConnPool();
		try {
			String sql = "delete from all_orders where Invalid=0 and InvalidPd=0 and userordernumber=0 and id>0;";
			System.out.println(sql);
			pool.stmt.executeUpdate(sql);  

			sql = "update all_orders as a set productcode=(select productcode from products where id=a.productid), productname=(select productname from products where id=a.productid)"
					+ ",ProductType=(select ProductType  from products where id=a.productid),ProductClass=(select ProductClass  from products where id=a.productid)"
					+ ",ProductSerial=(select ProductSerial   from products where id=a.productid),PriceZone=(select PriceZone  from products where id=a.productid)"
					+ ", ProductPrice=(select TagPrice  from products where id=a.productid), Mucai=(select Mucai  from products where id=a.productid) "
					+ ", userorderamount=userordernumber*(select TagPrice  from products where id=a.productid) where a.id>0";
			System.out.println(sql);
			pool.stmt.executeUpdate(sql);
			
			sql = "update user_info as a set OrderProductNumber=(select sum(userordernumber) from all_orders where Invalid=0 and InvalidPd=0 and customerid=a.id),orderamount=(select sum(userorderamount) from all_orders where Invalid=0 and InvalidPd=0 and customerid=a.id),orderstylenumber=(select count(distinct productid) from all_orders where Invalid=0 and InvalidPd=0 and customerid=a.id) where a.id>0;";
			System.out.println(sql);
			pool.stmt.executeUpdate(sql);  
			
			sql = "update user_info as a set OrderProductNumber=0,orderamount=0,orderstylenumber=0 where id>0 and orderamount is null;";
			System.out.println(sql);
			pool.stmt.executeUpdate(sql);  
			
			sql = "update products as a set ordernumber=(select sum(userordernumber) from all_orders where Invalid=0 and productid=a.id),orderamount=(select sum(userorderamount) from all_orders where Invalid=0 and productid=a.id) where a.id>0;";
			System.out.println(sql);
			pool.stmt.executeUpdate(sql);  

			sql = "update products as a set ordernumber=0,orderamount=0 where id>0 and ordernumber is null;";
			System.out.println(sql);
			pool.stmt.executeUpdate(sql);  

		} catch (SQLException e) {
		// TODO Auto-generated catch block
			jsonObj.put("ResultCode", "1");
			e.printStackTrace();
		} finally {  
	    	try {  
	    		if (!pool.stmt.isClosed()) 
	    		{
	    			pool.stmt.close();
	    		}
	    		if (!pool.conn.isClosed()) 
	    		{
	    			pool.conn.close();
	    		}
	        } catch (SQLException e) {  
                e.printStackTrace();  
            }  
	    }
		return jsonObj;
	}
    
    @GET
    @Path("analyzeByTOP10")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public JSONArray analyzeByTOP10() throws JSONException {
	    JSONArray jArr = new JSONArray();  
		return this.batchQuery("select Id, Name,BudgetAmount,OrderAmount, OrderProductNumber,OrderStyleNumber,GuideAmount from  user_info "
				+ "where authority like 'diancang%%' and OrderAmount > 0 order by OrderAmount desc limit 0,20", jArr);
	} 
    
    
    
    @POST
	@Path("submit")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject submitOrder(MultivaluedMap<String, String> orderParams) throws JSONException{
    	String productId = orderParams.getFirst("productId");
    	String productCode = orderParams.getFirst("productCode");
    	String userId = orderParams.getFirst("userId");
	    int productPrice = Integer.valueOf(orderParams.getFirst("productPrice"));
	    int skc = Integer.valueOf(orderParams.getFirst("SKC"));
	    String priceZone = orderParams.getFirst("priceZone");
	    String productType = orderParams.getFirst("productType");
	    String mucai = orderParams.getFirst("mucai");
	    String productSerial = orderParams.getFirst("productSerial");
	    String productClass = orderParams.getFirst("productClass");
	    String productName = orderParams.getFirst("productName");
//	    String orderNumber = orderParams.getFirst("orderNumber");
//	    String orderAmount = orderParams.getFirst("orderAmount");
	    String changeNumber = orderParams.getFirst("changeNumber");
	    String changeAmount = orderParams.getFirst("changeAmount");
	    String[] orderInfo = orderParams.getFirst("orderInfo").split("\\|\\|");
	    String invalidPd = orderParams.getFirst("invalidPd");
	    String areaId = orderParams.getFirst("areaId");
	    String superAreaId = orderParams.getFirst("superAreaId");
	    JSONObject jsonObj = new JSONObject();  
	    jsonObj.put("ResultCode", "1");
	    DBConnPool pool = new DBConnPool();
		try {
			String sql = String.format("select Name from user_info where id=%s", areaId);
			jsonObj = this.singleQuery(sql , jsonObj);
			String[] colorOrder; 
			int colorAmount;
			// "花梨色|20::直径90:10,直径80:10||乌木色|30::直径90:10,直径80:20"
			sql = String.format("delete from all_orders where customerid=%s and productid=%s", userId.split(":")[1], productId);
			pool.stmt.executeUpdate(sql);
			for (int i=0;i<orderInfo.length;i++){
				System.out.println(orderInfo[0]);
				colorOrder = orderInfo[i].split("::");
				System.out.println(colorOrder[0]);
				colorAmount = Integer.valueOf(colorOrder[0].split("\\|")[1])*productPrice;
				sql = String.format("insert into all_orders "
						+ "(ProductId, CustomerId, Color, SKC, PriceZone, ProductCode, Mucai, ProductType, ProductClass,ProductSerial, ProductName, ProductPrice,UserOrderNumber,UserOrderAmount, UserOrderDetails,Invalid, InvalidPd, SuperAreaId, AreaId, AreaName)"
						+ " Values ('%s', '%s', '%s', %d, '%s', '%s', '%s', '%s', '%s','%s','%s', %d,%s,%d, '%s',0, %s, %s, '%s', '%s')  "
						, productId, userId.split(":")[1], colorOrder[0].split("\\|")[0], skc, priceZone, productCode, mucai, productType, productClass, productSerial, productName, productPrice, colorOrder[0].split("\\|")[1], colorAmount, colorOrder[1], invalidPd, superAreaId, areaId, jsonObj.get("Name"));
				System.out.println(sql);
				pool.stmt.executeUpdate(sql);
			}
			jsonObj.put("ResultCode", "0");
			if (invalidPd.equals("0")){
				sql = String.format("UPDATE user_info set OrderAmount=OrderAmount+(%s)"
						+ ",OrderStyleNumber=(select count(distinct productid) from all_orders where Invalid=0 and InvalidPd=0 and customerid=%s)"
						+ ",OrderProductNumber=OrderProductNumber+(%s) where id=%s"
						, changeAmount, userId.split(":")[1], changeNumber, userId.split(":")[1]);
				System.out.println(sql);
				pool.stmt.executeUpdate(sql);
			}
			sql=String.format("update products set OrderAmount=OrderAmount+(%s),OrderNumber=OrderNumber+(%s) where id=%s", changeAmount, changeNumber, productId);
			System.out.println(sql);
			pool.stmt.executeUpdate(sql);
		} catch (SQLException | JSONException e) {
			
		// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {  
	    	try {
	    		if (pool != null ){
	    			try {
			    		if (pool.stmt!= null&& !pool.stmt.isClosed()) 
			    		{
			    			pool.stmt.close();
			    			pool.stmt = null;
			    		}
	    			} catch (SQLException e) {  
	                    e.printStackTrace();  
	                }
		    		if (pool.conn!=null && !pool.conn.isClosed()) 
		    		{
		    			pool.conn.close();
		    			pool.conn = null;
		    		}
	    		} 
	        } catch (SQLException e) {  
                e.printStackTrace();  
            }  
	    }
		return jsonObj;
	}

	@GET
    @Path("cancel")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject cancelOrder(@QueryParam("userId") String userId, @QueryParam("productId") String productId, @QueryParam("orderNumber") String orderNumber, @QueryParam("orderAmount") String orderAmount, @QueryParam("invalidPd") String invalidPd) throws JSONException {
	  	JSONObject jsonObj = new JSONObject();  
	    jsonObj.put("ResultCode", "1");
	    DBConnPool pool = new DBConnPool();
		try {
			String sql = String.format("update all_orders set Invalid=1 where ProductId='%s' and CustomerId='%s'", productId, userId.split(":")[1]);
			System.out.println(sql);
			int result = pool.stmt.executeUpdate(sql);  
			if (result >= 1){
				jsonObj.put("ResultCode", "0");
				if (invalidPd.equals("0")){
					sql = String.format("UPDATE user_info set OrderAmount=OrderAmount-%s"
							+ ",OrderStyleNumber=OrderStyleNumber-1"
							+ ",OrderProductNumber=OrderProductNumber-%s where id=%s"
							, orderAmount, orderNumber, userId.split(":")[1]);
					System.out.println(sql);
					result = pool.stmt.executeUpdate(sql);
				}
				sql = String.format("update products set OrderAmount=OrderAmount-%s"
						+ ",OrderNumber=OrderNumber-%s where id=%s;"
						, orderAmount, orderNumber, productId);
				System.out.println(sql);
				result = pool.stmt.executeUpdate(sql);
			}
		} catch (SQLException | JSONException e) {
		// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {  
	    	try {  
	    		if (!pool.stmt.isClosed()) 
	    		{
	    			pool.stmt.close();
	    		}
	    		if (!pool.conn.isClosed()) 
	    		{
	    			pool.conn.close();
	    		}
	        } catch (SQLException e) {  
                e.printStackTrace();  
            }  
	    }
		return jsonObj;
	}

    @GET
    @Path("getCompletePercentage")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public JSONObject getCompletePercentage(@QueryParam("userId") String userId) throws JSONException {
		JSONObject jsonObj = new JSONObject();
		String sql;
		if (userId.contains("zongdai")){
			sql = "select count(distinct productid) as OrderStyleNumber from all_orders where Invalid=0 and InvalidPd=0 and areaid="+userId.split(":")[1];
			System.out.println(sql);
			jsonObj = this.singleQuery(sql, jsonObj);
			jsonObj = this.singleQuery("select sum(OrderAmount) as OrderAmount, sum(BudgetAmount) as BudgetAmount from user_info where authority like 'diancang%%' and areaId="+userId.split(":")[1], jsonObj);
			
		}
		else if (userId.startsWith("zongjingli")){
			jsonObj = this.singleQuery("select sum(OrderAmount) as OrderAmount, count(*) as OrderStyleNumber from products where ordernumber>0 and deleted=0", jsonObj);
		    jsonObj = this.singleQuery("select BudgetAmount from user_info where id="+userId.split(":")[1], jsonObj);
		}
		else
			jsonObj = this.singleQuery("select OrderAmount,BudgetAmount from user_info where id="+userId.split(":")[1], jsonObj);
		return jsonObj;
    }
	
//    @GET
//    @Path("mine")
//	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
//	public JSONObject getMyBatchOrders(@QueryParam("userId") String userId, @QueryParam("startIndex") String startIndex, @QueryParam("count") String count) throws JSONException {
//		JSONObject jsonObj = new JSONObject();
//		jsonObj = this.singleQuery("select max(orderAmount) as maxOrderAmount, max(OrderStyleNumber) as maxOrderStyleNumber, max(OrderProductNumber) as maxOrderProductNumber from user_info where authority like 'diancang%%'",
//				jsonObj);
//		jsonObj.put("SKC", "136");
//		jsonObj.put("SKC2", "248");
//		String sql;
//		JSONArray jsonArray = new JSONArray();  
//    	if (userId.startsWith("zongjingli")){
//			sql = String.format("select Id as ProductId,ProductCode,ProductName,OrderNumber,Price as ProductPrice, OrderAmount,ProductSerial from products where OrderNumber > 0 order by OrderNumber desc limit %s, %s", startIndex, count);
//			jsonObj = this.singleQuery("select count(*) as OrderStyleNumber,sum(OrderNumber) as OrderProductNumber, sum(OrderAmount) as OrderAmount from products where OrderNumber > 0", jsonObj);
//			jsonObj = this.singleQuery("select BudgetAmount as GuideAmount from user_info where id ="+ userId.split(":")[1],jsonObj);
//			jsonArray = this.batchQuery(sql, jsonArray);
//    	} else if (userId.contains("zongdai")){
//			sql = String.format("select ProductId, ProductCode,ProductName,sum(UserOrderNumber) as OrderNumber,ProductPrice,sum(UserOrderAmount) as OrderAmount,ProductSerial from all_orders where Invalid=0 and InvalidPd=0 and AreaId=%s group by ProductId order by OrderNumber desc limit %s,%s;"
//					, userId.split(":")[1], startIndex, count);
//			jsonObj = this.singleQuery("select count(distinct productid) as OrderStyleNumber from all_orders where Invalid=0 and InvalidPd=0 and areaid="+userId.split(":")[1], jsonObj);
//			jsonObj = this.singleQuery("select  sum(OrderProductNumber) as OrderProductNumber, sum(OrderAmount) as OrderAmount, sum(GuideAmount) as GuideAmount from user_info where Authority like 'diancang%%' and AreaId ="+ userId.split(":")[1],jsonObj);
//			jsonArray = this.batchQuery(sql, jsonArray);
//			
//		}  else if (userId.startsWith("area")){
//			sql = String.format("select ProductId, ProductCode,ProductName,sum(UserOrderNumber) as OrderNumber,ProductPrice,sum(UserOrderAmount) as OrderAmount,ProductSerial from all_orders where Invalid=0 and InvalidPd=0 and SuperAreaId=%s group by ProductId order by OrderNumber desc limit %s,%s;"
//					, userId.split(":")[1], startIndex, count);
//			jsonObj = this.singleQuery("select count(distinct productid) as OrderStyleNumber from all_orders where Invalid=0 and InvalidPd=0 and SuperAreaId="+userId.split(":")[1], jsonObj);
//			jsonObj = this.singleQuery("select  sum(OrderProductNumber) as OrderProductNumber, sum(OrderAmount) as OrderAmount, sum(GuideAmount) as GuideAmount from user_info where Authority like 'diancang%%' and SuperAreaId ="+ userId.split(":")[1],jsonObj);
//			jsonArray = this.batchQuery(sql, jsonArray);
//			
//		} else {
//			sql = String.format("select ProductId,ProductCode,ProductName,UserOrderNumber as OrderNumber,ProductPrice, UserOrderAmount as OrderAmount,Color, UserOrderDetails from all_orders where Invalid=0 and InvalidPd=0 and CustomerId='%s' order by OrderNumber desc  limit %s,%s", userId.split(":")[1], startIndex, count);
//			jsonObj = this.singleQuery("select OrderStyleNumber,OrderProductNumber,OrderAmount, GuideAmount from user_info where Id="+userId.split(":")[1], jsonObj);
//			jsonArray = this.batchQuery(sql, jsonArray);
//		}
//		jsonObj.put("orderArray", jsonArray);
//		return jsonObj;
//    }

    @GET
    @Path("filter")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public JSONObject filterMyBatchOrders(@QueryParam("numberOrder") String numberOrder, @QueryParam("idOrder") String idOrder, @DefaultValue("all") @QueryParam("mucai") String mucai, @DefaultValue("all") @QueryParam("type") String type, @DefaultValue("all") @QueryParam("class") String classParam, @DefaultValue("all") @QueryParam("serial") String serial
			, @QueryParam("userId") String userId, @QueryParam("startIndex") String startIndex, @QueryParam("count") String count) throws JSONException {
		JSONObject jsonObj = new JSONObject();
		jsonObj = this.singleQuery("select max(orderAmount) as maxOrderAmount, max(OrderStyleNumber) as maxOrderStyleNumber, max(OrderProductNumber) as maxOrderProductNumber from user_info",
				jsonObj);
		jsonObj = this.singleQuery("select sum(SKC) as TotalSKC from products", jsonObj);
		String orderSql = "order by ";
		if (numberOrder != null)
			if (numberOrder.equals("desc"))
				orderSql += " OrderNumber desc";
			else
				orderSql +=" OrderNumber asc";
		if (idOrder != null)
			if (idOrder.equals("desc"))
				orderSql += ", ProductId desc";
			else
				orderSql += ", ProductId asc";
		
		if (orderSql.equals("order by "))
			orderSql += " OrderNumber desc";
		else
			orderSql = orderSql.replace("order by ,", "order by ");
		
		String whereSql = "";
		if (!mucai.equalsIgnoreCase("all"))
			whereSql += String.format(" and Mucai='%s'", mucai);
		if (!type.equalsIgnoreCase("all"))
			whereSql += String.format(" and ProductType='%s'", type);
		if (!classParam.equalsIgnoreCase("all"))
			whereSql += String.format(" and productClass='%s'", classParam);
		if (!serial.equalsIgnoreCase("all"))
			whereSql += String.format(" and productSerial='%s'", serial);
		String sql;
		JSONArray jsonArray = new JSONArray();  
//    	if (userId.startsWith("zongjingli")){
//			jsonObj = this.singleQuery("select count(*) as OrderSKC, count(distinct productid) as OrderStyleNumber from all_orders where Invalid=0 and InvalidPd=0", jsonObj);
//			jsonObj = this.singleQuery("select  sum(OrderProductNumber) as OrderProductNumber, sum(OrderAmount) as OrderAmount, sum(GuideAmount) as GuideAmount from user_info where authority like 'diancang%%' ",jsonObj);
//			jsonObj = this.singleQuery("select count(distinct productid) as FilterOrderStyleNumber, IFNULL(sum(UserOrderNumber),0) as FilterOrderProductNumber, IFNULL(sum(UserOrderAmount),0) as FilterOrderAmount from all_orders where Invalid=0 and InvalidPd=0 "+whereSql, jsonObj);
//			sql = String.format("select ProductId, count(*) as SKC, ProductCode,ProductName,sum(UserOrderNumber) as OrderNumber,ProductPrice,sum(UserOrderAmount) as OrderAmount,Mucai,ProductSerial from all_orders where Invalid=0 and InvalidPd=0 %s group by ProductId %s limit %s,%s;"
//					,whereSql, orderSql, startIndex, count);
//			jsonArray = this.batchQuery(sql, jsonArray);
//    	} else if (userId.contains("zongdai")){
//			jsonObj = this.singleQuery("select count(*) as OrderSKC, count(distinct productid) as OrderStyleNumber from all_orders where Invalid=0 and InvalidPd=0 and areaid="+userId.split(":")[1], jsonObj);
//			jsonObj = this.singleQuery("select  sum(OrderProductNumber) as OrderProductNumber, sum(OrderAmount) as OrderAmount, sum(GuideAmount) as GuideAmount from user_info where authority like 'diancang%%' and AreaId ="+ userId.split(":")[1],jsonObj);
//			jsonObj = this.singleQuery("select count(distinct productid) as FilterOrderStyleNumber, IFNULL(sum(UserOrderNumber),0) as FilterOrderProductNumber, IFNULL(sum(UserOrderAmount),0) as FilterOrderAmount from all_orders where Invalid=0 and InvalidPd=0 and areaid="+userId.split(":")[1]+whereSql, jsonObj);
//			sql = String.format("select ProductId, count(*) as SKC, ProductCode,ProductName,sum(UserOrderNumber) as OrderNumber,ProductPrice,sum(UserOrderAmount) as OrderAmount,Mucai,ProductSerial from all_orders where Invalid=0 and InvalidPd=0 and AreaId=%s %s group by ProductId %s limit %s,%s;"
//					, userId.split(":")[1],whereSql, orderSql, startIndex, count);
//			jsonArray = this.batchQuery(sql, jsonArray);
//			
//		}  else if (userId.startsWith("area")){
////			jsonObj = this.singleQuery("select sum(a.SKC) as TotalSKC from (select SKC from all_orders where Invalid=0 and InvalidPd=0 and SuperAreaId="+userId.split(":")[1]+" group by productid) as a", jsonObj);
//			jsonObj = this.singleQuery("select count(*) as OrderSKC, count(distinct productid) as OrderStyleNumber from all_orders where Invalid=0 and InvalidPd=0 and SuperAreaId="+userId.split(":")[1], jsonObj);
//			jsonObj = this.singleQuery("select  sum(OrderProductNumber) as OrderProductNumber, sum(OrderAmount) as OrderAmount, sum(GuideAmount) as GuideAmount from user_info where authority like 'diancang%%' and SuperAreaId ="+ userId.split(":")[1],jsonObj);
//			jsonObj = this.singleQuery("select count(distinct productid) as FilterOrderStyleNumber, IFNULL(sum(UserOrderNumber),0) as FilterOrderProductNumber, IFNULL(sum(UserOrderAmount),0) as FilterOrderAmount from all_orders where Invalid=0 and InvalidPd=0 and SuperAreaId="+userId.split(":")[1]+whereSql, jsonObj);
//			sql = String.format("select ProductId, count(*) as SKC, ProductCode,ProductName,sum(UserOrderNumber) as OrderNumber,ProductPrice,sum(UserOrderAmount) as OrderAmount,Mucai,ProductSerial from all_orders where Invalid=0 and InvalidPd=0 and SuperAreaId=%s %s group by ProductId %s limit %s,%s;"
//					, userId.split(":")[1],whereSql, orderSql, startIndex, count);
//			jsonArray = this.batchQuery(sql, jsonArray);
//			
//		} else {
////			jsonObj = this.singleQuery("select sum(a.SKC) as TotalSKC from (select SKC from all_orders where Invalid=0 and InvalidPd=0 and CustomerId="+userId.split(":")[1]+" group by productid) as a", jsonObj);
//			jsonObj = this.singleQuery("select OrderStyleNumber,OrderProductNumber,OrderAmount, GuideAmount from user_info where Id="+userId.split(":")[1], jsonObj);
//			jsonObj = this.singleQuery("select count(*) as OrderSKC from all_orders where Invalid=0 and InvalidPd=0 and CustomerId="+userId.split(":")[1], jsonObj);
//			jsonObj = this.singleQuery("select count(distinct productid) as FilterOrderStyleNumber, IFNULL(sum(UserOrderNumber),0) as FilterOrderProductNumber, IFNULL(sum(UserOrderAmount),0) as FilterOrderAmount from all_orders where Invalid=0 and InvalidPd=0 and CustomerId="+userId.split(":")[1]+ whereSql, jsonObj);
//			sql = String.format("select ProductId,count(*) as SKC, ProductCode,ProductName,sum(UserOrderNumber) as OrderNumber,ProductPrice, sum(UserOrderAmount) as OrderAmount,group_concat(Color separator '<br>') as Color, group_concat(UserOrderDetails separator '<br>') as ColorOrderDetail from all_orders where Invalid=0 and InvalidPd=0 and CustomerId='%s' %s group by ProductId %s limit %s,%s", userId.split(":")[1], whereSql, orderSql, startIndex, count);
//			jsonArray = this.batchQuery(sql, jsonArray);
//		}
    	if (userId.startsWith("zongjingli")){
			jsonObj = this.singleQuery("select count(distinct productid,color) as OrderSKC, count(distinct productid) as OrderStyleNumber from all_orders where Invalid=0 and invalidpd=0", jsonObj);
			jsonObj = this.singleQuery("select  sum(OrderProductNumber) as OrderProductNumber, sum(OrderAmount) as OrderAmount, sum(GuideAmount) as GuideAmount from user_info where authority like 'diancang%%' ",jsonObj);
			jsonObj = this.singleQuery("select count(distinct productid) as FilterOrderStyleNumber, IFNULL(sum(UserOrderNumber),0) as FilterOrderProductNumber, IFNULL(sum(UserOrderAmount),0) as FilterOrderAmount from all_orders where Invalid=0 and InvalidPd=0 "+whereSql, jsonObj);
			sql = String.format("select ProductId, ProductClass, count(distinct productid,color) as SKC, ProductCode,Sum(OrderNumber) as OrderNumber,ProductPrice,sum(OrderAmount) as OrderAmount,group_concat(Color separator '<br>') as Color,group_concat(OrderNumber separator '<br>') as ColorOrderDetail "
					+ "from (select ProductId,ProductClass,ProductCode,sum(UserOrderNumber) as OrderNumber,ProductPrice,sum(UserOrderAmount) as OrderAmount,Color from all_orders where Invalid=0 and InvalidPd=0 %s group by ProductId,Color) as a group by ProductId %s limit %s,%s;"
					,whereSql, orderSql, startIndex, count);
			jsonArray = this.batchQuery(sql, jsonArray);
    	} else if (userId.contains("zongdai")){
			jsonObj = this.singleQuery("select count(distinct productid,color) as OrderSKC, count(distinct productid) as OrderStyleNumber from all_orders where Invalid=0 and InvalidPd=0 and areaid="+userId.split(":")[1], jsonObj);
			jsonObj = this.singleQuery("select  sum(OrderProductNumber) as OrderProductNumber, sum(OrderAmount) as OrderAmount, sum(GuideAmount) as GuideAmount from user_info where authority like 'diancang%%' and AreaId ="+ userId.split(":")[1],jsonObj);
			jsonObj = this.singleQuery("select count(distinct productid) as FilterOrderStyleNumber, IFNULL(sum(UserOrderNumber),0) as FilterOrderProductNumber, IFNULL(sum(UserOrderAmount),0) as FilterOrderAmount from all_orders where Invalid=0 and InvalidPd=0 and areaid="+userId.split(":")[1]+whereSql, jsonObj);
			sql = String.format("select ProductId, ProductCode, count(distinct productid,color) as SKC, ProductClass,Sum(OrderNumber) as OrderNumber,ProductPrice,sum(OrderAmount) as OrderAmount,group_concat(Color separator '<br>') as Color,group_concat(UserOrderDetails separator '<br>') as ColorOrderDetail "
					+ "from (select ProductId,ProductCode,ProductClass,sum(UserOrderNumber) as OrderNumber,group_concat(UserOrderDetails separator ';') as UserOrderDetails, ProductPrice,sum(UserOrderAmount) as OrderAmount,Color from all_orders where Invalid=0 and InvalidPd=0 and AreaId=%s %s group by ProductId,Color) as a group by ProductId %s limit %s,%s;"
					, userId.split(":")[1],whereSql, orderSql, startIndex, count);
			jsonArray = this.batchQueryForZongDaiOrders(sql, jsonArray);
			
		}  else if (userId.startsWith("area")){
			jsonObj = this.singleQuery("select count(distinct productid,color) as OrderSKC, count(distinct productid) as OrderStyleNumber from all_orders where Invalid=0 and InvalidPd=0 and superareaid="+userId.split(":")[1], jsonObj);
			jsonObj = this.singleQuery("select  sum(OrderProductNumber) as OrderProductNumber, sum(OrderAmount) as OrderAmount, sum(GuideAmount) as GuideAmount from user_info where authority like 'diancang%%' and SuperAreaId ="+ userId.split(":")[1],jsonObj);
			jsonObj = this.singleQuery("select count(distinct productid) as FilterOrderStyleNumber, IFNULL(sum(UserOrderNumber),0) as FilterOrderProductNumber, IFNULL(sum(UserOrderAmount),0) as FilterOrderAmount from all_orders where Invalid=0 and InvalidPd=0 and superareaid="+userId.split(":")[1]+whereSql, jsonObj);
			sql = String.format("select ProductId, ProductCode, count(*) as SKC, ProductClass,Sum(OrderNumber) as OrderNumber,ProductPrice,sum(OrderAmount) as OrderAmount,group_concat(Color separator '<br>') as Color,group_concat(OrderNumber separator '<br>') as ColorOrderDetail "
					+ "from (select ProductId,ProductClass,ProductCode,sum(UserOrderNumber) as OrderNumber,ProductPrice,sum(UserOrderAmount) as OrderAmount,Color from all_orders where Invalid=0 and InvalidPd=0 and SuperAreaId=%s %s group by ProductId,Color) as a group by ProductId %s limit %s,%s;"
					, userId.split(":")[1],whereSql, orderSql, startIndex, count);
//			System.out.println(sql);
			jsonArray = this.batchQuery(sql, jsonArray);
			
		} else {
			jsonObj = this.singleQuery("select OrderStyleNumber,OrderProductNumber,OrderAmount,GuideAmount from user_info where Id="+userId.split(":")[1], jsonObj);
			jsonObj = this.singleQuery("select count(distinct productid,color) as OrderSKC from all_orders where Invalid=0 and InvalidPd=0 and CustomerId="+userId.split(":")[1], jsonObj);
			jsonObj = this.singleQuery("select count(distinct productid) as FilterOrderStyleNumber, IFNULL(sum(UserOrderNumber),0) as FilterOrderProductNumber, IFNULL(sum(UserOrderAmount),0) as FilterOrderAmount from all_orders where Invalid=0 and InvalidPd=0 and CustomerId="+userId.split(":")[1]+ whereSql, jsonObj);
			sql = String.format("select ProductId,ProductCode, count(*) as SKC, ProductClass,sum(UserOrderNumber) as OrderNumber,ProductPrice, sum(UserOrderAmount) as OrderAmount,group_concat(Color separator '<br>') as Color, group_concat(UserOrderDetails separator '<br>') as ColorOrderDetail "
					+ "from all_orders where Invalid=0 and InvalidPd=0 and CustomerId='%s' %s group by ProductId %s limit %s,%s", userId.split(":")[1], whereSql, orderSql, startIndex, count);
			jsonArray = this.batchQuery(sql, jsonArray);
		}		
    	jsonObj.put("orderArray", jsonArray);
		return jsonObj;
    }
    
    @GET
    @Path("analyzeByType")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public JSONArray analyzeOrderByProductType(@QueryParam("userId") String userId) throws JSONException {
		return this.analyzeOrderByWhich(userId, "ProductType");
    }

    @GET
    @Path("analyzeByMucai")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public JSONArray analyzeByMucai(@QueryParam("userId") String userId) throws JSONException {
		return this.analyzeOrderByWhich(userId, "Mucai");
	}

    @GET
    @Path("analyzeByPricezone")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public JSONArray analyzeByPricezone(@QueryParam("userId") String userId) throws JSONException {
		return this.analyzeOrderByWhich(userId, "PriceZone");
	}

    @GET
    @Path("analyzeBySize")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public JSONArray analyzeOrderBySize(@QueryParam("userId") String userId) throws JSONException {
		String sql;
    	if (userId.startsWith("zongjingli"))
			sql = String.format("select ProductPrice,UserOrderDetails from all_orders where Invalid=0 and InvalidPd=0");
		else if (userId.contains("zongdai")){
			sql = String.format("select ProductPrice,UserOrderDetails from all_orders where Invalid=0 and InvalidPd=0 and AreaId=%s", userId.split(":")[1]);
		} else if (userId.startsWith("area")){
			sql = String.format("select ProductPrice,UserOrderDetails from all_orders where Invalid=0 and InvalidPd=0 and SuperAreaId=%s", userId.split(":")[1]);
		} else
			sql = String.format("select ProductPrice,UserOrderDetails from all_orders where Invalid=0 and InvalidPd=0 and CustomerId='%s'", userId.split(":")[1]);
    	JSONArray jsonArray = new JSONArray();  
		jsonArray = this.batchQueryForAnalyzeBySize(sql, jsonArray);
		return jsonArray;
    }

    @GET
    @Path("analyzeByClass")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public JSONArray analyzeOrderByProductClass(@QueryParam("userId") String userId) throws JSONException {
		return this.analyzeOrderByWhich(userId,"ProductClass");
    }

    @GET
    @Path("analyzeBySerial")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public JSONArray analyzeOrderByProductSerial(@QueryParam("userId") String userId) throws JSONException {
		return this.analyzeOrderByWhich(userId, "ProductSerial");
    }

    @GET
    @Path("analyzeByCustomer")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public JSONArray analyzeOrderByCustomer(@QueryParam("userId") String userId) throws JSONException {
		String sql;
    	if (userId.startsWith("zongjingli"))
			sql = String.format("select Id, Name, OrderStyleNumber, OrderProductNumber, OrderAmount,  BudgetAmount,  GuideAmount from user_info where authority like 'diancang%%' order by OrderAmount desc", userId.split(":")[1]);
    	else if (userId.startsWith("area"))
			sql = String.format("select Id, Name, OrderStyleNumber, OrderProductNumber, OrderAmount,  BudgetAmount,  GuideAmount from user_info where superareaId='%s' and authority like 'diancang%%' order by OrderAmount desc", userId.split(":")[1]);
		else 
			sql = String.format("select Id, Name, OrderStyleNumber, OrderProductNumber, OrderAmount,  BudgetAmount,  GuideAmount from user_info where areaId='%s' and authority like 'diancang%%' order by OrderAmount desc", userId.split(":")[1]);
		JSONArray jsonArray = new JSONArray();  
		jsonArray = this.batchQuery(sql, jsonArray);
		return jsonArray;
    }
    
    @GET
    @Path("profitForcastByCustomer")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public JSONArray profitForcastByCustomer(@QueryParam("userId") String userId, @QueryParam("discount") String discount) throws JSONException {
		String sql;
    	if (userId.contains("zongdai"))
    		sql = String.format("select CustomerId, Name, sum(UserOrderNumber*ProductPrice*%s/10) as UserOrderAmount, sum(UserOrderAmount) as SellAmount "
    				+ " from  (select * from all_orders where Invalid=0 and InvalidPd=0 and  AreaId=%s ) as a "
    				+ " inner join (select Id, Name from user_info ) as c on a.CustomerId = c.Id group by CustomerId", discount, userId.split(":")[1]);
		else {
			sql = String.format("select CustomerId, Name, sum(UserOrderNumber*ProductPrice*%s/10) as UserOrderAmount, sum(UserOrderAmount*Discount/10) as SellAmount "
					+ " from  (select * from all_orders where Invalid=0 and InvalidPd=0 and  SuperAreaId=%s ) as a "
					+ " inner join (select Id, Name, Discount from user_info ) as c on a.CustomerId = c.Id group by CustomerId", discount, userId.split(":")[1]);
		}
		JSONArray jsonArray = new JSONArray();  
		jsonArray = this.batchQuery(sql, jsonArray);
		return jsonArray;
    }

    @GET
    @Path("profitForcastByMucai")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public JSONArray profitForcastByMucai(@QueryParam("userId") String userId, @QueryParam("discount") String discount) throws JSONException {
    	return this.profitForcastByWhich(userId, "Mucai", discount);
    }

    @GET
    @Path("profitForcastByPricezone")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public JSONArray profitForcastByPricezone(@QueryParam("userId") String userId, @QueryParam("discount") String discount) throws JSONException {
    	return this.profitForcastByWhich(userId, "PriceZone", discount);
    }

    @GET
    @Path("analyzeByColor")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public JSONArray analyzeOrderByColor(@QueryParam("userId") String userId) throws JSONException {
		String sql;
    	if (userId.startsWith("diancang:"))
			sql = String.format("select %s, count(distinct ProductId) as OrderStyleNumber, sum(UserOrderNumber) as UserOrderNumber, count(distinct productid,color) as SKC, sum(UserOrderAmount) as UserOrderAmount from all_orders where Invalid=0 and InvalidPd=0 and CustomerId=%s group by %s"
					,"right(Color,2) as Color",userId.split(":")[1],"Color");
		else if (userId.contains("zongdai"))
			sql = String.format("select %s, count(distinct ProductId) as OrderStyleNumber, sum(UserOrderNumber) as UserOrderNumber, count(distinct productid,color) as SKC, sum(UserOrderAmount) as UserOrderAmount "
					+ "from all_orders where Invalid=0 and InvalidPd=0 and  AreaId=%s group by %s", "right(Color,2) as Color",userId.split(":")[1],"Color");
		else if (userId.startsWith("area"))
			sql = String.format("select %s, count(distinct ProductId) as OrderStyleNumber, sum(UserOrderNumber) as UserOrderNumber, count(distinct productid,color) as SKC, sum(UserOrderAmount) as UserOrderAmount "
					+ "from all_orders where Invalid=0 and InvalidPd=0 and  SuperAreaId=%s group by %s", "right(Color,2) as Color",userId.split(":")[1],"Color");
		else
			sql = String.format("select %s, count(distinct ProductId) as OrderStyleNumber, sum(UserOrderNumber) as UserOrderNumber, count(distinct productid,color) as SKC, sum(UserOrderAmount) as UserOrderAmount "
					+ "from all_orders where Invalid=0 and InvalidPd=0 group by %s"
					,"right(Color,2) as Color","Color");
		JSONArray jsonArray = new JSONArray();  
		jsonArray = this.batchQuery(sql, jsonArray);
		return jsonArray;
    }
    
    @GET
    @Path("profitForcastByColor")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public JSONArray profitForcastByColor(@QueryParam("userId") String userId, @QueryParam("discount") String discount) throws JSONException {
		String sql;
		if (userId.contains("zongdai"))
			sql = String.format("select %s, sum(UserOrderNumber*ProductPrice*%s) as UserOrderAmount, sum(UserOrderAmount) as SellAmount "
					+ " from  all_orders where Invalid=0 and InvalidPd=0 and AreaId=%s   "
					+ " group by %s", "right(Color,2) as Color",discount,userId.split(":")[1],"Color");
		else 
			sql = String.format("select %s, sum(UserOrderNumber*ProductPrice*Discount) as UserOrderAmount, sum(UserOrderAmount)*%s as SellAmount "
					+ " from  (select * from all_orders where Invalid=0 and InvalidPd=0 and SuperAreaId=%s ) as a "
					+ " inner join (select Discount from user_info) as b on a.customerId=b.Id group by %s", "right(Color,2) as Color",discount,userId.split(":")[1],"Color");
		JSONArray jsonArray = new JSONArray();  
		jsonArray = this.batchQuery(sql, jsonArray);
		return jsonArray;
    }
    
    @GET
    @Path("profitForcastBySize")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public JSONArray profitForcastBySize(@QueryParam("userId") String userId, @QueryParam("discount") String discount) throws JSONException {
		String sql;
    	if (userId.contains("zongdai")){
			sql = String.format("select ProductPrice as SellPrice,(ProductPrice*%s/10) as OrderPrice, UserOrderDetails from ( select * from all_orders where Invalid=0 and InvalidPd=0 and  AreaId=%s) as a"
					, discount, userId.split(":")[1]);
		} else {
			sql = String.format("select (ProductPrice*Discount/10) as SellPrice,UserOrderDetails,(ProductPrice*%s/10) as OrderPrice from ( select * from all_orders where Invalid=0 and InvalidPd=0 and  SuperAreaId=%s) as a"
					+ " left join (select Discount from user_info) as b on a.customerId=b.Id ", discount, userId.split(":")[1]);
		} 
    	JSONArray jsonArray = new JSONArray();  
		jsonArray = this.batchQueryForProfitForcastBySize(sql, jsonArray);
		return jsonArray;
    }

    @GET
    @Path("profitForcastBySerial")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public JSONArray profitForcastBySerial(@QueryParam("userId") String userId, @QueryParam("discount") String discount) throws JSONException {
    	return this.profitForcastByWhich(userId, "ProductSerial", discount);
    }

    @GET
    @Path("profitForcastByType")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public JSONArray profitForcastByType(@QueryParam("userId") String userId, @QueryParam("discount") String discount) throws JSONException {
    	return this.profitForcastByWhich(userId, "ProductType", discount);
    }
    
    @GET
    @Path("profitForcastByClass")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public JSONArray profitForcastByClass(@QueryParam("userId") String userId, @QueryParam("discount") String discount) throws JSONException {
    	return this.profitForcastByWhich(userId, "ProductClass", discount);
    }

    @GET
    @Path("analyzeByAny")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public JSONObject analyzeOrderByAny(@QueryParam("userId") String userId,@QueryParam("byFirst") String byFirst,@QueryParam("bySecond") String bySecond) throws JSONException {
    	JSONObject jsonResult = new JSONObject();  
    	String sql,selectFirst=byFirst,selectSecond=bySecond;
    	if (byFirst.equals("Color"))
    		selectFirst = "right(Color,2) as Color";
    	else if (bySecond.equals("Color"))
    		selectSecond = "right(Color,2) as Color";

    	if (byFirst.equals("Size")) {
	    	if (userId.startsWith("zongjingli"))
				sql = String.format("select ProductPrice, UserOrderDetails, %s from all_orders where Invalid=0 and InvalidPd=0 ", selectSecond);
			else if (userId.contains("zongdai"))
				sql = String.format("select ProductPrice, UserOrderDetails, %s from all_orders where Invalid=0 and InvalidPd=0 and AreaId=%s  ", selectSecond, userId.split(":")[1]);
			else if (userId.contains("area"))
				sql = String.format("select ProductPrice, UserOrderDetails, %s from all_orders where Invalid=0 and InvalidPd=0 and SuperAreaId=%s  ", selectSecond, userId.split(":")[1]);
			else	
				sql = String.format("select ProductPrice, UserOrderDetails, %s from all_orders where Invalid=0 and InvalidPd=0 and CustomerId='%s' ", selectSecond,userId.split(":")[1]);
	    	jsonResult = this.batchQueryForAnalyzeByAnySize(sql, jsonResult, byFirst,bySecond);
    	} else if (bySecond.equals("Size")) {
	    	if (userId.startsWith("zongjingli"))
				sql = String.format("select ProductPrice, UserOrderDetails, %s from all_orders where Invalid=0 and InvalidPd=0 ", selectFirst);
			else if (userId.contains("zongdai"))
				sql = String.format("select ProductPrice, UserOrderDetails, %s from all_orders where Invalid=0 and InvalidPd=0 and AreaId=%s  ", selectFirst, userId.split(":")[1]);
			else if (userId.contains("area"))
				sql = String.format("select ProductPrice, UserOrderDetails, %s from all_orders where Invalid=0 and InvalidPd=0 and SuperAreaId=%s  ", selectFirst, userId.split(":")[1]);
			else	
				sql = String.format("select ProductPrice, UserOrderDetails, %s from all_orders where Invalid=0 and InvalidPd=0 and CustomerId='%s' ", selectFirst,userId.split(":")[1]);
		 
	    	jsonResult = this.batchQueryForAnalyzeByAnySize(sql, jsonResult, byFirst,bySecond);
    	} else {
	    	if (userId.startsWith("zongjingli"))
				sql = String.format("select %s, %s, count(distinct productid) as OrderStyleNumber, count(distinct productid,color) as SKC,  sum(userordernumber) as OrderProductNumber, sum(userOrderAmount) as OrderAmount "
						+ "from all_orders where Invalid=0 and InvalidPd=0 group by %s,%s", selectFirst,selectSecond, byFirst,bySecond);
			else if (userId.contains("zongdai"))
				sql = String.format("select %s, %s,count(distinct productid) as OrderStyleNumber,  count(distinct productid,color) as SKC, sum(userordernumber) as OrderProductNumber, sum(userOrderAmount) as OrderAmount "
						+ "from all_orders where Invalid=0 and InvalidPd=0 and AreaId=%s group by %s,%s", selectFirst,selectSecond, userId.split(":")[1], byFirst,bySecond);
			else if (userId.contains("area"))
				sql = String.format("select %s, %s,count(distinct productid) as OrderStyleNumber,  count(distinct productid,color) as SKC, sum(userordernumber) as OrderProductNumber, sum(userOrderAmount) as OrderAmount "
						+ "from all_orders where Invalid=0 and InvalidPd=0 and SuperAreaId=%s group by %s,%s", selectFirst,selectSecond, userId.split(":")[1], byFirst,bySecond);
			else	
				sql = String.format("select %s,%s, count(distinct productid) as OrderStyleNumber, count(distinct productid,color) as SKC,  sum(userordernumber) as OrderProductNumber, sum(userOrderAmount) as OrderAmount  "
						+ "from all_orders where Invalid=0 and InvalidPd=0 and CustomerId='%s' "
						+ " group by %s,%s", selectFirst,selectSecond,userId.split(":")[1], byFirst,bySecond);
			jsonResult = this.batchQueryForAnalyzeByAny(sql, jsonResult, byFirst,bySecond);
    	}
		return jsonResult;
    }
    
    private JSONArray analyzeOrderByWhich(String userId, String byWhich){
		String sql;
    	if (userId.startsWith("diancang:"))
			sql = String.format("select %s, count(distinct ProductId) as OrderStyleNumber, count(distinct productid,color) as SKC, sum(UserOrderNumber) as UserOrderNumber, sum(UserOrderAmount) as UserOrderAmount from all_orders where Invalid=0 and InvalidPd=0 and CustomerId=%s group by %s"
					,byWhich,userId.split(":")[1],byWhich);
		else if (userId.contains("zongdai"))
			sql = String.format("select %s, count(distinct ProductId) as OrderStyleNumber, sum(UserOrderNumber) as UserOrderNumber, count(distinct productid,color) as SKC, sum(UserOrderAmount) as UserOrderAmount "
					+ "from all_orders where Invalid=0 and InvalidPd=0 and  AreaId=%s group by %s", byWhich,userId.split(":")[1],byWhich);
		else if (userId.startsWith("area"))
			sql = String.format("select %s, count(distinct ProductId) as OrderStyleNumber, sum(UserOrderNumber) as UserOrderNumber, count(distinct productid,color) as SKC, sum(UserOrderAmount) as UserOrderAmount "
					+ "from all_orders where Invalid=0 and InvalidPd=0 and  SuperAreaId=%s group by %s", byWhich,userId.split(":")[1],byWhich);
		else
			sql = String.format("select %s, count(distinct ProductId) as OrderStyleNumber, sum(UserOrderNumber) as UserOrderNumber, count(distinct productid,color) as SKC, sum(UserOrderAmount) as UserOrderAmount "
					+ "from all_orders where Invalid=0 and InvalidPd=0 group by %s"
					,byWhich,byWhich);
		JSONArray jsonArray = new JSONArray();  
		jsonArray = this.batchQuery(sql, jsonArray);
		return jsonArray;
    }

	private JSONArray profitForcastByWhich(String userId, String byWhich, String discount) {
		String sql;
		if (userId.contains("zongdai"))
			sql = String.format("select %s, sum(UserOrderNumber*ProductPrice*%s/10) as UserOrderAmount, sum(UserOrderAmount) as SellAmount "
					+ " from  all_orders where Invalid=0 and InvalidPd=0 and AreaId=%s   "
					+ " group by %s", byWhich,discount,userId.split(":")[1],byWhich);
		else 
			sql = String.format("select %s, sum(UserOrderNumber*ProductPrice*Discount/10) as UserOrderAmount, sum(UserOrderAmount)*%s/10 as SellAmount "
					+ " from  (select * from all_orders where Invalid=0 and InvalidPd=0 and SuperAreaId=%s ) as a "
					+ " inner join (select Discount from user_info) as b on a.customerId=b.Id group by %s", byWhich,discount,userId.split(":")[1],byWhich);
		JSONArray jsonArray = new JSONArray();  
		jsonArray = this.batchQuery(sql, jsonArray);
		return jsonArray;
    }
   
    private JSONArray batchQuery(String sql, JSONArray jsonArray) {
    	System.out.println(sql);
    	DBConnPool pool = new DBConnPool();
		try {
			ResultSet rs = pool.stmt.executeQuery(sql);  
			jsonArray = this.resultSetToJson(rs, jsonArray);
		} catch (SQLException | JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {  
	    	try {
	    		if (pool != null ){
	    			try {
			    		if (pool.stmt!= null&& !pool.stmt.isClosed()) 
			    		{
			    			pool.stmt.close();
			    			pool.stmt = null;
			    		}
	    			} catch (SQLException e) {  
	                    e.printStackTrace();  
	                }
		    		if (pool.conn!=null && !pool.conn.isClosed()) 
		    		{
		    			pool.conn.close();
		    			pool.conn = null;
		    		}
	    		} 
	        } catch (SQLException e) {  
                e.printStackTrace();  
            }  
	    }
		return jsonArray;
    }

    private JSONArray batchQueryForZongDaiOrders(String sql, JSONArray jsonArray) {
    	System.out.println(sql);
    	DBConnPool pool = new DBConnPool();
		try {
			ResultSet rs = pool.stmt.executeQuery(sql);  
			jsonArray = this.resultSetToJsonForZongDaiOrders(rs, jsonArray);
		} catch (SQLException | JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {  
	    	try {
	    		if (pool != null ){
	    			try {
			    		if (pool.stmt!= null&& !pool.stmt.isClosed()) 
			    		{
			    			pool.stmt.close();
			    			pool.stmt = null;
			    		}
	    			} catch (SQLException e) {  
	                    e.printStackTrace();  
	                }
		    		if (pool.conn!=null && !pool.conn.isClosed()) 
		    		{
		    			pool.conn.close();
		    			pool.conn = null;
		    		}
	    		} 
	        } catch (SQLException e) {  
                e.printStackTrace();  
            }  
	    }
		return jsonArray;
    }

//    private JSONArray batchQueryForDiancangRecords(String sql, JSONArray jsonArray) {
//    	System.out.println(sql);
//    	DBConnPool pool = new DBConnPool();
//		try {
//			ResultSet rs = pool.stmt.executeQuery(sql);  
//			jsonArray = this.resultSetToJsonForDiancangRecords(rs, jsonArray);
//		} catch (SQLException | JSONException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} finally {  
//	    	try {  
//	    		if (!pool.stmt.isClosed()) 
//	    		{
//	    			pool.stmt.close();
//	    		}
//	    		if (!pool.conn.isClosed()) 
//	    		{
//	    			pool.conn.close();
//	    		}
//	        } catch (SQLException e) {  
//                e.printStackTrace();  
//            }  
//	    }
//		return jsonArray;
//    }
//
//
//
//    private JSONArray resultSetToJsonForDiancangRecords(ResultSet rs, JSONArray jsonArray) throws SQLException,JSONException{  
//    	while (rs.next()) {
//    		String[] orderList = rs.getString("UserOrderDetails").split("\\|\\|");
//    		Map<String, String> colorSet = new HashMap<String, String>();
//    		String colorKey;
//    		for (int i=0;i<orderList.length;i++) {
//        		colorKey = orderList[i].split(",")[0];
//        		if (colorSet.containsKey(colorKey))
//        			colorSet.put(colorKey, colorSet.get(colorKey)+","+orderList[i].split(",")[1]);
//        		else
//        			colorSet.put(colorKey, orderList[i].split(",")[1]);
//    		}
//	    	Object[] keys =  colorSet.keySet().toArray();   
//    		JSONObject jsonObj = new JSONObject();
//    		jsonObj.put("ProductId", rs.getString("ProductId"));
//    		jsonObj.put("ProductCode", rs.getString("ProductCode"));
//    		jsonObj.put("ProductName", rs.getString("ProductName"));
//    		jsonObj.put("OrderNumber", rs.getString("OrderNumber"));
//    		jsonObj.put("ProductPrice", rs.getString("ProductPrice"));
//    		jsonObj.put("OrderAmount", rs.getString("OrderAmount"));
//    		String colors = "";
//    		String details = "";
//	    	for(int i = 0; i<keys.length; i++)  
//	    	{    
//	    		colors += keys[i] + "\n";
//	    		details += colorSet.get(keys[i])+"\n";
//	    	}
//	    	jsonObj.put("ProductSerial", colors);
//	    	jsonObj.put("SKC", details);
//    		jsonArray.put(jsonObj);
//    	}
//	    	
//    	return jsonArray;  
//    } 

    private JSONArray batchQueryForAnalyzeBySize(String sql, JSONArray jsonArray) {
    	System.out.println(sql);
    	DBConnPool pool = new DBConnPool();
		try {
			ResultSet rs = pool.stmt.executeQuery(sql);  
			jsonArray = this.resultSetToJsonForAnalyzeBySize(rs, jsonArray);
		} catch (SQLException | JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {  
	    	try {
	    		if (pool != null ){
	    			try {
			    		if (pool.stmt!= null&& !pool.stmt.isClosed()) 
			    		{
			    			pool.stmt.close();
			    			pool.stmt = null;
			    		}
	    			} catch (SQLException e) {  
	                    e.printStackTrace();  
	                }
		    		if (pool.conn!=null && !pool.conn.isClosed()) 
		    		{
		    			pool.conn.close();
		    			pool.conn = null;
		    		}
	    		} 
	        } catch (SQLException e) {  
                e.printStackTrace();  
            }  
	    }
		return jsonArray;
    }
    
    private JSONArray batchQueryForProfitForcastBySize(String sql, JSONArray jsonArray) {
    	System.out.println(sql);
    	DBConnPool pool = new DBConnPool();
		try {
			ResultSet rs = pool.stmt.executeQuery(sql);  
			jsonArray = this.resultSetToJsonForProfitForcastBySize(rs, jsonArray);
		} catch (SQLException | JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {  
	    	try {
	    		if (pool != null ){
	    			try {
			    		if (pool.stmt!= null&& !pool.stmt.isClosed()) 
			    		{
			    			pool.stmt.close();
			    			pool.stmt = null;
			    		}
	    			} catch (SQLException e) {  
	                    e.printStackTrace();  
	                }
		    		if (pool.conn!=null && !pool.conn.isClosed()) 
		    		{
		    			pool.conn.close();
		    			pool.conn = null;
		    		}
	    		} 
	        } catch (SQLException e) {  
                e.printStackTrace();  
            }  
	    }
		return jsonArray;
    }
    private JSONObject batchQueryForAnalyzeByAny(String sql, JSONObject jsonResult, String byFirst, String bySecond) {
    	System.out.println(sql);
    	DBConnPool pool = new DBConnPool();
		try {
			ResultSet rs = pool.stmt.executeQuery(sql);  
			jsonResult = this.resultSetToJsonForAnalyzeByAny(rs, jsonResult, byFirst, bySecond);
		} catch (SQLException | JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {  
	    	try {
	    		if (pool != null ){
	    			try {
			    		if (pool.stmt!= null&& !pool.stmt.isClosed()) 
			    		{
			    			pool.stmt.close();
			    			pool.stmt = null;
			    		}
	    			} catch (SQLException e) {  
	                    e.printStackTrace();  
	                }
		    		if (pool.conn!=null && !pool.conn.isClosed()) 
		    		{
		    			pool.conn.close();
		    			pool.conn = null;
		    		}
	    		} 
	        } catch (SQLException e) {  
                e.printStackTrace();  
            }  
	    }
		return jsonResult;
    }
    
    private  JSONObject batchQueryForAnalyzeByAnySize(String sql, JSONObject jsonResult, String byFirst, String bySecond) {
    	System.out.println(sql);
    	DBConnPool pool = new DBConnPool();
		try {
			ResultSet rs = pool.stmt.executeQuery(sql);
			jsonResult = this.resultSetToJsonForAnalyzeByAnySize(rs, jsonResult, byFirst, bySecond);
		} catch (SQLException | JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally { 
	    	try {
	    		if (pool != null ){
	    			try {
			    		if (pool.stmt!= null&& !pool.stmt.isClosed()) 
			    		{
			    			pool.stmt.close();
			    			pool.stmt = null;
			    		}
	    			} catch (SQLException e) {  
	                    e.printStackTrace();  
	                }
		    		if (pool.conn!=null && !pool.conn.isClosed()) 
		    		{
		    			pool.conn.close();
		    			pool.conn = null;
		    		}
	    		} 
	        } catch (SQLException e) {  
                e.printStackTrace();  
            }  
		}
		return jsonResult;
    }
    
    private JSONObject batchQueryForChart(String sql, JSONObject chartJson, String byFirst, String bySecond) {
    	System.out.println(sql);
    	DBConnPool pool = new DBConnPool();
		try {
			ResultSet rs = pool.stmt.executeQuery(sql);  
			chartJson = this.resultSetToJsonForChart(rs, chartJson, byFirst, bySecond);
		} catch (SQLException | JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {  
	    	try {
	    		if (pool != null ){
	    			try {
			    		if (pool.stmt!= null&& !pool.stmt.isClosed()) 
			    		{
			    			pool.stmt.close();
			    			pool.stmt = null;
			    		}
	    			} catch (SQLException e) {  
	                    e.printStackTrace();  
	                }
		    		if (pool.conn!=null && !pool.conn.isClosed()) 
		    		{
		    			pool.conn.close();
		    			pool.conn = null;
		    		}
	    		} 
	        } catch (SQLException e) {  
                e.printStackTrace();  
            }  
	    }
		return chartJson;
    }
    
    private JSONObject batchQueryForChartSize(String sql, JSONObject chartJson, String byFirst, String bySecond) {
    	System.out.println(sql);
    	DBConnPool pool = new DBConnPool();
		try {
			ResultSet rs = pool.stmt.executeQuery(sql);  
			ArrayList<JSONObject> arr = new ArrayList<JSONObject>();
			arr = this.resultSetToJsonForAnalyzeByAnySizeChart(rs, arr, byFirst, bySecond);
			chartJson = this.resultArrayToJsonForChart(arr, chartJson, byFirst, bySecond);
		} catch (SQLException | JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {  
	    	try {
	    		if (pool != null ){
	    			try {
			    		if (pool.stmt!= null&& !pool.stmt.isClosed()) 
			    		{
			    			pool.stmt.close();
			    			pool.stmt = null;
			    		}
	    			} catch (SQLException e) {  
	                    e.printStackTrace();  
	                }
		    		if (pool.conn!=null && !pool.conn.isClosed()) 
		    		{
		    			pool.conn.close();
		    			pool.conn = null;
		    		}
	    		} 
	        } catch (SQLException e) {  
                e.printStackTrace();  
            }  
	    }
		return chartJson;
    }

    private JSONArray resultSetToJsonForAnalyzeBySize(ResultSet rs, JSONArray jsonArray) throws SQLException,JSONException  
    {  
    	Map<String, Integer> sizeNumberMap = new HashMap<String, Integer>();
    	Map<String, Integer> sizeAmountMap = new HashMap<String, Integer>();
    	
    	while (rs.next()) {  
    		//S:10,M:10,L:20
    		String[] sizeList = rs.getString("UserOrderDetails").split(",");
    		int price = rs.getInt("ProductPrice");
    		for (int i = 0; i < sizeList.length; i++) {  
    			String size = sizeList[i].split(":")[0]; 
    			int orderNumber =  Integer.valueOf(sizeList[i].split(":")[1]);
    			if (sizeNumberMap.containsKey(size)) {
    				sizeNumberMap.put(size,sizeNumberMap.get(size)+orderNumber);
    				sizeAmountMap.put(size,sizeAmountMap.get(size)+orderNumber*price);
    			} else {
    				sizeNumberMap.put(size,orderNumber);
    				sizeAmountMap.put(size,orderNumber*price);
    			}
    		}   
    	}
    	Iterator iter = sizeNumberMap.entrySet().iterator(); 
    	DBConnPool pool = new DBConnPool();
    	ResultSet rs1;
    	while (iter.hasNext()) { 
    	    Map.Entry entry = (Map.Entry) iter.next(); 
    	    String key = entry.getKey().toString(); 
    	    String val = entry.getValue().toString();
    		rs1 = pool.stmt.executeQuery("select count(distinct ProductId) as OrderStyleNumber from all_orders where Invalid=0 and InvalidPd=0 and (UserOrderDetails like '%,"+key+":%' or  UserOrderDetails like '"+key+":%')");
    	    rs1.next();
    	    JSONObject jsonObj=new JSONObject();
    	    jsonObj.put("Size", key.toString());
    	    jsonObj.put("OrderStyleNumber", rs1.getString("OrderStyleNumber"));
    	    jsonObj.put("UserOrderNumber", val);
    	    jsonObj.put("SKC", val);
    	    jsonObj.put("UserOrderAmount", sizeAmountMap.get(key));
    	    jsonArray.put(jsonObj);
    	}
    	return jsonArray;
    }   

    private JSONArray resultSetToJsonForProfitForcastBySize(ResultSet rs, JSONArray jsonArray) throws SQLException,JSONException  
    {  
    	Map<String, Integer> sizeSellAmountMap = new HashMap<String, Integer>();
    	Map<String, Integer> sizeOrderAmountMap = new HashMap<String, Integer>();
    	String[] sizeList;
    	String size;
    	int price,tagPrice,orderNumber;
    	//S:10,M:10
    	while (rs.next()) {  
    		sizeList = rs.getString("UserOrderDetails").split(",");
    		price = rs.getInt("OrderPrice");
    		tagPrice = rs.getInt("SellPrice");
    		for (int i = 0; i < sizeList.length; i++) {  
    			size = sizeList[i].split(":")[0]; 
    			orderNumber =  Integer.valueOf(sizeList[i].split(":")[1]);
    			if (sizeSellAmountMap.containsKey(size)) {
    				sizeSellAmountMap.put(size,sizeSellAmountMap.get(size)+orderNumber*tagPrice);
    				sizeOrderAmountMap.put(size,sizeOrderAmountMap.get(size)+orderNumber*price);
    			} else {
    				sizeSellAmountMap.put(size,orderNumber*tagPrice);
    				sizeOrderAmountMap.put(size,orderNumber*price);
    			}
    		}   
    	}
    	Iterator iter = sizeSellAmountMap.entrySet().iterator(); 
    	Map.Entry entry;
    	while (iter.hasNext()) { 
    	    entry = (Map.Entry) iter.next(); 
    	    Object key = entry.getKey(); 
    	    Object val = entry.getValue();
    	    JSONObject jsonObj=new JSONObject();
    	    jsonObj.put("Size", key);
    	    jsonObj.put("SellAmount", val);
    	    jsonObj.put("UserOrderAmount", sizeOrderAmountMap.get(key));
    	    jsonArray.put(jsonObj);
    	}
    	return jsonArray;
    }   
    
    private JSONObject resultSetToJsonForChart(ResultSet rs, JSONObject chartJson, String byFirst, String bySecond) throws SQLException,JSONException  
    {  
      	
      	//init multi-level category
    	List<String> firstCatKey = new ArrayList<String>();
    	String firstKey;
    	String curFirstCatKey = "";
    	int curFirstCatValue = 0;
    	JSONArray firstCatArray = new JSONArray();
    	JSONArray curSecondCatArray = new JSONArray();
    	while (rs.next()) {
    		JSONObject curSecondItem = new JSONObject();
    		curSecondItem.put("label",rs.getString(bySecond));
    		curSecondItem.put("value",rs.getInt("OrderAmount"));
    		firstKey = rs.getString(byFirst);  
    		if (!firstCatKey.contains(firstKey)){
    			// meet new firstKey
    			if (curFirstCatValue!=0){
    				//sum former cat value
    				JSONObject curFirstCat = new JSONObject();
    				curFirstCat.put("label", curFirstCatKey);
    				curFirstCat.put("value",curFirstCatValue);
    				curFirstCat.put("category", curSecondCatArray);
    				firstCatArray.put(curFirstCat);
    			}
    			curFirstCatKey = firstKey;
    			firstCatKey.add(firstKey);
    			curFirstCatValue = rs.getInt("OrderAmount");
    			curSecondCatArray = new JSONArray();
    			curSecondCatArray.put(curSecondItem);
    		} else {
    			curFirstCatValue += rs.getInt("OrderAmount");
    			curSecondCatArray.put(curSecondItem);
    		}
    	}
    	if (curFirstCatValue!=0){
			JSONObject curFirstCat = new JSONObject();
			curFirstCat.put("label", curFirstCatKey);
			curFirstCat.put("value",curFirstCatValue);
			curFirstCat.put("category", curSecondCatArray);
			firstCatArray.put(curFirstCat);
	        // init chart info
	      	JSONObject chartInfo = new JSONObject();
	      	chartInfo.put("palette", 2);
	      	chartInfo.put( "piefillalpha", 44);
	      	chartInfo.put("pieborderthickness", 1);
	      	chartInfo.put("bgcolor", "FFFFFF");
	      	chartInfo.put("piebordercolor", "FFFFFF");
	      	chartInfo.put("basefontsize", "9");
	      	chartInfo.put("usehovercolor", "1");
	      	chartInfo.put("caption", "汇总");
	      	chartJson.put("chart", chartInfo);
	      	chartJson.put("category", firstCatArray);
    	}
    	return chartJson;
    }   
    
    private JSONObject resultArrayToJsonForChart(ArrayList<JSONObject> jsList, JSONObject chartJson, String byFirst, String bySecond) throws SQLException,JSONException  
    {  
      	
      	//init multi-level category
    	List<String> firstCatKey = new ArrayList<String>();
    	String firstKey;
    	String curFirstCatKey = "";
    	int curFirstCatValue = 0;
    	JSONArray firstCatArray = new JSONArray();
    	JSONArray curSecondCatArray = new JSONArray();
    	Iterator<JSONObject> it=jsList.iterator();
    	while(it.hasNext()){
    		JSONObject js= it.next();
    		JSONObject curSecondItem = new JSONObject();
    		curSecondItem.put("label",js.getString(bySecond));
    		curSecondItem.put("value",js.getInt("OrderAmount"));
    		firstKey = js.getString(byFirst);  
    		if (!firstCatKey.contains(firstKey)){
    			// meet new firstKey
    			if (curFirstCatValue!=0){
    				//sum former cat value
    				JSONObject curFirstCat = new JSONObject();
    				curFirstCat.put("label", curFirstCatKey);
    				curFirstCat.put("value",curFirstCatValue);
    				curFirstCat.put("category", curSecondCatArray);
    				firstCatArray.put(curFirstCat);
    			}
    			curFirstCatKey = firstKey;
    			firstCatKey.add(firstKey);
    			curFirstCatValue = js.getInt("OrderAmount");
    			curSecondCatArray = new JSONArray();
    			curSecondCatArray.put(curSecondItem);
    		} else {
    			curFirstCatValue += js.getInt("OrderAmount");
    			curSecondCatArray.put(curSecondItem);
    		}
    	}
    	if (curFirstCatValue!=0){
			JSONObject curFirstCat = new JSONObject();
			curFirstCat.put("label", curFirstCatKey);
			curFirstCat.put("value",curFirstCatValue);
			curFirstCat.put("category", curSecondCatArray);
			firstCatArray.put(curFirstCat);
	        // init chart info
	      	JSONObject chartInfo = new JSONObject();
	      	chartInfo.put("palette", 2);
	      	chartInfo.put( "piefillalpha", 44);
	      	chartInfo.put("pieborderthickness", 1);
	      	chartInfo.put("bgcolor", "FFFFFF");
	      	chartInfo.put("piebordercolor", "FFFFFF");
	      	chartInfo.put("basefontsize", "9");
	      	chartInfo.put("usehovercolor", "1");
	      	chartInfo.put("caption", "汇总");
	      	chartJson.put("chart", chartInfo);
	      	chartJson.put("category", firstCatArray);
    	}
    	return chartJson;
    }   

    private JSONObject singleQuery(String sql, JSONObject jsonObj) {
    	System.out.println(sql);
    	DBConnPool pool = new DBConnPool();
		try {
			ResultSet rs = pool.stmt.executeQuery(sql);  
			if (rs.next())
				jsonObj = this.resultToJson(rs, jsonObj);
		} catch (SQLException | JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {  
	    	try {  
	    		if (!pool.stmt.isClosed()) 
	    		{
	    			pool.stmt.close();
	    		}
	    		if (!pool.conn.isClosed()) 
	    		{
	    			pool.conn.close();
	    		}
	        } catch (SQLException e) {  
                e.printStackTrace();  
            }  
	    }
		return jsonObj;
    }
  
    

    private JSONObject resultSetToJsonForAnalyzeByAny(ResultSet rs, JSONObject jsonResult, String byFirst, String bySecond) throws SQLException,JSONException  
    {  
    	ResultSetMetaData metaData = rs.getMetaData();  
    	int columnCount = metaData.getColumnCount();  
    	JSONArray jsonArray = new JSONArray();
    	List<String> secondCat = new ArrayList<String>();
    	List<String> firstCat = new ArrayList<String>();
//    	Map<String, String> firstMap = new HashMap<String, String>();
    	while (rs.next()) {
    		JSONObject jsonObj = new JSONObject();  
    		for (int i = 1; i <= columnCount; i++) {  
    			String columnName =metaData.getColumnLabel(i);  
    			String value = rs.getString(columnName);  
    			jsonObj.put(columnName, value);  
    		}
    		jsonArray.put(jsonObj);   
    		if (!secondCat.contains(jsonObj.getString(bySecond))){
    			secondCat.add(jsonObj.getString(bySecond));
    		}
    		if (!firstCat.contains(jsonObj.getString(byFirst))){
    			firstCat.add(jsonObj.getString(byFirst));
    		}
    	}
    	jsonResult.put("firstOrder", firstCat);
    	jsonResult.put("secondOrder", secondCat);
    	jsonResult.put("resultArray", jsonArray);
    	return jsonResult;
    }   
    
    private JSONObject resultSetToJsonForAnalyzeByAnySize(ResultSet rs, JSONObject jsonResult, String byFirst, String bySecond) throws SQLException,JSONException  
    {  
    	JSONArray jsonArray = new JSONArray();
    	List<String> secondCat = new ArrayList<String>();
    	List<String> firstCat = new ArrayList<String>();
    	Map<String, Integer> analyzeKeyNumberMap = new HashMap<String, Integer>();
    	Map<String, Integer> analyzeKeyAmountMap = new HashMap<String, Integer>();
		int otherIndex,sizeIndex;
		String otherKey,sizeKey,sql;
		if (byFirst.equals("Size")){
			otherIndex = 1;
			sizeIndex = 0;
			otherKey = bySecond;
		} else {
			otherIndex = 0;
			sizeIndex = 1;
			otherKey = byFirst;
		}
		String[] orderList;
		int price,orderNumber;
		String otherKeyValue,analyzeSizeKey,key;
    	while (rs.next()) {
    		orderList = rs.getString("UserOrderDetails").split(",");
    		price = rs.getInt("ProductPrice");
    		otherKeyValue = rs.getString(otherKey);
    		for (int i = 0; i < orderList.length; i++) {  
    			analyzeSizeKey = orderList[i].split(":")[0]; //0:color,1:size
    			orderNumber =  Integer.valueOf(orderList[i].split(":")[1]);
    			if (otherIndex==0) {
    				key = otherKeyValue+","+analyzeSizeKey;
    				if(!firstCat.contains(rs.getString(otherKey)))
    					firstCat.add(rs.getString(otherKey));
	        		if (!secondCat.contains(analyzeSizeKey))
	        			secondCat.add(analyzeSizeKey);
        		} else {
        			key = analyzeSizeKey +"," +otherKeyValue;
    				if(!secondCat.contains(rs.getString(otherKey)))
    					secondCat.add(rs.getString(otherKey));
	        		if (!firstCat.contains(analyzeSizeKey))
	        			firstCat.add(analyzeSizeKey);
        		}
        			
    			if (analyzeKeyNumberMap.containsKey(key)) {
    				analyzeKeyNumberMap.put(key,analyzeKeyNumberMap.get(key)+orderNumber);
    				analyzeKeyAmountMap.put(key,analyzeKeyAmountMap.get(key)+orderNumber*price);
    			} else {
    				analyzeKeyNumberMap.put(key,orderNumber);
    				analyzeKeyAmountMap.put(key,orderNumber*price);
    			}
    		}
		}
    	Object[] keys =  analyzeKeyNumberMap.keySet().toArray();    
    	Arrays.sort(keys);
    	DBConnPool pool = new DBConnPool();
    	ResultSet rs1;
    	for(int i = 0; i<keys.length; i++)  
    	{    
    		key = keys[i].toString();
    		sizeKey = key.split(",")[sizeIndex];
    		JSONObject jsonObj=new JSONObject();
    		jsonObj.put(byFirst, key.split(",")[0]);
    		jsonObj.put(bySecond, key.split(",")[1]);
    		sql = String.format("select count(distinct ProductId) as OrderStyleNumber from all_orders where Invalid=0 and InvalidPd=0 and "
    				+ " %s='%s' and (UserOrderDetails like '%%,%s:%%' or  UserOrderDetails like '%s:%%')", otherKey,key.split(",")[otherIndex],sizeKey,sizeKey);
    		System.out.println(sql);
    		rs1 = pool.stmt.executeQuery(sql);
    		rs1.next();
    		jsonObj.put("OrderStyleNumber", rs1.getString("OrderStyleNumber"));
    		jsonObj.put("OrderProductNumber", analyzeKeyNumberMap.get(key));
    		jsonObj.put("OrderAmount", analyzeKeyAmountMap.get(key));
    		jsonObj.put("SKC", analyzeKeyNumberMap.get(key));
    		jsonArray.put(jsonObj);
    	} 
    	jsonResult.put("firstOrder", firstCat);
    	jsonResult.put("secondOrder", secondCat);
    	jsonResult.put("resultArray", jsonArray);

    	return jsonResult;
    }


    private ArrayList<JSONObject> resultSetToJsonForAnalyzeByAnySizeChart(ResultSet rs, ArrayList<JSONObject> jsonResult, String byFirst, String bySecond) throws SQLException,JSONException  
    {  
    	Map<String, Integer> analyzeKeyAmountMap = new HashMap<String, Integer>();
			
		int otherIndex;
		String otherKey;
		if (byFirst.equals("Size")){
			otherIndex = 2;
			otherKey = bySecond;
		} else {
			otherIndex=1;
			otherKey=byFirst;
		}
		String[] orderList;
		int price, orderNumber;
		String otherKeyValue, key;
    	while (rs.next()) {
    		orderList = rs.getString("UserOrderDetails").split(",");
    		price = rs.getInt("ProductPrice");
    		otherKeyValue = rs.getString(otherKey);
    		for (int i = 0; i < orderList.length; i++) {  
    			String analyzeSizeKey = orderList[i].split(":")[0]; //0:color,1:size
    			orderNumber =  Integer.valueOf(orderList[i].split(":")[1]);
    			if (otherIndex==1)
    				key = otherKeyValue+","+analyzeSizeKey;
    			else
    				key = analyzeSizeKey +"," +otherKeyValue;
    			if (analyzeKeyAmountMap.containsKey(key)) {
    				analyzeKeyAmountMap.put(key,analyzeKeyAmountMap.get(key)+orderNumber*price);
    			} else {
    				analyzeKeyAmountMap.put(key,orderNumber*price);
    			}
    		}
    	}
    	Object[] keys =  analyzeKeyAmountMap.keySet().toArray();    
    	Arrays.sort(keys);  
    	for(int i = 0; i<keys.length; i++)  
    	{   
    		key = keys[i].toString();
    	    JSONObject jsonObj=new JSONObject();
	    	jsonObj.put(byFirst, key.split(",")[0]);
	    	jsonObj.put(bySecond, key.split(",")[1]);
    	    jsonObj.put("OrderAmount", analyzeKeyAmountMap.get(keys[i]));
    	    jsonResult.add(jsonObj);
    	} 
    	
    	return jsonResult;
    }

    private JSONArray resultSetToJsonForZongDaiOrders(ResultSet rs, JSONArray jsonArray) throws SQLException,JSONException  
    {  
    	ResultSetMetaData metaData = rs.getMetaData();  
    	int columnCount = metaData.getColumnCount();  
    	while (rs.next()) {  
    		JSONObject jsonObj = new JSONObject();  
    		for (int i = 1; i <= columnCount; i++) {  
    			String columnName =metaData.getColumnLabel(i); 
    			if (!columnName.equals("ColorOrderDetail")){
    				String value = rs.getString(columnName);  
        			jsonObj.put(columnName, value);
    			} else {
    				String colorOrderDetailStr = "";
//    				S:2,M:2,L:2,XL:2;S:2,M:2,L:2,XL:2<br>S:2,M:2,L:2,XL:2;S:2,M:2,L:2,XL:2 change to S:4,M:4,L:4,XL:4<br>S:4,M:4,L:4,XL:4 
    				String[] orderList = rs.getString("ColorOrderDetail").split("<br>");
    	    		for (int j=0;j<orderList.length;j++) {
//    	    			S:2,M:2,L:2,XL:2;S:2,M:2,L:2,XL:2
    	        		String[] sizeList = orderList[j].split(";");
    	        		int sNumber =0;
    	        		int lNumber =0;
    	        		int xlNumber =0;
    	        		int mNumber =0;
    	        		int x34Number = 0;
    	        		int x35Number = 0;
    	        		int x36Number = 0;
    	        		int x37Number = 0;
    	        		int x38Number = 0;
    	        		int x39Number = 0;
    	        		int x40Number = 0;
    					for (int k=0;k<sizeList.length;k++){
    						System.out.println(sizeList[k]);
    						if (sizeList[k].contains("S"))
								sNumber += Integer.valueOf(sizeList[k].split("S:")[1].split(",")[0]);
    						if (sizeList[k].contains("M"))
								mNumber += Integer.valueOf(sizeList[k].split("M:")[1].split(",")[0]);
							if (sizeList[k].contains("XL"))
								xlNumber += Integer.valueOf(sizeList[k].split("XL:")[1].split(",")[0]);
							if (sizeList[k].contains(",L:"))
								lNumber += Integer.valueOf(sizeList[k].split(",L:")[1].split(",")[0]);
							else if (sizeList[k].startsWith("L:"))
								lNumber += Integer.valueOf(sizeList[k].split("L:")[1].split(",")[0]);
							if (sizeList[k].contains("34:"))
								x34Number += Integer.valueOf(sizeList[k].split("34:")[1].split(",")[0]);
							if (sizeList[k].contains("35:"))
								x35Number += Integer.valueOf(sizeList[k].split("35:")[1].split(",")[0]);
							if (sizeList[k].contains("36:"))
								x36Number += Integer.valueOf(sizeList[k].split("36:")[1].split(",")[0]);
							if (sizeList[k].contains("37:"))
								x37Number += Integer.valueOf(sizeList[k].split("37:")[1].split(",")[0]);
							if (sizeList[k].contains("38:"))
								x38Number += Integer.valueOf(sizeList[k].split("38:")[1].split(",")[0]);
							if (sizeList[k].contains("39:"))
								x39Number += Integer.valueOf(sizeList[k].split("39:")[1].split(",")[0]);
							if (sizeList[k].contains("40:"))
								x40Number += Integer.valueOf(sizeList[k].split("40:")[1].split(",")[0]);
    					}
    					String sizeSumStr = "";
    					if (sNumber != 0 ) 
    						sizeSumStr += "S:"+sNumber;
    					if (mNumber != 0 )
    						sizeSumStr += ",M:"+mNumber;
    					if (lNumber != 0 ) 
    						sizeSumStr += ",L:"+lNumber;
    					if (xlNumber != 0 ) 
    						sizeSumStr += ",XL:"+xlNumber;
    					if (x34Number != 0 ) 
    						sizeSumStr += "34:"+x34Number;
    					if (x35Number != 0 ) 
    						sizeSumStr += ",35:"+x35Number;
    					if (x36Number != 0 ) 
    						sizeSumStr += ",36:"+x36Number;
    					if (x37Number != 0 ) 
    						sizeSumStr += ",37:"+x37Number;
    					if (x38Number != 0 ) 
    						sizeSumStr += ",38:"+x38Number;
    					if (x39Number != 0 ) 
    						sizeSumStr += ",39:"+x39Number;
    					if (x40Number != 0 ) 
    						sizeSumStr += ",40:"+x40Number;
    					
    					if (sizeSumStr.startsWith(","))
    						sizeSumStr = sizeSumStr.replaceFirst(",","");
    					colorOrderDetailStr += "<br>"+sizeSumStr ;
    	    		}
    	    		System.out.println(colorOrderDetailStr);
    	    		jsonObj.put(columnName, colorOrderDetailStr.replaceFirst("<br>", ""));
    			}  
    		}   
    		jsonArray.put(jsonObj);   
    	}  
    	return jsonArray;
    }   
    
    private JSONArray resultSetToJson(ResultSet rs, JSONArray jsonArray) throws SQLException,JSONException  
    {  
    	ResultSetMetaData metaData = rs.getMetaData();  
    	int columnCount = metaData.getColumnCount();  
    	while (rs.next()) {  
    		JSONObject jsonObj = new JSONObject();  
    		for (int i = 1; i <= columnCount; i++) {  
    			String columnName =metaData.getColumnLabel(i);  
    			String value = rs.getString(columnName);  
    			jsonObj.put(columnName, value);  
    		}   
    		jsonArray.put(jsonObj);   
    	}  
    	return jsonArray;
    }   

    private JSONObject resultToJson(ResultSet rs, JSONObject jsonObj) throws SQLException,JSONException  
    {  
		ResultSetMetaData metaData = rs.getMetaData();  
		int columnCount = metaData.getColumnCount();  
		for (int i = 1; i <= columnCount; i++) {  
			String columnName =metaData.getColumnLabel(i);  
			String value = rs.getString(columnName);  
			jsonObj.put(columnName, value);  
		}
    	return jsonObj;  
    	
    }   
    
}
