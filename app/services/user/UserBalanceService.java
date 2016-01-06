package services.user;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import utils.GlobalDBControl;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.PagedList;
import com.avaje.ebean.Query;

import form.UserBalanceForm;
import models.user.UserAddress;
import models.user.UserBalance;
import models.user.UserBalanceLog;

public class UserBalanceService{
	public static UserBalanceService instance=new UserBalanceService();
	private UserBalanceService(){}
	
	//添加余额信息
	public void addbalance(UserBalance ub){
		Ebean.getServer(GlobalDBControl.getDB()).save(ub);
	}
	//根据用户编号获取余额信息
	public UserBalance getBalanceByUid(int uid){
		return Ebean.getServer(GlobalDBControl.getDB()).find(UserBalance.class).where()
				.eq("uid", uid).findUnique();
	}
	
	//修改余额
	public void updateBalance(int uid,int balance){
		String sql="update user_balance set balance=balance+"+balance+",date_upd=NOW() where uid="+uid;
		Ebean.getServer(GlobalDBControl.getDB()).createSqlUpdate(sql).execute();
	}
	
	//添加余额变动日志
	public void addbalancelog(UserBalanceLog blg){
		Ebean.getServer(GlobalDBControl.getDB()).save(blg);
	}
	
	//根据用户编号获取余额变动列表
	public List<UserBalanceLog> getbalanceloglist(Long uid){
		return Ebean.getServer(GlobalDBControl.getDB()).find(UserBalanceLog.class)
				.where().eq("uid",uid).findList();
	}

	public PagedList<UserBalanceLog> findUserBalanceLogList(
			UserBalanceForm formPage) {
		List<String> sqlList = new ArrayList<>();
		sqlList.add("endbalance > beforebalance ");
		List<Object> paramsList = new ArrayList<>();
		Query<UserBalanceLog> query = Ebean.getServer(GlobalDBControl.getDB()).createQuery(UserBalanceLog.class);
		query.where(StringUtils.join(sqlList, " AND "));
		
		int i = 1; // first param uses index 1 NOT 0!
		for (Object param : paramsList) {
		    query.setParameter(i, param);
		    i++;
		}
		PagedList<UserBalanceLog> xyzList = query.orderBy("id desc").findPagedList(formPage.page, formPage.size);
		return xyzList;
	}
	
}