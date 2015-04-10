package resource;


import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
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

import resource.DBConnPool;

@Path("product")
public class ProductResource {
	
	
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
    @Path("top5")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public JSONArray getTop5Products(@QueryParam("userId") String userId, @DefaultValue("") @QueryParam("userBrand") String userBrand) {
		String whereSql = "";
		if (!userBrand.equals(""))
			whereSql += " where instr('"+userBrand+"',brand)";
		String sql;
		if (userId.startsWith("zongjingli"))
			sql = String.format("select (-1) as UserOrderNumber, a.* from products as a order by OrderNumber desc limit 5");
		else if (userId.startsWith("zongdai")){
			sql = String.format("select a.*, b.UserOrderNumber from (select * from products %s order by OrderNumber desc limit 5) as a "
					+ " left join (select ProductId,sum(UserOrderNumber) as UserOrderNumber from all_orders where AreaId=%s and Invalid=0 and InvalidColor=0 group by ProductId) as b"
					+ " on a.Id=b.ProductId ", whereSql, userId.split(":")[1]);
		} else if (userId.startsWith("area")){
			sql = String.format("select a.*, b.UserOrderNumber from (select * from products %s order by OrderNumber desc limit 5) as a "
					+ " left join (select ProductId,sum(UserOrderNumber) as UserOrderNumber from all_orders where SuperAreaId=%s and Invalid=0 and InvalidColor=0 group by ProductId) as b"
					+ " on a.Id=b.ProductId ", whereSql, userId.split(":")[1]);
		} else
			sql = String.format("select a.*,b.UserOrderNumber from (select * from products  %s order by OrderNumber desc limit 5) as a "
					+ " left join (select ProductId,sum(UserOrderNumber) as UserOrderNumber from all_orders where CustomerId=%s and Invalid=0 and InvalidColor=0 group by ProductId) as b "
					+ " on a.Id=b.ProductId", whereSql, userId.split(":")[1]);
		JSONArray jsonArray = new JSONArray();  
		jsonArray = this.batchQuery(sql, jsonArray);
			
		return jsonArray;
    }

    
    @GET
    @Path("recommendation")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public JSONArray getRecommendProducts(@QueryParam("userId") String userId, @DefaultValue("") @QueryParam("userBrand") String userBrand) {
		String whereSql = "";
		if (!userBrand.equals(""))
			whereSql += " and instr('"+userBrand+"',brand)";
		String sql;
		if (userId.startsWith("zongjingli"))
			sql = String.format("select (-1) as UserOrderNumber, a.* from products as a where ProductType='推广'");
		else if (userId.startsWith("zongdai")){
			
			sql = String.format("select a.*, b.UserOrderNumber from (select * from products where ProductType='推广' %s ) as a "
					+ " left join (select ProductId,sum(UserOrderNumber) as UserOrderNumber from all_orders where AreaId=%s and Invalid=0 and InvalidColor=0 group by ProductId) as b"
					+ " on a.Id=b.ProductId ", whereSql, userId.split(":")[1]);
		} else if (userId.startsWith("area")){
			
			sql = String.format("select a.*, b.UserOrderNumber from (select * from products where ProductType='推广' %s) as a "
					+ " left join (select ProductId,sum(UserOrderNumber) as UserOrderNumber from all_orders where SuperAreaId=%s and Invalid=0 and InvalidColor=0 group by ProductId) as b"
					+ " on a.Id=b.ProductId ", whereSql, userId.split(":")[1]);
		} else
			sql = String.format("select a.*,b.UserOrderNumber from (select * from products where ProductType='推广' %s) as a "
					+ " left join (select ProductId,sum(UserOrderNumber) as UserOrderNumber from all_orders where CustomerId=%s and Invalid=0 and InvalidColor=0 group by ProductId) as b "
					+ " on a.Id=b.ProductId", whereSql, userId.split(":")[1]);
		JSONArray jsonArray = new JSONArray();  
		jsonArray = this.batchQuery(sql, jsonArray);
		if (jsonArray.length()==0)
			if (userId.startsWith("zongjingli"))
				sql = String.format("select (-1) as UserOrderNumber, a.* from products as a limit 5,5");
			else if (userId.startsWith("zongdai")){
				
				sql = String.format("select a.*, b.UserOrderNumber from (select * from products %s limit 5,5) as a "
						+ " left join (select ProductId,sum(UserOrderNumber) as UserOrderNumber from all_orders where AreaId=%s and Invalid=0 and InvalidColor=0 group by ProductId) as b"
						+ " on a.Id=b.ProductId ", whereSql.replace("and", "where"), userId.split(":")[1]);
			} else if (userId.startsWith("area")){
				
				sql = String.format("select a.*, b.UserOrderNumber from (select * from products %s limit 5,5) as a "
						+ " left join (select ProductId,sum(UserOrderNumber) as UserOrderNumber from all_orders where SuperAreaId=%s and Invalid=0 and InvalidColor=0 group by ProductId) as b"
						+ " on a.Id=b.ProductId ", whereSql.replace("and", "where"), userId.split(":")[1]);
			} else
				sql = String.format("select a.*,b.UserOrderNumber from (select * from products %s limit 5,5) as a "
						+ " left join (select ProductId,sum(UserOrderNumber) as UserOrderNumber from all_orders where CustomerId=%s and Invalid=0 and InvalidColor=0 group by ProductId) as b "
						+ " on a.Id=b.ProductId", whereSql.replace("and", "where"), userId.split(":")[1]);
			jsonArray = this.batchQuery(sql, jsonArray);
		return jsonArray;
    }
    		
