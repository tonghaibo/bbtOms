package services.statistics;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.PagedList;
import com.avaje.ebean.Query;
import com.google.common.base.Strings;
import form.StatsDailyForm;
import models.statistics.StatDailyX7;
import org.apache.commons.lang3.StringUtils;
import play.Logger;
import play.Logger.ALogger;
import services.ServiceFactory;
import utils.GlobalDBControl;

import java.util.ArrayList;
import java.util.List;

/**
 * X7服务类
 * @author chenxi
 */
public class StatsDailyX7Service {

	private static final StatsDailyX7Service statsDailyX7Service = ServiceFactory.getStatsDailyX7Service();
	private static StatsDailyX7Service instance = null;
	private static ALogger logger = Logger.of(StatsDailyX7Service.class);

	public static StatsDailyX7Service getInstance() {
		if (instance == null) {
			syncInit();
		}
		return instance;
	}

	private StatsDailyX7Service() {

	}

	private static synchronized void syncInit() {
		if (instance == null) {
			instance = new StatsDailyX7Service();
		}
	}

	public PagedList<StatDailyX7> findStatsDailyX7List(StatsDailyForm formPage) {
		List<String> sqlList = new ArrayList<>();
		List<Object> paramsList = new ArrayList<>();
		//统计时间
		if (formPage.between != null) {
            sqlList.add("stat_date between date_format(?,'%Y-%m-%d') ");
            sqlList.add(" date_format(?,'%Y-%m-%d') ");
			paramsList.add(formPage.between.start);
			paramsList.add(formPage.between.end);
		}

		Query<StatDailyX7> query = Ebean.getServer(GlobalDBControl.getDB()).createQuery(StatDailyX7.class);
		query.where(StringUtils.join(sqlList, " AND "));

		int i = 1; // first param uses index 1 NOT 0!
		for (Object param : paramsList) {
		    query.setParameter(i, param);
		    i++;
		}

		PagedList<StatDailyX7> xyzList = query.orderBy("stat_date desc").findPagedList(formPage.page, formPage.size);
		
		return xyzList;
	}

	public StatDailyX7 findById(Long id) {
		return Ebean.getServer(GlobalDBControl.getDB()).find(StatDailyX7.class).where().eq("id", id).findUnique();
	}

    public StatDailyX7 findByStatDate (String statDate) {
        return Ebean.getServer(GlobalDBControl.getDB()).find(StatDailyX7.class).where().eq("stat_date", statDate).findUnique();
    }

    public void save(StatDailyX7 statDailyX7) {
        Ebean.getServer(GlobalDBControl.getDB()).save(statDailyX7);
    }

}
