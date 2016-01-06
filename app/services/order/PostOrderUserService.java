package services.order;

import java.util.List;

import utils.GlobalDBControl;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.SqlRow;

import models.order.PostOrderUser;
import models.user.UserInfo;

public class PostOrderUserService{
	public PostOrderUserService instance=new PostOrderUserService();
	private PostOrderUserService(){}
	
	//根据订单号查询订单用户
	public PostOrderUser getorderuser(Integer orderid){
		return Ebean.getServer(GlobalDBControl.getDB()).find(PostOrderUser.class)
				.where().eq("orderid", orderid).findUnique();
	}
	//根据订单号查询订单用户
	public UserInfo getorderUserInfo(Integer orderid){
		String sql="select u.* from user_info u,post_order_user pu where"
				+ " u.uid=pu.uid and and pu.orderid="+orderid;
		List<SqlRow> rslist=Ebean.getServer(GlobalDBControl.getDB()).createSqlQuery(sql).findList();
		if(rslist==null || rslist.isEmpty())
			return null;
		UserInfo us=new UserInfo();
		SqlRow rs=rslist.get(0);
		us.setUid(rs.getInteger("uid"));
		us.setNickName(rs.getString("nickName"));
		us.setOpenid(rs.getString("openid"));
		us.setDate_new(rs.getDate("date_new"));
		us.setDate_upd(rs.getDate("date_upd"));
		return us;
	}
	
	//添加订单用户 
	public void addorderuser(PostOrderUser orderuser){
		Ebean.getServer(GlobalDBControl.getDB()).save(orderuser);
	}
	//查询订单用户是否存在
	public PostOrderUser getPOUByOrderidUid(Integer orderid,Integer uid){
		return Ebean.getServer(GlobalDBControl.getDB()).find(PostOrderUser.class)
				.where().eq("orderid", orderid).eq("uid", uid).findUnique();
	}
	
	//根据状态查询快递员所有所有承单列表
	public List<PostOrderUser> getPOUByPostman(Integer postmanid,Integer status){
		if(status==0 || status==null)
			return Ebean.getServer(GlobalDBControl.getDB()).find(PostOrderUser.class)
				.where().eq("postmanid", postmanid).findList();
		return Ebean.getServer(GlobalDBControl.getDB()).find(PostOrderUser.class)
				.where().eq("postmanid", postmanid).eq("status", status).findList();
	}
	
	//根据状态查询快递员未配送订单数量
	public Integer getPOUBCounts(Integer postmanid,Integer status){
		String sql="select count(1) as c from post_order_user where postmanid="+postmanid;
		if(status!=null && status>0)
			sql=sql+" and status="+status;
		SqlRow rs=Ebean.getServer(GlobalDBControl.getDB()).createSqlQuery(sql).findUnique();
		if(rs==null)
			return 0;
		return rs.getInteger("c");
	}
	
	//查询某时间完成的派送数量,yyyy-mm-dd
	public Integer getnosentCount(Integer postmanid,String timstr){
		String sql="select count(1) as c from post_order_user where postmanid="+postmanid
				+" and date_upd>='"+timstr+" 00:00:00' and date_upd<='"+timstr+" 23:59:59'"
				+" and status=0";
		SqlRow rs=Ebean.getServer(GlobalDBControl.getDB()).createSqlQuery(sql).findUnique();
		if(rs==null)
			return 0;
		return rs.getInteger("c");
	}
}