    @GET
    @Path("filter")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public JSONArray filterProducts(@DefaultValue("") @QueryParam("userBrand") String userBrand,@DefaultValue("") @QueryParam("numberOrder") String numberOrder,@DefaultValue("") @QueryParam("idOrder") String idOrder, @DefaultValue("all") @QueryParam("class") String classParam, @DefaultValue("all") @QueryParam("mucai") String mucai, @DefaultValue("all") @QueryParam("serial") String serial
			, @DefaultValue("all") @QueryParam("type") String type, @DefaultValue("all") @QueryParam("brand") String brand, @QueryParam("userId") String userId, @QueryParam("startIndex") String startIndex, @QueryParam("count") String count) {
		String orderSql = "";
		if (numberOrder.equals("desc"))
			orderSql += ", OrderNumber desc";
		else if (numberOrder.equals("asc"))
			orderSql +=", OrderNumber asc";
		if (idOrder.equals("desc"))
			orderSql += ", Id desc";
		else if (idOrder.equals("asc"))
			orderSql += ", Id asc";
		orderSql = orderSql.replaceFirst(",", "order by");

		String whereSql = "";
		if (!userBrand.equals(""))
			whereSql += " and instr('"+userBrand+"',brand)";
		if (!brand.equalsIgnoreCase("all"))
			whereSql += String.format(" and instr('%s',brand)", brand);
		if (!type.equalsIgnoreCase("all"))
			whereSql += String.format(" and ProductType='%s'", type);
		if (!mucai.equalsIgnoreCase("all"))
			whereSql += String.format(" and Mucai='%s'", mucai);
		if (!classParam.equalsIgnoreCase("all"))
			whereSql += String.format(" and productClass='%s'", classParam);
		if (!serial.equalsIgnoreCase("all"))
			whereSql += String.format(" and productSerial='%s'", serial);
		
    	whereSql = whereSql.replaceFirst("and", "where");
		String sql;
		if (userId.startsWith("zongjingli"))
			sql = String.format("select * from products %s %s limit %s,%s ", whereSql, orderSql, startIndex, count);
		else if (userId.startsWith("zongdai"))
			sql = String.format("select a.*, b.UserOrderNumber from (select * from products %s %s limit %s,%s) as a "
					+ " left join (select ProductId,sum(UserOrderNumber) as UserOrderNumber from all_orders where AreaId=%s and Invalid=0 and InvalidColor=0 group by ProductId) as b"
					+ " on a.Id=b.ProductId ", whereSql, orderSql, startIndex, count, userId.split(":")[1]);
		else if (userId.startsWith("area"))
			sql = String.format("select a.*, b.UserOrderNumber from (select * from products %s %s limit %s,%s) as a "
					+ " left join (select ProductId,sum(UserOrderNumber) as UserOrderNumber from all_orders where SuperAreaId=%s and Invalid=0 and InvalidColor=0 group by ProductId) as b"
					+ " on a.Id=b.ProductId ", whereSql, orderSql, startIndex, count, userId.split(":")[1]);
		else
			sql = String.format("select a.*,b.UserOrderNumber from (select * from products %s %s limit %s,%s) as a"
					+ " left join (select ProductId,sum(UserOrderNumber) as UserOrderNumber from all_orders where CustomerId=%s and Invalid=0 and InvalidColor=0 group by ProductId) as b "
					+ " on a.Id=b.ProductId",whereSql, orderSql, startIndex, count, userId.split(":")[1]);
		JSONArray jsonArray = new JSONArray();  
		jsonArray = this.batchQuery(sql, jsonArray);
		return jsonArray;
    }

    @GET
    @Path("matches")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public JSONArray getBatchMatches(@QueryParam("startIndex") String startIndex, @QueryParam("count") String count,@DefaultValue("") @QueryParam("userBrand") String userBrand) {
		String whereSql = "";
		if (!userBrand.equals(""))
			whereSql += " where instr('"+userBrand+"',brand)";
		String sql = String.format("select * from matches %s limit %s,%s", whereSql, startIndex, count);
		JSONArray jsonArray = new JSONArray();  
		jsonArray = this.batchQuery(sql, jsonArray);
		return jsonArray;
    }

    @GET
    @Path("placeDisplay")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public JSONArray getBatchPlaceDisplay(@QueryParam("startIndex") String startIndex, @QueryParam("count") String count,@DefaultValue("") @QueryParam("userBrand") String userBrand) {
		String whereSql = "";
		if (!userBrand.equals(""))
			whereSql += " where instr('"+userBrand+"',brand)";
		String sql = String.format("select * from place_display %s limit %s,%s", whereSql, startIndex, count);
		JSONArray jsonArray = new JSONArray();  
		jsonArray = this.batchQuery(sql, jsonArray);
		return jsonArray;
    }
    
