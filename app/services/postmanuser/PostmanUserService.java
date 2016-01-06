package services.postmanuser;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
















import java.util.Map;

import models.PushInfo;
import models.order.PostOrder;
import models.postman.PostmanUser;
import models.postman.PostmanUserLocationLog;
import models.postman.PostmanUserTemp;
import models.user.UserInfo;

import org.apache.commons.lang3.StringUtils;

import play.Logger;
import play.Logger.ALogger;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Expr;
import com.avaje.ebean.Expression;
import com.avaje.ebean.PagedList;
import com.avaje.ebean.Query;
import com.avaje.ebean.RawSql;
import com.avaje.ebean.RawSqlBuilder;
import com.avaje.ebean.SqlRow;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import form.PostmanUserForm;
import services.ServiceFactory;
import services.baidu.BaiduService;
import utils.Constants;
import utils.GlobalDBControl;
import utils.JdbcOper;
import utils.Numbers;
import utils.StringUtil;

public class PostmanUserService {
	private static final SimpleDateFormat CHINESE_D_TIME_FORMAT = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");
	private static PostmanUserService instance = null;
	private static ALogger logger = Logger.of(PostmanUserService.class);

	public static PostmanUserService getInstance() {
		if (instance == null) {
			syncInit();
		}
		return instance;
	}

	private PostmanUserService() {

	}

	private static synchronized void syncInit() {
		if (instance == null) {
			instance = new PostmanUserService();
		}
	}

	public PagedList<PostmanUser> findPostmanUserList(PostmanUserForm formPage) {
		List<String> sqlList = new ArrayList<>();
		List<Object> paramsList = new ArrayList<>();
		if (formPage.companyid!=-1) {
			sqlList.add("companyid = ?");
			paramsList.add(formPage.companyid);
		}
		if (formPage.sta!=-1) {
			sqlList.add("sta = ?");
			paramsList.add(formPage.sta);
		}
		if (!Strings.isNullOrEmpty(formPage.nickname)) {
		    sqlList.add("nickname like ?");
		    paramsList.add("%"+formPage.nickname + "%");
		}
		if (!Strings.isNullOrEmpty(formPage.phone)) {
			sqlList.add("phone = ?");
			paramsList.add(formPage.phone);
		}

		Query<PostmanUser> query = Ebean.getServer(GlobalDBControl.getDB()).createQuery(PostmanUser.class);
		query.where(StringUtils.join(sqlList, " AND "));

		int i = 1; // first param uses index 1 NOT 0!
		for (Object param : paramsList) {
		    query.setParameter(i, param);
		    i++;
		}
		PagedList<PostmanUser> xyzList = query.orderBy("id desc").findPagedList(formPage.page, formPage.size);
		
		return xyzList;
	}

	public PostmanUser findPostmanUserById(int id) {
		return Ebean.getServer(GlobalDBControl.getDB()).find(PostmanUser.class).where().eq("id", id).findUnique();
	}


	public PostmanUser update(PostmanUser postmanUser) {
		Ebean.getServer(GlobalDBControl.getDB()).update(postmanUser);
		return postmanUser;
	}

	/**
	 * 
	 * <p>Title: initPostmanUser</p> 
	 * <p>Description: 用户审核通过，初始化信息</p> 
	 * @param postmanUser
	 */
	public void initPostmanUser(PostmanUser postmanUser) {
		String sql="CALL `sp_postmanuser_init`(?)";
		// 调用存储过程
		JdbcOper oper = JdbcOper.getCalledbleDao(sql);
		try {
			// 数据库存储过程操作
			oper.cst.setInt(1, postmanUser.getId());
			oper.rs = oper.cst.executeQuery();
		} catch (Exception e) {
			logger.info(e.toString());
		} finally {
			oper.close();
		}
	}

