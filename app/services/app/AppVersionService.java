package services.app;

import java.sql.ResultSet;

import models.app.AppVersion;
import play.Logger;
import play.Logger.ALogger;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.PagedList;
import com.avaje.ebean.Query;
import com.google.common.base.Strings;

import form.AppVersionForm;
import services.ServiceFactory;
import utils.GlobalDBControl;
import utils.JdbcOper;
import utils.Numbers;
import vo.BalanceWithdrawVo;

/**
 * 
 * <p>Title: AppVersionService.java</p> 
 * <p>Description: APP版本升级用</p> 
 * <p>Company: higegou</p> 
 * @author  ctt
 * date  2015年11月24日  上午11:10:22
 * @version
 */
public class AppVersionService {
	private static final AppVersionService productService = ServiceFactory.getAppVersionService();
	private static AppVersionService instance = null;
	private static ALogger logger = Logger.of(AppVersionService.class);

	public static AppVersionService getInstance() {
		if (instance == null) {
			syncInit();
		}
		return instance;
	}

	private AppVersionService() {

	}

	private static synchronized void syncInit() {
		if (instance == null) {
			instance = new AppVersionService();
		}
	}

	public PagedList<AppVersion> findAppVersionList(AppVersionForm formPage) {
		/*List<String> sqlList = new ArrayList<>();
		List<Object> paramsList = new ArrayList<>();*/
		Query<AppVersion> query = Ebean.getServer(GlobalDBControl.getDB()).createQuery(AppVersion.class);
/*		query.where(StringUtils.join(sqlList, " AND "));

		int i = 1; // first param uses index 1 NOT 0!
		for (Object param : paramsList) {
		    query.setParameter(i, param);
		    i++;
		}
*/		PagedList<AppVersion> xyzList = query.orderBy("id desc").findPagedList(formPage.page, formPage.size);
		return xyzList;
	}

	public AppVersion findAppVersionById(Long id) {
		return Ebean.getServer(GlobalDBControl.getDB()).find(AppVersion.class).where().eq("id", id).findUnique();
	}

	/**
	 * 
	 * <p>Title: save</p> 
	 * <p>Description: 保存版本信息</p> 
	 * @param appVersion
	 * @return
	 */
	public AppVersion save(AppVersion appVersion) {
		Ebean.getServer(GlobalDBControl.getDB()).save(appVersion);
		return appVersion;
	}

	public AppVersion update(AppVersion appVersion) {
		Ebean.getServer(GlobalDBControl.getDB()).update(appVersion);
		return appVersion;
	}

	/**
	 * 
	 * <p>Title: updateReddotWithReddot</p> 
	 * <p>Description: 修改红点显示</p> 
	 * @param i
	 */
	public void updateReddotWithReddot(AppVersion appVersion) {
		String sql="CALL `sp_version_reddot`(?)";
		// 调用存储过程
		JdbcOper oper = JdbcOper.getCalledbleDao(sql);
		try {
			// 数据库存储过程操作
			oper.cst.setInt(1, appVersion.getId());
			oper.rs = oper.cst.executeQuery();
		} catch (Exception e) {
			logger.info(e.toString());
		} finally {
			oper.close();
		}
	}
	
}
