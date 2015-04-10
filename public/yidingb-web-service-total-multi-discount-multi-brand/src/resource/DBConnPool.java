package resource;
import java.sql.Connection;  
import java.sql.SQLException;  
import java.sql.Statement;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

//import com.jolbox.bonecp.BoneCP;  
//import com.jolbox.bonecp.BoneCPConfig;  
   
public class DBConnPool {  
	public Connection conn = null;
	public Statement stmt =null;
    public DBConnPool() {
		try {
			Context ctx = new InitialContext();
			DataSource ds=(DataSource)ctx.lookup("java:/comp/env/jdbc/mysql");
			conn= ds.getConnection();
			stmt = conn.createStatement();
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }  
    
} 