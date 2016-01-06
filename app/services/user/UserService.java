package services.user;

import java.text.SimpleDateFormat;
import java.util.Date;

import play.Logger;

import com.avaje.ebean.Ebean;

import utils.GlobalDBControl;
import utils.JdbcOper;
import utils.Numbers;
import models.CountH5;
import models.user.UserInfo;

public class UserService {
	private static final Logger.ALogger LOGGER = Logger.of(UserService.class);
	private static final SimpleDateFormat CHINESE_DATE_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static final SimpleDateFormat CHINESE_DATE = new SimpleDateFormat("yyyyMM");

	public static UserService instance=new UserService();
	private UserService(){}
	
	//保存用户
	public void addUser(UserInfo user){
		Ebean.getServer(GlobalDBControl.getDB()).save(user);
	}

	//根据OPENID查询用户
	public UserInfo getUserByopenid(String openid){
		return Ebean.getServer(GlobalDBControl.getDB()).find(UserInfo.class).where()
				.eq("openid", openid)
				.findUnique();
	}
	//修改妮称
	public void editNickname(Long uid,String nickname){
		String sql="update user_info set nickname='"+nickname+"' where uid="+uid.longValue();
		Ebean.getServer(GlobalDBControl.getDB()).createSqlUpdate(sql).execute();
	}
	
	//修改经纬度
	public void updatelatlng(Integer uid,String lat,String lng){
		Double latn=Numbers.parseDouble(lat, 0D);
		Double lngn=Numbers.parseDouble(lng, 0D);
		if(latn>0 && lngn>0 && uid>0){
			String sql="update user_info set lat="+lat+",lng="+lng+" where uid="+uid.intValue();
			Ebean.getServer(GlobalDBControl.getDB()).createSqlUpdate(sql).execute();
		}
	}
	public CountH5 saveCount(CountH5 countH5) {
		//按月生成表
		String tablename=CHINESE_DATE.format(new Date());
		if(countH5!=null){
			if(countH5.getChannel()==null)
				countH5.setChannel("");
			if(countH5.getIp()==null)
				countH5.setIp("");
			if(countH5.getIswx()==null)
				countH5.setIswx("");
			if(countH5.getShareType()==null)
				countH5.setShareType("");
			if(countH5.getUrl()==null)
				countH5.setUrl("");
			if(countH5.getUserId()==null)
				countH5.setUserId(0L);
			if(countH5.getOpenid()==null)
				countH5.setOpenid("");
		}
		String sql="{call `sp_create_logtable`('count_h5"+tablename+"','"+countH5.getChannel()+"','"+countH5.getIp()+"','"+countH5.getIswx()+"','"+countH5.getShareType()+"','"+countH5.getUrl()+"','"+countH5.getUserId()+"','"+countH5.getOpenid()+"')}";		
		JdbcOper db = JdbcOper.getCalledbleDao(sql);
		try{		
			db.cst.execute();
		}catch(Exception e){}
		finally{
			if(db!=null)
				db.close();
		}
		//写文件
		String logstr=CHINESE_DATE_TIME_FORMAT.format(new Date())+"|"+countH5.getIp()+"|H5|H5|H5.1.0||"+countH5.getUrl()+"|"+countH5.getChannel();
		Logger.info(logstr);
		return countH5;
	}
}