    @GET
    @Path("id/{productId}")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public JSONObject getProductById(@PathParam("productId") String productId, @QueryParam("userId") String userId) throws JSONException {
    	String sql;
    	if (userId.startsWith("zongjingli"))
			sql = String.format("select * from products where Id = '%s'", productId);
		else if (userId.startsWith("zongdai")){
			sql = String.format("select a.*,b.UserOrderNumber,b.UserOrderAmount from (select * from products where Id = '%s') as a "
					+ " left join (select ProductId,sum(UserOrderNumber) as UserOrderNumber,sum(UserOrderAmount) as UserOrderAmount from all_orders where ProductId=%s and AreaId=%s and Invalid=0 and InvalidColor=0 group by ProductId) as b"
					+ " on a.Id=b.ProductId", productId, productId, userId.split(":")[1]);
		} else if (userId.startsWith("area")){
			sql = String.format("select a.*,b.UserOrderNumber,b.UserOrderAmount from (select * from products where Id = '%s') as a "
					+ " left join (select ProductId,sum(UserOrderNumber) as UserOrderNumber,sum(UserOrderAmount) as UserOrderAmount from all_orders where ProductId=%s and SuperAreaId=%s and Invalid=0 and InvalidColor=0 group by ProductId) as b"
					+ " on a.Id=b.ProductId", productId, productId, userId.split(":")[1]);
		} else
			sql = String.format("select a.*,b.UserOrderDetails,b.UserOrderNumber,b.UserOrderAmount,b.Invalid from (select * from products where Id='%s') as a "
					+ " left join (select ProductId,sum(UserOrderNumber) as UserOrderNumber,sum(UserOrderAmount) as UserOrderAmount, Invalid, group_concat(Color,'::',UserOrderDetails separator '||') as UserOrderDetails from all_orders where CustomerId=%s and ProductId=%s group by ProductId) as b "
					+ " on a.Id=b.ProductId", productId, userId.split(":")[1], productId);
		JSONObject jsonObj = new JSONObject();  
		jsonObj = this.singleQuery(sql, jsonObj);
		if (!jsonObj.get("MatchIds").equals("")) {
			JSONArray jsonArray = new JSONArray();
			jsonArray = this.batchQuery(String.format("select * from matches where find_in_set(concat(Id), '%s');",jsonObj.get("MatchIds").toString()) , jsonArray);
			for (int i=0; i<jsonArray.length(); i++) {
				JSONObject jsonMPObj = jsonArray.getJSONObject(i);
				JSONArray jsonMPArray = new JSONArray();
				jsonMPArray = this.batchQuery("select Id,TagPrice,Pictures from products where id in ("+jsonMPObj.get("MatchProductIDs").toString()+")", jsonMPArray);
				jsonMPObj.put("MatchProductList",jsonMPArray);
				jsonArray.put(i, jsonMPObj);
			}
			jsonObj.put("Matches", jsonArray);
		}
		else if (!jsonObj.get("MatchProductIds").equals("")) {
			JSONArray jsonMPArray = new JSONArray();
			jsonMPArray = this.batchQuery("select Id,TagPrice,Pictures from products where id in ("+jsonObj.get("MatchProductIds").toString()+")", jsonMPArray);
			jsonObj.put("RecommendProductList",jsonMPArray);
		}
		
		if (userId.contains("zongdai")||userId.contains("zongjingli")){
	    	if (userId.startsWith("zongjingli")) {
				sql = String.format("select  b.name, a.color, a.UserOrderDetails from (select CustomerId, color, UserOrderDetails from all_orders where ProductId = '%s' and Invalid=0 and InvalidColor=0 ) as a "
						+ "	inner join user_info as b on a.customerid=b.id", productId);
	    	} else if (userId.startsWith("zongdai")){	
				sql = String.format("select  b.name, a.color, a.UserOrderDetails from (select CustomerId, color, UserOrderDetails from all_orders where ProductId = '%s' and AreaId=%s and Invalid=0 and InvalidColor=0 ) as a "
						+ "	inner join user_info as b on a.customerid=b.id", productId, userId.split(":")[1]);
			} else if (userId.startsWith("area")){	
				sql = String.format("select  b.name, a.color, a.UserOrderDetails from (select CustomerId, color, UserOrderDetails from all_orders where ProductId = '%s' and SuperAreaId=%s and Invalid=0 and InvalidColor=0 ) as a "
						+ "	inner join user_info as b on a.customerid=b.id", productId, userId.split(":")[1]);
			} else {
				sql = String.format("select  b.name, a.color, a.UserOrderDetails from (select CustomerId, color, UserOrderDetails from all_orders where ProductId = '%s' and AreaId=%s and Invalid=0 and InvalidColor=0 and CustomerId!=%s) as a "
						+ "	inner join user_info as b on a.customerid=b.id", productId, userId.split(":")[1], userId.split(":")[1]);
			}

			JSONArray jsonArray = new JSONArray();  
			jsonArray = this.batchQuery(sql, jsonArray);
			jsonObj.put("DiancangOrderDetails", jsonArray);
		}
		JSONArray comments = new JSONArray();  
		comments = this.batchQuery("select * from comments where ProductId="+productId, comments);
		jsonObj.put("Comments", comments);
		return jsonObj;
    }