	//根据临时订单查询快递员信息列表
	public List<PostmanUser> getpostmanlist(Integer count,Integer orderid){
		String sql="select pu.id,pu.staffid,pu.nickname,pu.phone,"
				+"pu.headicon,pu.cardidno,pu.companyname,pu.companyid,"
				+"pu.substation,pu.alipay_account,pu.token,pu.bbttoken,"
				+"pu.lat,pu.lon,pu.height,pu.addr,pu.addrdes,pu.shopurl,"
				+"pu.sta,pu.date_new,pu.date_upd,pu.spreadticket from postmanuser "
				+"pu,postmanuser_temp p where pu.id=p.postmanid and p.orderid="+orderid+" limit "+count;
		RawSql rawSql = RawSqlBuilder.parse(sql).create();
		return Ebean.getServer(GlobalDBControl.getDB()).find(PostmanUser.class)
				.setRawSql(rawSql).findList();
	}
	
	//某距离内快递员的数量latlng-纬度x，经度y(百度算法)
	public Integer postmancount(Long meter,Map<String,Double> latlng){
		Date date=new Date();//取时间 
	     Calendar calendar= new GregorianCalendar(); 
	     calendar.setTime(date); 
	     calendar.add(calendar.DATE,1);//把日期往后增加一天.整数往后推,负数往前移动 
	     date=calendar.getTime();
		List<PostmanUser> pulist=Ebean.getServer(GlobalDBControl.getDB()).find(PostmanUser.class)
				.where().between("date_upd", CHINESE_D_TIME_FORMAT.format(date), new Date()).findList();
		if(pulist==null || pulist.isEmpty())
			return 0;
		Integer count=0;
		Integer n=0;
		n=pulist.size()/5;
		Integer m=pulist.size()%5;
		n=m>0?n+1:n;
		Map<String,String> beginpoint=new HashMap<String,String>();
		beginpoint.put("o1", latlng.get("x").doubleValue()+","+latlng.get("y").doubleValue());
		Map<String,String> points=new HashMap<String,String>();
		
		for(int i=0;i<n;i++){
			//取距离
			points.clear();
			for(int p=0;p<5;p++){
				points.put("latlng"+(5*m+p), pulist.get(5*m+p).getLat().doubleValue()+","+pulist.get(5*m+p).getLon().doubleValue());				
			}
			 Map<String,Long> distances=BaiduService.instance.getdisPointmore(beginpoint, points, "北京");
			  if(distances!=null && !distances.isEmpty()){
				  Iterator<String> key=distances.keySet().iterator();
				  int p=0;
				  while(key.hasNext()){
					  Long distc=distances.get(key.next());
					  if(distc.longValue()<=3000)
						  count++;
					  Logger.info(pulist.get(5*m+p).getId().toString()+"距离:"+distc.longValue());
					  p++;
					  
				  }
			  }
		}
		return count;
	}
	
	//计算距中心点某距离周边经纬度范围内快递数量i=1时公里数为１１１公里
	public Integer getPostMancount(Double x, Double y,Double i){
		Double minlat=x-i;
		Double maxlat=x+i;
		Double minlong=y-i;
		Double maxlong=y+i;
		
		String sql="select count(1) as count from (SELECT id,SQRT(POWER("+x+" - lat, 2) + POWER("+y+" - lon, 2)) AS d FROM postmanuser "
				+"WHERE (lat BETWEEN "+minlat+" AND "+maxlat+") AND (lon BETWEEN "+minlong+" AND "+maxlong
						+ ") AND SQRT(POWER("+x+" - lat, 2) + POWER("+y+" - lon, 2)) < "+i+") a";
		
		SqlRow rs=Ebean.getServer(GlobalDBControl.getDB()).createSqlQuery(sql).findUnique();
		if(rs==null)
			return 0;
		return rs.getInteger("count");
	}
	
