package services.statistics;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.PagedList;
import com.avaje.ebean.Query;
import com.google.common.base.Strings;
import form.StatsDailyForm;
import models.statistics.StatDailyUser;
import org.apache.commons.lang3.StringUtils;
import play.Logger;
import play.Logger.ALogger;
import services.ServiceFactory;
import utils.GlobalDBControl;

import java.util.ArrayList;
import java.util.List;

/**
 * 用户服务类
 * @author chenxi
 */
public class StatsDailyUserService {

	private static final StatsDailyUserService statsDailyUserService = ServiceFactory.getStatsDailyUserService();
	private static StatsDailyUserService instance = null;
	private static ALogger logger = Logger.of(StatsDailyUserService.class);

	public static StatsDailyUserService getInstance() {
		if (instance == null) {
			syncInit();
		}
		return instance;
	}

	private StatsDailyUserService() {

	}

	private static synchronized void syncInit() {
		if (instance == null) {
			instance = new StatsDailyUserService();
		}
	}

	public PagedList<StatDailyUser> findStatsDailyUserList(StatsDailyForm formPage) {
		List<String> sqlList = new ArrayList<>();
		List<Object> paramsList = new ArrayList<>();
		//统计时间
		if (formPage.between != null) {
            sqlList.add("stat_date between date_format(?,'%Y-%m-%d') ");
            sqlList.add(" date_format(?,'%Y-%m-%d') ");
			paramsList.add(formPage.between.start);
			paramsList.add(formPage.between.end);
		}

		Query<StatDailyUser> query = Ebean.getServer(GlobalDBControl.getDB()).createQuery(StatDailyUser.class);
		query.where(StringUtils.join(sqlList, " AND "));

		int i = 1; // first param uses index 1 NOT 0!
		for (Object param : paramsList) {
		    query.setParameter(i, param);
		    i++;
		}

		PagedList<StatDailyUser> xyzList = query.orderBy("stat_date desc").findPagedList(formPage.page, formPage.size);
		
		return xyzList;
	}

	public StatDailyUser findStatsDailyUserById(Long id) {
		return Ebean.getServer(GlobalDBControl.getDB()).find(StatDailyUser.class).where().eq("id", id).findUnique();
	}

    public StatDailyUser findByStatDate (String statDate) {
        return Ebean.getServer(GlobalDBControl.getDB()).find(StatDailyUser.class).where().eq("stat_date", statDate).findUnique();
    }

    public void save(StatDailyUser statDailyUser) {
        Ebean.getServer(GlobalDBControl.getDB()).save(statDailyUser);
    }

}