    @GET
    @Path("searchByCode/{productCode}")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public JSONArray searchByCode(@PathParam("productCode") String productCode, @QueryParam("userId") String userId, @DefaultValue("") @QueryParam("userBrand") String userBrand) throws JSONException {
		String whereSql = "";
		if (!userBrand.equals(""))
			whereSql += " and instr('"+userBrand+"',brand)";
		String sql;
    	if (userId.startsWith("zongjingli"))
			sql = String.format("select * from products where  productCode like '%%%s%%'",productCode);
		else if (userId.startsWith("zongdai")){
			sql = String.format("select a.*, b.UserOrderNumber from (select * from products where productCode like '%%%s%%' %s) as a "
					+ " left join (select ProductId,sum(UserOrderNumber) as UserOrderNumber from all_orders where AreaId=%s and Invalid=0 and InvalidColor=0 group by ProductId) as b"
					+ " on a.Id=b.ProductId", productCode, whereSql,userId.split(":")[1]);
		} else if (userId.startsWith("area")){
			sql = String.format("select a.*, b.UserOrderNumber from (select * from products where productCode like '%%%s%%' %s) as a "
					+ " left join (select ProductId,sum(UserOrderNumber) as UserOrderNumber from all_orders where SuperAreaId=%s and Invalid=0 and InvalidColor=0 group by ProductId) as b"
					+ " on a.Id=b.ProductId", productCode, whereSql,userId.split(":")[1]);
		} else
			sql = String.format("select a.*,b.UserOrderNumber from (select * from products where productCode like '%%%s%%' %s) as a "
					+ " left join (select ProductId,sum(UserOrderNumber) as UserOrderNumber from all_orders where CustomerId=%s and Invalid=0 and InvalidColor=0 group by ProductId) as b "
				+ " on a.Id=b.ProductId",  productCode, whereSql,userId.split(":")[1]);
		JSONArray result = new JSONArray();  
		result = this.batchQuery(sql, result);
		return result;
    }
    
    @GET
    @Path("getByIdSet")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public JSONArray getProductsByIdSet(@QueryParam("productIdSet") String productIdSet, @QueryParam("userId") String userId) throws JSONException {
    	String sql;
    	if (userId.startsWith("zongjingli"))
			sql = String.format("select * from products where Id in (%s)", productIdSet);
    	else if (userId.startsWith("zongdai")){
			sql = String.format("select a.*,IFNULL(b.UserOrderNumber,0) as UserOrderNumber,IFNULL(b.UserOrderAmount,0) as UserOrderAmount from (select * from products where Id in (%s)) as a "
					+ " left join (select ProductId,sum(UserOrderAmount) as UserOrderAmount, sum(UserOrderNumber) as UserOrderNumber from all_orders where ProductId in (%s) and AreaId=%s and Invalid=0 and InvalidColor=0 group by ProductId) as b"
					+ " on a.Id=b.ProductId", productIdSet, productIdSet, userId.split(":")[1]);
		} else if (userId.startsWith("area")){
			sql = String.format("select a.*,IFNULL(b.UserOrderNumber,0) as UserOrderNumber,IFNULL(b.UserOrderAmount,0) as UserOrderAmount from (select * from products where Id in (%s)) as a "
					+ " left join (select ProductId,sum(UserOrderAmount) as UserOrderAmount, sum(UserOrderNumber) as UserOrderNumber from all_orders where ProductId in (%s) and SuperAreaId=%s and Invalid=0 and InvalidColor=0 group by ProductId) as b"
					+ " on a.Id=b.ProductId", productIdSet, productIdSet, userId.split(":")[1]);
		} else
			sql = String.format("select a.*,b.UserOrderDetails,IFNULL(b.UserOrderNumber,0) as UserOrderNumber,IFNULL(b.UserOrderAmount,0) as UserOrderAmount,b.Invalid from (select * from products where Id in (%s)) as a "
					+ " left join (select ProductId,Invalid,sum(UserOrderNumber) as UserOrderNumber,sum(UserOrderAmount) as UserOrderAmount, group_concat(color,'::',UserOrderDetails separator '||') as UserOrderDetails from all_orders where CustomerId=%s and ProductId in (%s) group by ProductId) as b "
					+ " on a.Id=b.ProductId", productIdSet, userId.split(":")[1], productIdSet);
		JSONArray jsonArray = new JSONArray();  
		jsonArray = this.batchQuery(sql, jsonArray);
		if (userId.contains("zongdai")||userId.contains("zongjingli")) {
			for (int i = 0; i < jsonArray.length(); i++){
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				String productId =  jsonObject.getString("Id");
		    	if (userId.startsWith("zongjingli")) {
					sql = String.format("select  b.name, a.color, a.UserOrderDetails from (select CustomerId, color, UserOrderDetails from all_orders where ProductId = '%s' and Invalid=0 and InvalidColor=0 ) as a "
							+ "	inner join user_info as b on a.customerid=b.id", productId);
		    	} else if (userId.startsWith("zongdai")){	
					sql = String.format("select  b.name, a.color, a.UserOrderDetails from (select CustomerId, color, UserOrderDetails from all_orders where ProductId = '%s' and AreaId=%s and Invalid=0 and InvalidColor=0 ) as a "
							+ "	inner join user_info as b on a.customerid=b.id", productId, userId.split(":")[1]);
				} else if (userId.startsWith("area")){	
					sql = String.format("select  b.name, a.color, a.UserOrderDetails from (select CustomerId, color, UserOrderDetails from all_orders where ProductId = '%s' and SuperAreaId=%s and Invalid=0 and InvalidColor=0 ) as a "
							+ "	inner join user_info as b on a.customerid=b.id", productId, userId.split(":")[1]);
				} else {
					sql = String.format("select  b.name, a.color, a.UserOrderDetails from (select CustomerId, color, UserOrderDetails from all_orders where ProductId = '%s' and AreaId=%s and Invalid=0 and InvalidColor=0 and CustomerId!=%s) as a "
							+ "	inner join user_info as b on a.customerid=b.id", productId, userId.split(":")[1], userId.split(":")[1]);
				}
		    	JSONArray DiancangOrderDetails = new JSONArray();  
		    	DiancangOrderDetails = this.batchQuery(sql, DiancangOrderDetails);
		    	jsonObject.put("DiancangOrderDetails", DiancangOrderDetails);
			}			
		}
		
		return jsonArray;
    }

