package services.statistics;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.PagedList;
import com.avaje.ebean.Query;
import com.google.common.base.Strings;
import form.StatsDailyForm;
import models.statistics.StatDailyPostmanTask;
import models.statistics.StatDailyPostmanTaskDetail;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import play.Logger;
import play.Logger.ALogger;
import services.ServiceFactory;
import utils.GlobalDBControl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static utils.Dates.getEndOfDay;

/**
 * 快递员任务服务类
 * @author chenxi
 */
public class StatsDailyPostmanTaskService {

	private static final StatsDailyPostmanTaskService statsDailyPostmanTaskService = ServiceFactory.getStatsDailyPostmanTaskService();
	private static StatsDailyPostmanTaskService instance = null;
	private static ALogger logger = Logger.of(StatsDailyPostmanTaskService.class);

	public static StatsDailyPostmanTaskService getInstance() {
		if (instance == null) {
			syncInit();
		}
		return instance;
	}

	private StatsDailyPostmanTaskService() {

	}

	private static synchronized void syncInit() {
		if (instance == null) {
			instance = new StatsDailyPostmanTaskService();
		}
	}

	public PagedList<StatDailyPostmanTask> findStatsDailyPostmanTaskList(StatsDailyForm formPage) {
		List<String> sqlList = new ArrayList<>();
        List<Object> paramsList = new ArrayList<>();
		//统计时间
		if (formPage.between != null) {
            sqlList.add("stat_date between date_format(?,'%Y-%m-%d') ");
            sqlList.add(" date_format(?,'%Y-%m-%d') ");
			paramsList.add(formPage.between.start);
            paramsList.add(formPage.between.end);
		}

		Query<StatDailyPostmanTask> query = Ebean.getServer(GlobalDBControl.getDB()).createQuery(StatDailyPostmanTask.class);
		query.where(StringUtils.join(sqlList, " AND "));

		int i = 1; // first param uses index 1 NOT 0!
		for (Object param : paramsList) {
		    query.setParameter(i, param);
		    i++;
		}

		PagedList<StatDailyPostmanTask> xyzList = query.orderBy("stat_date desc").findPagedList(formPage.page, formPage.size);
		
		return xyzList;
	}

	public StatDailyPostmanTask findStatsDailyPostmanTaskById(Long id) {
		return Ebean.getServer(GlobalDBControl.getDB()).find(StatDailyPostmanTask.class).where().eq("id", id).findUnique();
	}

    public StatDailyPostmanTask findByStatDate (String statDate) {
        return Ebean.getServer(GlobalDBControl.getDB()).find(StatDailyPostmanTask.class).where().eq("stat_date", statDate).findUnique();
    }

    public StatDailyPostmanTaskDetail findByStatDateAndTaskId (String statDate,long taskId) {
        HashMap con = new HashMap();
        con.put("stat_date",statDate);
        con.put("task_id",taskId);
        return Ebean.getServer(GlobalDBControl.getDB()).find(StatDailyPostmanTaskDetail.class).where().eq("stat_date", statDate).eq("task_id", taskId).findUnique();
    }

    public void save(StatDailyPostmanTask task) {
        Ebean.getServer(GlobalDBControl.getDB()).save(task);
    }

    public void saveDetail(StatDailyPostmanTaskDetail task) {
        Ebean.getServer(GlobalDBControl.getDB()).save(task);
    }
}
