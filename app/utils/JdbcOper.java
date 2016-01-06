package utils;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Transaction;

import play.Logger;
import play.Play;

public class JdbcOper {
	public static String url = "jdbc:mysql://127.0.0.1/bbt?useUnicode=true&characterEncoding=UTF-8";  
    public static final String name = "com.mysql.jdbc.Driver";  
    public static String user = "yangtao";  
    public static String password = "neolix";  
	public Connection  con      = null;
	public CallableStatement cst=null;
	public ResultSet rs         = null;
	public PreparedStatement pst = null;  
	Transaction tran ;
	
	private static JdbcOper instance = null;  
	  
    /* 静态工程方法，创建实例 */  
    public static JdbcOper getInstance() {  
    	instance = new JdbcOper(); 
        return instance;   
    }  
    
	//获取一个数据库连接的操作Object,并且包装了存储过程的调用
	public static JdbcOper getCalledbleDao(String sql)
	{
		JdbcOper op = new JdbcOper();
		try{
			op.tran = Ebean.getServer( Constants.getDB() ).beginTransaction();
			op.con  = op.tran.getConnection();
			op.cst  = op.con.prepareCall(sql);
		}catch(Exception e)
		{
			Logger.info( "getCalledbleDao JdbcOper error - "+e.toString() );
			e.printStackTrace();
			op.close();
			return null;
		}
		finally{
		}
		return op;
	}
	
	public static JdbcOper getPrepareStateDao(String sql) { 
	    JdbcOper op = new JdbcOper();
		try{
			op.tran = Ebean.getServer( Constants.getDB() ).beginTransaction();
			op.con  = op.tran.getConnection();
			op.pst = op.con.prepareStatement(sql);//准备执行语句  
		} catch (Exception e) {  
            e.printStackTrace();  
            op.close();
            Logger.info( "getPrepareStateDao error"+e.toString()+"  ---!!! "+sql );
        } finally{
        	
        }
        return op;
    } 
	
	//释放数据库连接；
	public void close()
	{
		try{
			if(tran!=null) tran.commit();
			if(tran!=null) tran.end();
			if(rs!=null) rs.close();
			if(cst!=null) cst.close();
			if(con!=null)con.close();
			if(pst!=null){
				pst.close();
			} 
		}
		catch(Exception e){
			Logger.info(" JdbcOper close--"+e.toString());
		}
		finally{
			tran = null;
			rs   = null;
			cst  = null;
			con  = null;
			pst=null;
		}
	}
}