    @GET
    @Path("filterCollection")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public JSONArray filterCollection(@DefaultValue("") @QueryParam("numberOrder") String numberOrder, @DefaultValue("") @QueryParam("idOrder") String idOrder, @DefaultValue("all") @QueryParam("class") String classParam, @DefaultValue("all") @QueryParam("serial") String serial
			, @DefaultValue("all") @QueryParam("type") String type, @DefaultValue("all") @QueryParam("brand") String brand, @DefaultValue("all") @QueryParam("mucai") String mucai, @QueryParam("productIdSet") String productIdSet, @QueryParam("userId") String userId) {
		String whereSql = "where id in ("+productIdSet+")";
		String orderSql = "";
		if (numberOrder.equals("desc"))
			orderSql += ", OrderNumber desc";
		else if (numberOrder.equals("asc"))
			orderSql +=", OrderNumber asc";
		if (idOrder.equals("desc"))
			orderSql += ", Id desc";
		else if (idOrder.equals("asc"))
			orderSql += ", Id asc";
		orderSql = orderSql.replaceFirst(",", "order by");

		
		if (!brand.equalsIgnoreCase("all"))
			whereSql += String.format(" and instr('%s',brand)", brand);
		if (!mucai.equalsIgnoreCase("all"))
			whereSql += String.format(" and Mucai='%s'", mucai);
		if (!type.equalsIgnoreCase("all"))
			whereSql += String.format(" and ProductType='%s'", type);
		if (!classParam.equalsIgnoreCase("all"))
			whereSql += String.format(" and productClass='%s'", classParam);
		if (!serial.equalsIgnoreCase("all"))
			whereSql += String.format(" and productSerial='%s'", serial);

		String sql;
		if (userId.startsWith("zongjingli"))
			sql = String.format("select * from products %s %s", whereSql, orderSql);
		else if (userId.startsWith("zongdai"))
			sql = String.format("select a.*, b.UserOrderNumber from (select * from products %s %s) as a "
					+ " left join (select ProductId,sum(UserOrderNumber) as UserOrderNumber from all_orders where AreaId=%s and Invalid=0 and InvalidColor=0 group by ProductId) as b"
					+ " on a.Id=b.ProductId ", whereSql, orderSql, userId.split(":")[1]);
		else if (userId.startsWith("area"))
			sql = String.format("select a.*, b.UserOrderNumber from (select * from products %s %s) as a "
					+ " left join (select ProductId,sum(UserOrderNumber) as UserOrderNumber from all_orders where SuperAreaId=%s and Invalid=0 and InvalidColor=0 group by ProductId) as b"
					+ " on a.Id=b.ProductId ", whereSql, orderSql, userId.split(":")[1]);
		else
			sql = String.format("select a.*,b.UserOrderNumber from (select * from products %s %s) as a"
					+ " left join (select ProductId,sum(UserOrderNumber) as UserOrderNumber from all_orders where CustomerId=%s and Invalid=0 and InvalidColor=0 group by ProductId) as b"
					+ " on a.Id=b.ProductId",whereSql, orderSql, userId.split(":")[1]);
		JSONArray jsonArray = new JSONArray();  
		jsonArray = this.batchQuery(sql, jsonArray);
		return jsonArray;
    }

    
    /*返回给我的删除
     *客户曾经订过的商品 
     */
    @GET
    @Path("invalidOrder")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public JSONArray invalidOrderProducts(@QueryParam("userId") String userId, @QueryParam("startIndex") String startIndex, @QueryParam("count") String count) {
    	JSONArray jsonArray = new JSONArray();  
		if (userId.startsWith("diancang")){
			String sql = String.format("select a.*,b.UserOrderNumber from (select * from products limit %s,%s) as a "
					+ " inner join (select ProductId,sum(UserOrderNumber) as UserOrderNumber from all_orders where CustomerId=%s and Invalid=1 group by ProductId) as b"
					+ " on a.Id=b.ProductId", startIndex, count, userId.split(":")[1]);
			jsonArray = this.batchQuery(sql, jsonArray);
		}
		return jsonArray;
    }

//    下架款
    @GET
    @Path("filterDeleted")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public JSONArray filterDeletedProducts(@DefaultValue("no") @QueryParam("checkOrdered") String checkOrdered, @DefaultValue("") @QueryParam("userBrand") String userBrand, @DefaultValue("") @QueryParam("numberOrder") String numberOrder, @DefaultValue("") @QueryParam("idOrder") String idOrder, @DefaultValue("all") @QueryParam("brand") String brand
			, @DefaultValue("all") @QueryParam("mucai") String mucai, @DefaultValue("all") @QueryParam("type") String type, @DefaultValue("all") @QueryParam("class") String classParam, @DefaultValue("all") @QueryParam("serial") String serial, @QueryParam("userId") String userId) {
    	String sql;
		String whereSql = "";
		if (!userBrand.equals(""))
			whereSql += " and instr('"+userBrand+"',brand)";
    	if (checkOrdered.equals("yes")){
			if (userId.startsWith("zongjingli"))
				sql = String.format("select * from products where deleted is true and ordernumber >0 ");
			else if (userId.startsWith("zongdai")){
				sql = String.format("select a.*, b.UserOrderNumber from (select * from products where deleted is true %s) as a "
						+ " inner join (select ProductId, sum(UserOrderNumber) as UserOrderNumber from all_orders where AreaId=%s and Invalid=0 and InvalidColor=0 group by productId ) as b on a.Id=b.ProductId"
						,whereSql,userId.split(":")[1]);
			} else if (userId.startsWith("area")){
				sql = String.format("select a.*, b.UserOrderNumber from (select * from products where deleted is true %s) as a "
						+ " inner join (select ProductId, sum(UserOrderNumber) as UserOrderNumber from all_orders where SuperAreaId=%s and Invalid=0 and InvalidColor=0 group by productId ) as b on a.Id=b.ProductId"
						,whereSql,userId.split(":")[1]);
			} else
				sql = String.format("select a.*, b.UserOrderNumber from (select * from products where deleted is true %s) as a "
						+ " inner join (select ProductId, sum(UserOrderNumber) as UserOrderNumber from all_orders where CustomerId=%s and Invalid=0 and InvalidColor=0 group by productId ) as b on a.Id=b.ProductId"
						,whereSql, userId.split(":")[1]);
		} else {
			String orderSql = "";
			if (numberOrder.equals("desc"))
				orderSql += ", OrderNumber desc";
			else if (numberOrder.equals("asc"))
				orderSql +=", OrderNumber asc";
			if (idOrder.equals("desc"))
				orderSql += ", Id desc";
			else if (idOrder.equals("asc"))
				orderSql += ", Id asc";
			orderSql = orderSql.replaceFirst(",", "order by");

			if (!brand.equalsIgnoreCase("all"))
				whereSql += String.format(" and instr('%s',brand)", brand);
			if (!mucai.equalsIgnoreCase("all"))
				whereSql += String.format(" and Mucai='%s'", mucai);
			if (!type.equalsIgnoreCase("all"))
				whereSql += String.format(" and productType='%s'", type);
			if (!classParam.equalsIgnoreCase("all"))
				whereSql += String.format(" and productClass='%s'", classParam);
			if (!serial.equalsIgnoreCase("all"))
				whereSql += String.format(" and productSerial='%s'", serial);
			
	    	if (userId.startsWith("zongjingli"))
				sql = String.format("select * from products where deleted is true %s %s",whereSql,orderSql);
			else if (userId.startsWith("zongdai")){
				sql = String.format("select a.*, b.UserOrderNumber from (select * from products where deleted is true %s %s) as a "
						+ "left join (select ProductId, sum(UserOrderNumber) as UserOrderNumber from all_orders where AreaId=%s and Invalid=0 and InvalidColor=0 group by productId ) as b on a.Id=b.ProductId"
						,whereSql, orderSql, userId.split(":")[1]);
			} else if (userId.startsWith("area")){
				sql = String.format("select a.*, b.UserOrderNumber from (select * from products where deleted is true %s %s) as a "
						+ "left join (select ProductId, sum(UserOrderNumber) as UserOrderNumber from all_orders where SuperAreaId=%s and Invalid=0 and InvalidColor=0 group by productId ) as b on a.Id=b.ProductId"
						,whereSql, orderSql, userId.split(":")[1]);
			} else
				sql = String.format("select a.*, b.UserOrderNumber from (select * from products where deleted is true %s %s) as a "
						+ "left join (select ProductId, UserOrderNumber, UserOrderAmount from all_orders where CustomerId=%s and Invalid=0 and InvalidColor=0 ) as b on a.Id=b.ProductId"
						,whereSql, orderSql, userId.split(":")[1]);
		}
		JSONArray jsonArray = new JSONArray();  
		jsonArray = this.batchQuery(sql, jsonArray);
		return jsonArray;
    }
    
