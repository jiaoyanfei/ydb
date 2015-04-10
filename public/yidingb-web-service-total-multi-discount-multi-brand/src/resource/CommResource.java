package resource;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import javax.ws.rs.Consumes;
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

import java.security.InvalidKeyException;  
import java.security.NoSuchAlgorithmException;  
import java.security.Security;  
  


import java.util.ArrayList;
import java.util.List;

import javax.crypto.BadPaddingException;  
import javax.crypto.Cipher;  
import javax.crypto.IllegalBlockSizeException;  
import javax.crypto.KeyGenerator;  
import javax.crypto.NoSuchPaddingException;  
import javax.crypto.SecretKey;  
@Path("comm")
public class CommResource {
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
    
//    @GET
//    @Path("generateCode")
//    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
//    public JSONObject generateCode() {  
//		EncrypDES de1 = new EncrypDES();
//		String msg ="郭XX-搞笑相声全集";
//		byte[] encontent = de1.Encrytor(msg);
//		byte[] decontent = de1.Decryptor(encontent);
//    }  
    
    
    @POST
	@Path("loginByCode")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public JSONObject loginByCode(MultivaluedMap<String, String> loginParams) throws JSONException{
	    String userCode = loginParams.getFirst("userCode");
	    JSONObject jsonObj = new JSONObject();  
		jsonObj.put("ResultCode", "3");
		DBConnPool pool = new DBConnPool();
		try {
			Statement stmt = pool.conn.createStatement();
			String sql = String.format("SELECT a.*, b.* "
					+ " FROM (select * from user_info where LoginUserName='%s') as a left join order_guide as b on a.OrderGuideId=b.Id"
					, userCode);
			//System.out.println(sql);
			ResultSet rs = stmt.executeQuery(sql);
			if (rs.next()){
				jsonObj = this.resultToJson(rs, jsonObj);
				jsonObj.put("ResultCode", "0");
				String userId = rs.getString("Id");
				String auth = rs.getString("Authority");
				if (auth.contains("zongdai")) {
					sql = "select a.*, b.* from (select * from user_info where authority like 'diancang%' and  AreaId="+ userId+") as a left join order_guide as b on a.OrderGuideId=b.id";
					//System.out.println(sql);
					ResultSet diancangBudgets = stmt.executeQuery(sql);
					JSONArray jsonArray = new JSONArray();  
					jsonArray = this.resultSetToJson(diancangBudgets, jsonArray);
					jsonObj.put("DiancangBudget", jsonArray);
				} else if (auth.contains("zongjingli")){
					String sql2;
					if (auth.equals("zongjingli")){
						sql = "select sum(orderamount) as OrderAmount, sum(GuideAmount) as GuideAmount, (10) as Discount from user_info where authority like 'diancang%'";
						sql2 = "select Name,BudgetAmount,OrderAmount, GuideAmount, OrderProductNumber,OrderStyleNumber from user_info where authority like '%diancang%' and OrderAmount > 0 order by OrderAmount desc";
					} else {
						sql = "select sum(orderamount) as OrderAmount, sum(GuideAmount) as GuideAmount from user_info where authority like 'diancang%' and   superareaid="+userId;
						sql2 = "select Name,BudgetAmount,OrderAmount, GuideAmount, OrderProductNumber,OrderStyleNumber from user_info where superareaid="+userId+" and authority like 'diancang%' and OrderAmount > 0 order by OrderAmount desc";
					}
					//System.out.println(sql);
					ResultSet rs1 = stmt.executeQuery(sql);
					if (rs1.next())
						jsonObj = this.resultToJson(rs1, jsonObj);
					//System.out.println(sql);
					ResultSet top10Diancang = stmt.executeQuery(sql2);
					JSONArray jsonArray = new JSONArray();  
					jsonArray = this.resultSetToJson(top10Diancang, jsonArray);
					jsonObj.put("Top10Diancang", jsonArray);
				}
				sql = "select count(*) as productTotalNumber from products";
				//System.out.println(sql);
				rs = stmt.executeQuery(sql);
				if (rs.next())
					jsonObj.put("productTotalNumber", rs.getString("productTotalNumber"));
				stmt.executeUpdate("update user_info set logined=0 where id="+userId);
			} else { 
				jsonObj.put("ResultCode", "1");
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
    
    @POST
	@Path("login")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public JSONObject userLogin(MultivaluedMap<String, String> loginParams) throws JSONException{
	    String username = loginParams.getFirst("username");
	    String password = loginParams.getFirst("password");
	    JSONObject jsonObj = new JSONObject();  
		jsonObj.put("ResultCode", "3");
		DBConnPool pool = new DBConnPool();
		try {
			Statement stmt = pool.conn.createStatement();
			String sql = String.format("SELECT a.*, b.* "
					+ " FROM (select * from user_info where LoginUserName='%s') as a left join order_guide as b on a.OrderGuideId=b.Id"
					, username);
			//System.out.println(sql);
			ResultSet rs = stmt.executeQuery(sql);
			if (rs.next()){
				jsonObj = this.resultToJson(rs, jsonObj);
				if (jsonObj.getString("logined") == "1")
					jsonObj.put("ResultCode", "4");
				else if (jsonObj.getString("Password").equalsIgnoreCase(password)){
					jsonObj.put("ResultCode", "0");
					String userId = rs.getString("Id");
					String auth = rs.getString("Authority");
					if (auth.contains("zongdai")) {
//						sql = "select count(distinct productid) as OrderStyleNumber from all_orders where Customerid in (select id from user_info where areaid="+userId+")";
//						//System.out.println(sql);
//						ResultSet rs1 = stmt.executeQuery(sql);
//						if (rs1.next())
//							jsonObj = this.resultToJson(rs1, jsonObj);
//						sql = "select sum(OrderProductNumber) as OrderProductNumber, sum(orderamount) as OrderAmount, sum(budgetAmount) as BudgetAmount from user_info where areaid="+userId;
//						//System.out.println(sql);
//						ResultSet rs1 = stmt.executeQuery(sql);
//						if (rs1.next())
//							jsonObj = this.resultToJson(rs1, jsonObj);
						sql = "select a.*, b.* from (select * from user_info where AreaId="+ userId+") as a left join order_guide as b on a.OrderGuideId=b.id";
						//System.out.println(sql);
						ResultSet diancangBudgets = stmt.executeQuery(sql);
						JSONArray jsonArray = new JSONArray();  
						jsonArray = this.resultSetToJson(diancangBudgets, jsonArray);
						jsonObj.put("DiancangBudget", jsonArray);
					} else if (auth.contains("zongjingli")){
//						sql = "select count(*) as OrderStyleNumber, sum(ordernumber) as OrderProductNumber, sum(orderamount) as OrderAmount from products where orderamount>0";
//						//System.out.println(sql);
//						ResultSet rs1 = stmt.executeQuery(sql);
//						if (rs1.next())
//							jsonObj = this.resultToJson(rs1, jsonObj);
						String sql2;
						if (auth.equals("zongjingli")){
							sql = "select sum(orderamount) as OrderAmount, sum(GuideAmount) as GuideAmount, sum(OrderProductNumber) as OrderProductNumber, (10) as Discount from user_info where authority like 'diancang%'";
							sql2 = "select Name,BudgetAmount,OrderAmount, GuideAmount, OrderProductNumber,OrderStyleNumber from user_info where authority like '%diancang%' and OrderAmount > 0 order by OrderAmount desc";
						} else {
							sql = "select sum(orderamount) as OrderAmount, sum(GuideAmount) as GuideAmount, sum(OrderProductNumber) as OrderProductNumber, sum(BudgetAmount) as BudgetAmount from user_info where authority like 'diancang%' and   superareaid="+userId;
							sql2 = "select Name,BudgetAmount,OrderAmount, GuideAmount, OrderProductNumber,OrderStyleNumber from user_info where superareaid="+userId+" and authority like 'diancang%' and OrderAmount > 0 order by OrderAmount desc";
						}
						ResultSet rs1  = stmt.executeQuery(sql);
						//System.out.println(sql);
						if (rs1.next())
							jsonObj = this.resultToJson(rs1, jsonObj);
						ResultSet rs2 = stmt.executeQuery(sql2);
						//System.out.println(sql2);
						JSONArray jsonArray = new JSONArray();  
						jsonArray = this.resultSetToJson(rs2, jsonArray);
						jsonObj.put("Top10Diancang", jsonArray);
					}
					sql = "select count(*) as productTotalNumber from products";
					//System.out.println(sql);
					rs = stmt.executeQuery(sql);
					if (rs.next())
						jsonObj.put("productTotalNumber", rs.getString("productTotalNumber"));
					stmt.executeUpdate("update user_info set logined=0 where id="+userId);
				} else 
					jsonObj.put("ResultCode", "2");
				
			} else { 
				jsonObj.put("ResultCode", "1");
			}
		} catch (SQLException | JSONException e) {
			jsonObj.put("ResultCode", "3");
			
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

//    @GET
//	@Path("loginOut")
//	public JSONObject userLoginOut(@QueryParam("userId") String userId) {
//    	JSONObject jsonObj = new JSONObject();  
//	    DBConnPool pool = new DBConnPool();
//		try {
//			Statement stmt = pool.conn.createStatement();
//
//			String sql = String.format("update user_info set  logined=0 where id="+userId);
//			//System.out.println(sql);
//			stmt.executeUpdate(sql);  
//		} catch (SQLException e) {
//			
//		// TODO Auto-generated catch block
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
//		return jsonObj;
//    }
 
    @GET
	@Path("downloadAllPicture")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public JSONArray downloadAllPicture(@QueryParam("password") String password) throws JSONException{
    	JSONArray jsonArray = new JSONArray();
	    if (password.equals("da")){
			DBConnPool pool = new DBConnPool();
			try {
				Statement stmt = pool.conn.createStatement();
				ResultSet rs = stmt.executeQuery("select Pictures from products");
				String[] picPaths;
		    	while (rs.next()) {  
		    		picPaths = rs.getString("pictures").split(",");
		    		for(int i = 0; i<picPaths.length; i++) 
		    			jsonArray.put(picPaths[i]);  
		    	}  
		    	rs = stmt.executeQuery("select MatchPicture from matches");
		    	while (rs.next()) {  
		    		picPaths = rs.getString("MatchPicture").split(",");
		    		for(int i = 0; i<picPaths.length; i++) 
		    			jsonArray.put(picPaths[i]);  
		    	}  
		    	rs = stmt.executeQuery("select PlacePicture from place_display");
		    	while (rs.next()) {  
		    		picPaths = rs.getString("PlacePicture").split(",");
		    		for(int i = 0; i<picPaths.length; i++) 
		    			jsonArray.put(picPaths[i]);  
		    	}  
			} catch (SQLException e) {
				
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
    	} 
		return jsonArray;
	}
    
//    @GET
//	@Path("getBudget")
//	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
//	public JSONObject getUserBudget(@QueryParam("userId") String userId) throws JSONException{
//	    JSONObject jsonObj = new JSONObject();  
//		DBConnPool pool = new DBConnPool();
//		try {
//			Statement stmt = pool.conn.createStatement();
//			String sql;
//			if (userId.startsWith("diancang:")){
//				sql = String.format("SELECT a.*, b.* FROM (select * from user_info where id='%s') as a left join order_guide as b on a.OrderGuideId=b.Id"
//					, userId.split(":")[1]);
//				//System.out.println(sql);
//				ResultSet rs = stmt.executeQuery(sql);
//				if (rs.next())
//					jsonObj = this.resultToJson(rs, jsonObj);
//			} else if (userId.contains("zongdai")) {
//				sql = "select sum(orderamount) as OrderAmount, sum(guideamount) as GuideAmount, sum(budgetAmount) as BudgetAmount from user_info where areaid="+userId.split(":")[1];
//				//System.out.println(sql);
//				ResultSet rs = stmt.executeQuery(sql);
//				if (rs.next())
//					jsonObj = this.resultToJson(rs, jsonObj);
//				ResultSet diancangBudgets = stmt.executeQuery("select a.Id,Name,OrderAmount, MinOrderAmount, GuideAmount, BudgetAmount,SerialOrderBudget,TypeOrderBudget,ClassOrderBudget, MucaiOrderBudget"
//						+ ",  ColorOrderBudget,SizeOrderBudget,PricezoneOrderBudget, b.* from (select * from user_info where AreaId="+ userId.split(":")[1]+") as a left join order_guide as b on a.OrderGuideId=b.id");
//				JSONArray jsonArray = new JSONArray();  
//				jsonArray = this.resultSetToJson(diancangBudgets, jsonArray);
//				jsonObj.put("DiancangBudget", jsonArray);
//			} else if (userId.startsWith("zongjingli:")) {
//				ResultSet rs = stmt.executeQuery("select sum(OrderAmount) as OrderAmount from user_info where authority='diancang'");
//				if (rs.next())
//					jsonObj = this.resultToJson(rs, jsonObj);
//				rs = stmt.executeQuery("select BudgetAmount from user_info where id="+userId.split(":")[1]);
//				if (rs.next())
//					jsonObj = this.resultToJson(rs, jsonObj);
//				ResultSet top10Diancang = stmt.executeQuery("select Name,GuideAmount, BudgetAmount,OrderAmount, OrderProductNumber,OrderStyleNumber from user_info where authority='diancang' and OrderAmount > 0 order by OrderAmount desc limit 10");
//				JSONArray jsonArray = new JSONArray();  
//				jsonArray = this.resultSetToJson(top10Diancang, jsonArray);
//				jsonObj.put("Top10Diancang", jsonArray);
//			}
//		} catch (SQLException | JSONException e) {
//			
//		// TODO Auto-generated catch block
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
//
//		return jsonObj;
//	}

	@GET
    @Path("currentProgress")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public JSONObject getCurrentProgress() {
	  	JSONObject jsonObj = new JSONObject();  
	  	DBConnPool pool = new DBConnPool();
		try {
			Statement stmt = pool.conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM progress where IsOn is true" );  
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

    @POST
	@Path("addComments")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject addComments(MultivaluedMap<String, String> updateParams) throws JSONException{
		String pId = updateParams.getFirst("ProductId");
		String author = updateParams.getFirst("Author");
		String comments = updateParams.getFirst("Comments");
	    JSONObject jsonObj = new JSONObject();  
	    DBConnPool pool = new DBConnPool();
		try {
			Statement stmt = pool.conn.createStatement();

			String sql = String.format("insert into comments (ProductId, Author, Comments) VALUES ('%s', '%s', '%s')", pId, author, comments);
			//System.out.println(sql);
			int result = stmt.executeUpdate(sql);  
			if (result==1){ 
				jsonObj.put("RestultCode", "0");
			}
		} catch (SQLException | JSONException e) {
			
		// TODO Auto-generated catch block
			e.printStackTrace();
			jsonObj.put("RestultCode", "1");
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

    @POST
	@Path("updateBudget")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject updateUserBudget(MultivaluedMap<String, String> updateParams) throws JSONException{
		String budgetUserId = updateParams.getFirst("budgetUserId");
		String budgetType = updateParams.getFirst("budgetType");
		String budgetDetails = updateParams.getFirst("budgetDetails");
		String budgetAmount = updateParams.getFirst("budgetAmount");
	    JSONObject jsonObj = new JSONObject();  
	    DBConnPool pool = new DBConnPool();
		try {
			Statement stmt = pool.conn.createStatement();

			String sql = String.format("update user_info set BudgetAmount='%s', %s='%s' where id='%s'", budgetAmount, budgetType, budgetDetails, budgetUserId);
			//System.out.println(sql);
			int result = stmt.executeUpdate(sql);  
			if (result==1){ 
				jsonObj.put("RestultCode", "0");
			}
		} catch (SQLException | JSONException e) {
			
		// TODO Auto-generated catch block
			e.printStackTrace();
			jsonObj.put("RestultCode", "1");
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
    @Path("updateCollection")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject updateCollection(@QueryParam("collectionSet") String collectionSet, @QueryParam("userId") String userId) throws JSONException {
	  	JSONObject jsonObj = new JSONObject();  
	    jsonObj.put("ResultCode", "1");
	    DBConnPool pool = new DBConnPool();
		try {
			Statement stmt = pool.conn.createStatement();
			String sql = String.format("update user_info set collectionSet='%s' where id=%s", collectionSet, userId.split(":")[1]);
			//System.out.println(sql);
			int result = stmt.executeUpdate(sql);  
			if (result == 1)
				jsonObj.put("ResultCode", "0");
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
	@Path("getFairInfo")
	@Produces(MediaType.APPLICATION_JSON+ ";charset=utf-8")
	public JSONObject getFairInfo() throws JSONException {
	  	JSONObject jsonObj = new JSONObject();  
	  	DBConnPool pool = new DBConnPool();
		try {
			Statement stmt = pool.conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM fair_info limit 0,1" );  
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

	public JSONArray resultSetToJson(ResultSet rs, JSONArray array) throws SQLException,JSONException  
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
    		array.put(jsonObj);   
    	}  
    	
    	return array;  
    }   

	public JSONObject resultToJson(ResultSet rs, JSONObject jsonObj) throws SQLException,JSONException  
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
