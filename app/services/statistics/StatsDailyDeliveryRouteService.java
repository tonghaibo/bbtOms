package services.statistics;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.PagedList;
import com.avaje.ebean.Query;
import form.StatsDailyForm;
import models.statistics.StatDailyDeliveryRoute;
import org.apache.commons.lang3.StringUtils;
import play.Logger;
import play.Logger.ALogger;
import services.ServiceFactory;
import utils.GlobalDBControl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 配送路径服务类
 * @author chenxi
 */
public class StatsDailyDeliveryRouteService {

	private static final StatsDailyDeliveryRouteService statsDailyDeliveryRouteService = ServiceFactory.getStatsDailyDeliveryRouteService();
	private static StatsDailyDeliveryRouteService instance = null;
	private static ALogger logger = Logger.of(StatsDailyDeliveryRouteService.class);

	public static StatsDailyDeliveryRouteService getInstance() {
		if (instance == null) {
			syncInit();
		}
		return instance;
	}

	private StatsDailyDeliveryRouteService() {

	}

	private static synchronized void syncInit() {
		if (instance == null) {
			instance = new StatsDailyDeliveryRouteService();
		}
	}

	public PagedList<StatDailyDeliveryRoute> findStatsDailyDeliveryRouteList(StatsDailyForm formPage) {
		List<String> sqlList = new ArrayList<>();
        List<Object> paramsList = new ArrayList<>();
		//统计时间
		if (formPage.between != null) {
            sqlList.add("stat_date between date_format(?,'%Y-%m-%d') ");
            sqlList.add(" date_format(?,'%Y-%m-%d') ");
			paramsList.add(formPage.between.start);
            paramsList.add(formPage.between.end);
		}

		Query<StatDailyDeliveryRoute> query = Ebean.getServer(GlobalDBControl.getDB()).createQuery(StatDailyDeliveryRoute.class);
		query.where(StringUtils.join(sqlList, " AND "));

		int i = 1; // first param uses index 1 NOT 0!
		for (Object param : paramsList) {
		    query.setParameter(i, param);
		    i++;
		}

		PagedList<StatDailyDeliveryRoute> xyzList = query.orderBy("stat_date desc").findPagedList(formPage.page, formPage.size);
		
		return xyzList;
	}

	public StatDailyDeliveryRoute findStatsDailyDeliveryRouteById(Long id) {
		return Ebean.getServer(GlobalDBControl.getDB()).find(StatDailyDeliveryRoute.class).where().eq("id", id).findUnique();
	}

    public StatDailyDeliveryRoute findByStatDate (String statDate) {
        return Ebean.getServer(GlobalDBControl.getDB()).find(StatDailyDeliveryRoute.class).where().eq("stat_date", statDate).findUnique();
    }

    public void save(StatDailyDeliveryRoute statDailyDeliveryRoute) {
        Ebean.getServer(GlobalDBControl.getDB()).save(statDailyDeliveryRoute);
    }

}