    @GET
    @Path("filterOrdered")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public JSONArray filterOrderedProducts(@DefaultValue("") @QueryParam("numberOrder") String numberOrder, @DefaultValue("") @QueryParam("idOrder") String idOrder, @DefaultValue("all") @QueryParam("brand") String brand
			, @DefaultValue("all") @QueryParam("mucai") String mucai, @DefaultValue("all") @QueryParam("type") String type, @DefaultValue("all") @QueryParam("class") String classParam, @DefaultValue("all") @QueryParam("serial") String serial, 
			@QueryParam("userId") String userId, @QueryParam("startIndex") String startIndex, @QueryParam("count") String count) {
		String orderSql = "";
		if (numberOrder.equals("desc"))
			orderSql += ", OrderNumber desc";
		else if (numberOrder.equals("asc"))
			orderSql +=", OrderNumber asc";
		if (idOrder.equals("desc"))
			orderSql += ", Id desc";
		else if (idOrder.equals("asc"))
			orderSql += ", Id asc";
		orderSql = orderSql.replaceFirst(",", "order by");

		String whereSql = "";
		if (!brand.equalsIgnoreCase("all"))
			whereSql += String.format(" and instr('%s',brand)", brand);
		if (!mucai.equalsIgnoreCase("all"))
			whereSql += String.format(" and Mucai='%s'", mucai);
		if (!type.equalsIgnoreCase("all"))
			whereSql += String.format(" and ProductType='%s'", type);
		if (!classParam.equalsIgnoreCase("all"))
			whereSql += String.format(" and productClass='%s'", classParam);
		if (!serial.equalsIgnoreCase("all"))
			whereSql += String.format(" and productSerial='%s'", serial);
    	if (userId.startsWith("zongjingli"))
    		whereSql += String.format(" and OrderNumber >0 ");
    	else if (userId.startsWith("zongdai"))
    		whereSql += String.format("and AreaId=%s and Invalid=0 and InvalidColor=0 ", userId.split(":")[1]);
    	else if (userId.startsWith("area"))
    		whereSql += String.format("and SuperAreaId=%s and Invalid=0 and InvalidColor=0 ", userId.split(":")[1]);
    	else
    		whereSql += String.format("and CustomerId=%s and Invalid=0 and InvalidColor=0 ", userId.split(":")[1]);
    	whereSql = whereSql.replaceFirst("and", "where");
    	String sql;
    	if (userId.startsWith("zongjingli")){
    		orderSql = orderSql.replace("UserOrderNumber", "OrderNumber").replace("ProductId", "Id");
    		sql = String.format("select * from products %s %s limit %s, %s", whereSql, orderSql, startIndex, count);
    	}
		else if (userId.startsWith("zongdai"))
			sql = String.format("select a.UserOrderNumber, b.*  from (select ProductId,sum(UserOrderNumber) as UserOrderNumber from all_orders %s group by productId %s limit %s,%s) as a "
					+" left join  (select * from products where OrderNumber>0) as b on b.Id=a.ProductId "
					,whereSql, orderSql, startIndex, count);
		else if (userId.startsWith("area"))
			sql = String.format("select a.UserOrderNumber, b.*  from (select ProductId,sum(UserOrderNumber) as UserOrderNumber from all_orders %s group by productId %s limit %s,%s) as a "
					+" left join  (select * from products where OrderNumber>0) as b on b.Id=a.ProductId "
					,whereSql, orderSql, startIndex, count);
		else
			sql = String.format("select a.UserOrderNumber, b.*  from (select ProductId,sum(UserOrderNumber) as UserOrderNumber from all_orders %s group by productId %s limit %s,%s) as a "
					+" left join  (select * from products where OrderNumber>0) as b on b.Id=a.ProductId "
					,whereSql, orderSql, startIndex, count);

		JSONArray jsonArray = new JSONArray();  
		jsonArray = this.batchQuery(sql, jsonArray);
		return jsonArray;
    }
    
        
    @GET
    @Path("filterNotOrdered")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public JSONArray filterNotOrderedProducts(@DefaultValue("no") @QueryParam("checkMust") String checkMust, @DefaultValue("") @QueryParam("numberOrder") String numberOrder, @DefaultValue("") @QueryParam("idOrder") String idOrder, @DefaultValue("all") @QueryParam("mucai") String mucai
			, @DefaultValue("all") @QueryParam("brand") String brand, @DefaultValue("") @QueryParam("userBrand") String userBrand, @DefaultValue("all") @QueryParam("type") String type, @DefaultValue("all") @QueryParam("class") String classParam, @DefaultValue("all") @QueryParam("serial") String serial, 
			@QueryParam("userId") String userId, @QueryParam("startIndex") String startIndex, @QueryParam("count") String count) {
		String whereSql = "";
		if (!userBrand.equals(""))
			whereSql += " and instr('"+userBrand+"',brand)";
    	String sql="";
		if (checkMust.equals("yes")){
	
	    	if (userId.startsWith("zongjingli"))
				sql = String.format("select * from products where productType = '必订' and (OrderNumber <=0 or OrderNumber is Null)");
			else if (userId.startsWith("zongdai")){
				sql = String.format("select * from products where productType = '必订' %s and id not in ( select productId from all_orders where  AreaId=%s and Invalid=0 and InvalidColor=0)"
						,whereSql, userId.split(":")[1]);
			} else if (userId.startsWith("area")){
				sql = String.format("select * from products where productType = '必订' %s and id not in ( select productId from all_orders where  SuperAreaId=%s and Invalid=0 and InvalidColor=0)"
						,whereSql, userId.split(":")[1]);
			} else
				sql = String.format("select * from products where productType = '必订' %s and id not in ( select productId from all_orders where  CustomerId=%s and Invalid=0 and InvalidColor=0)",
						whereSql, userId.split(":")[1]);
		} else {
    	
			String orderSql = "";
			if (numberOrder.equals("desc"))
				orderSql += ", OrderNumber desc";
			else if (numberOrder.equals("asc"))
				orderSql +=", OrderNumber asc";
			if (idOrder.equals("desc"))
				orderSql += ", Id desc";
			else if (idOrder.equals("asc"))
				orderSql += ", Id asc";
			orderSql = orderSql.replaceFirst(",", "order by");
			
			if (!userBrand.equals(""))
				whereSql += " and instr('"+userBrand+"',brand)";
			if (!brand.equalsIgnoreCase("all"))
				whereSql += String.format(" and instr('%s',brand)", brand);
			if (!mucai.equalsIgnoreCase("all"))
				whereSql += String.format(" and Mucai='%s'", mucai);
			if (!type.equalsIgnoreCase("all"))
				whereSql += String.format(" and ProductType='%s'", type);
			if (!classParam.equalsIgnoreCase("all"))
				whereSql += String.format(" and productClass='%s'", classParam);
			if (!serial.equalsIgnoreCase("all"))
				whereSql += String.format(" and productSerial='%s'", serial);
	    	if (userId.startsWith("zongjingli"))
				sql = String.format("select * from products where OrderNumber <=0 %s %s limit %s,%s", whereSql, orderSql, startIndex, count);
			else if (userId.startsWith("zongdai")){
				sql = String.format("select * from products where id not in (select ProductId  from all_orders  where AreaId=%s and Invalid=0 and InvalidColor=0) "
						+"  %s %s limit %s,%s"
						, userId.split(":")[1],whereSql, orderSql, startIndex, count);
			} else if (userId.startsWith("area")){
				sql = String.format("select * from products where id not in (select ProductId  from all_orders  where SuperAreaId=%s and Invalid=0 and InvalidColor=0)"
						+"  %s %s limit %s,%s"
						,userId.split(":")[1],whereSql, orderSql, startIndex, count);
			} else
				sql = String.format("select * from products where products.id not in (select productId from all_orders  where CustomerId=%s and Invalid=0 and InvalidColor=0) %s %s limit %s,%s",
						 userId.split(":")[1], whereSql, orderSql, startIndex, count);
		}
		JSONArray jsonArray = new JSONArray();  
		jsonArray = this.batchQuery(sql, jsonArray);
		return jsonArray;
    }