	/**
	 * 计算距中心点某距离周边经纬度范围内快递数量
	 * @param x 纬度
	 * @param y 经度
	 * @param meter 米
     * @return
     */
	public List<PostmanUser> getPostManlist(double x, double y,long meter,Integer orderid){
		MathContext mc = new MathContext(2, RoundingMode.HALF_DOWN);
		double i = new BigDecimal(meter).divide(new BigDecimal(111), mc).doubleValue();
		double minlat=x-i;
		double maxlat=x+i;
		double minlong=y-i;
		double maxlong=y+i;
		
		String sql="SELECT u.* ,SQRT(POWER("+x+" - u.lat, 2) + POWER("+y+" - u.lon, 2)) AS d FROM postmanuser u WHERE (u.lat BETWEEN "+minlat+" AND "+maxlat+") AND (u.lon BETWEEN "+minlong+" AND "+maxlong+") AND u.id NOT IN(SELECT postmanid FROM postmanuser_temp pt WHERE pt.`orderid`="+orderid+") HAVING d < 27.0";
		
		List<SqlRow> rslist=Ebean.getServer(GlobalDBControl.getDB()).createSqlQuery(sql).findList();
		if(rslist==null || rslist.isEmpty())
			return null;
		List<PostmanUser> pulist=new ArrayList<PostmanUser>();
		for(SqlRow rs:rslist){
			pulist.add(this.buildrspostman(rs));
		}
		return pulist;
	}
	
	//根据订单查询快递员信息
	public PostmanUser getpostmanbyorder(Integer orderid){
		String sql="select pu.id,pu.`staffid`,pu.`nickname`,pu.`phone`,"
				+"pu.`headicon`,pu.`cardidno`,pu.`companyname`,pu.`companyid`,"
				+"pu.`substation`,pu.`alipay_account`,pu.`token`,pu.`bbttoken`,"
				+"pu.`lat`,pu.`lon`,pu.`height`,pu.`addr`,pu.`addrdes`,pu.`shopurl`,"
				+"pu.`sta`,pu.`date_new`,pu.`date_upd`,pu.`spreadticket` from postmanuser pu,"
				+"post_order_user po where pu.id=po.postmanid and po.orderid="+orderid.intValue();
//		RawSql rawSql = RawSqlBuilder.parse(sql).create();
//		return Ebean.getServer(GlobalDBControl.getDB()).find(PostmanUser.class)
//				.setRawSql(rawSql).findUnique();
		SqlRow rs=Ebean.getServer(GlobalDBControl.getDB()).createSqlQuery(sql).findUnique();
		PostmanUser post=null;
		if(rs!=null){
			buildrspostman(rs);
		}
		return post;
	}
	
	private PostmanUser buildrspostman(SqlRow rs){
		PostmanUser post=new PostmanUser();
		post.setId(rs.getInteger("id"));
		post.setStaffid(rs.getString("staffid"));
		post.setNickname(rs.getString("nickname"));
		post.setPhone(rs.getString("phone"));
		post.setHeadicon(rs.getString("headicon"));
		post.setCardidno(rs.getString("cardidno"));
		post.setCompanyname(rs.getString("companyname"));
		post.setCompanyid(rs.getInteger("companyid"));
		post.setSubstation(rs.getString("substation"));
		post.setAlipayAccount(rs.getString("alipayAccount"));
		post.setToken(rs.getString("token"));
		post.setBbttoken(rs.getString("bbttoken"));
		post.setDateNew(rs.getDate("date_new"));
		post.setDateUpd(rs.getDate("date_upd"));
		post.setLat(rs.getDouble("lat"));
		post.setLon(rs.getDouble("lon"));
		return post;
	}
	
	//添加待push快递员，下单后使用或快递员在本单有效期内位置发生变动使用
	public void savetemp(PostmanUserTemp pt){
		Ebean.getServer(GlobalDBControl.getDB()).save(pt);
	}
	
	//快递员位置变动或订单接单使用
	public void delposttemp(Integer postmanid,Integer orderid){
		String sql="delete from postmanuser_temp where orderid="+orderid+" and postmanid="+postmanid;
		Ebean.getServer(GlobalDBControl.getDB()).createSqlUpdate(sql).execute();
	}
	
	//添加pushinfo
	public void savepushinfo(PushInfo push){
		Ebean.getServer(GlobalDBControl.getDB()).save(push);
	}

	/**
	 * 根据用户ID获取该用户的地理位置信息
	 * @param postmanid
	 * @return
     */
	public List<PostmanUserLocationLog> findPostmanUserLocationLogByUid(int postmanid) {
		return Ebean.getServer(GlobalDBControl.getDB()).find(PostmanUserLocationLog.class).where().eq("postmanid",postmanid).findList();
	}
}