    @GET
    @Path("showing")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public JSONArray getShowingProduct(@QueryParam("userId") String userId , @DefaultValue("") @QueryParam("userBrand") String userBrand) throws JSONException {
		String whereSql = "";
		if (!userBrand.equals(""))
			whereSql += " and instr('"+userBrand+"',brand)";
		String sql;
    	if (userId.startsWith("zongjingli"))
			sql = String.format("select * from products where isshowing=1 ");
		else if (userId.startsWith("zongdai")){
			sql = String.format("select a.*,b.UserOrderNumber from (select * from products where isshowing=1 %s) as a "
					+ "left join (select ProductId,sum(UserOrderNumber) as UserOrderNumber from all_orders where AreaId=%s and Invalid=0 and InvalidColor=0 group by ProductId) as b "
					+ "on a.Id=b.ProductId ", whereSql, userId.split(":")[1]);
		} else if (userId.startsWith("area")){
			sql = String.format("select a.*,b.UserOrderNumber from (select * from products where isshowing=1 %s) as a "
					+ "left join (select ProductId,sum(UserOrderNumber) as UserOrderNumber from all_orders where SuperAreaId=%s and Invalid=0 and InvalidColor=0 group by ProductId) as b "
					+ "on a.Id=b.ProductId ", whereSql, userId.split(":")[1]);
		} else
			sql = String.format("select a.*,b.UserOrderNumber from (select * from products where isshowing=1 %s) as a "
					+ " left join (select ProductId,sum(UserOrderNumber) as UserOrderNumber from all_orders where CustomerId=%s and Invalid=0 and InvalidColor=0 group by ProductId) as b "
					+ "on a.Id=b.ProductId ", whereSql, userId.split(":")[1]);
		JSONArray jsonArray = new JSONArray();  
		jsonArray = this.batchQuery(sql, jsonArray);
		return jsonArray;
    }
    
    private JSONArray batchQuery(String sql, JSONArray jsonArray) {
    	//System.out.println(sql);
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
    
//    private JSONArray batchQueryForDiancangRecords(String sql, JSONArray jsonArray) {
//    	////System.out.println(sql);
//    	DBConnPool pool = new DBConnPool();
//		try {
//			ResultSet rs = pool.stmt.executeQuery(sql); 
//			jsonArray = this.resultSetToJsonForDiancangRecords(rs, jsonArray);
//		} catch (SQLException | JSONException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//	    } finally {  
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
//	    return jsonArray;
//    }    
    
    private JSONObject singleQuery(String sql, JSONObject jsonObj) {
    	//System.out.println(sql);
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
    
    private JSONArray resultSetToJson(ResultSet rs, JSONArray jsonArray) throws SQLException,JSONException{  
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
//    		jsonObj.put("name", rs.getString("Name"));
//    		jsonObj.put("color", keys[0]);
//    		jsonObj.put("UserOrderDetails", colorSet.get(keys[0]));
//    		jsonArray.put(jsonObj);
//	    	for(int i = 1; i<keys.length; i++)  
//	    	{    
//	    		JSONObject tmpObj = new JSONObject();
//	    		tmpObj.put("name", "");
//	    		tmpObj.put("color",  keys[i]);
//	    		tmpObj.put("UserOrderDetails", colorSet.get(keys[i]));
//	    		jsonArray.put(tmpObj);
//	    	}
//    	}
//	    	
//    	return jsonArray;  
//    } 
    
    private JSONObject resultToJson(ResultSet rs, JSONObject jsonObj) throws SQLException,JSONException{  
		ResultSetMetaData metaData = rs.getMetaData();  
		int columnCount = metaData.getColumnCount();  
		for (int i = 1; i <= columnCount; i++) {  
			String columnName =metaData.getColumnLabel(i);  
			String value = rs.getString(columnName);  
			jsonObj.put(columnName, value);  
		}
    	return jsonObj;  
    	
    } 
    
    private boolean isInteger(String str) {    
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");    
        return pattern.matcher(str).matches();    
    }  
}


