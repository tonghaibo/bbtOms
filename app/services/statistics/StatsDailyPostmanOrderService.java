package services.statistics;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.PagedList;
import com.avaje.ebean.Query;
import com.google.common.base.Strings;
import form.StatsDailyForm;
import models.statistics.StatDailyPostmanOrder;
import org.apache.commons.lang3.StringUtils;
import play.Logger;
import play.Logger.ALogger;
import services.ServiceFactory;
import utils.GlobalDBControl;

import java.util.ArrayList;
import java.util.List;

/**
 * 快递员订单服务类
 * @author chenxi
 */
public class StatsDailyPostmanOrderService {

	private static final StatsDailyPostmanOrderService statsDailyPostmanOrderService = ServiceFactory.getStatsDailyPostmanOrderService();
	private static StatsDailyPostmanOrderService instance = null;
	private static ALogger logger = Logger.of(StatsDailyPostmanOrderService.class);

	public static StatsDailyPostmanOrderService getInstance() {
		if (instance == null) {
			syncInit();
		}
		return instance;
	}

	private StatsDailyPostmanOrderService() {

	}

	private static synchronized void syncInit() {
		if (instance == null) {
			instance = new StatsDailyPostmanOrderService();
		}
	}

	public PagedList<StatDailyPostmanOrder> findStatsDailyPostmanOrderList(StatsDailyForm formPage) {
		List<String> sqlList = new ArrayList<>();
		List<Object> paramsList = new ArrayList<>();
		//统计时间
		if (formPage.between != null) {
            sqlList.add("stat_date between date_format(?,'%Y-%m-%d') ");
            sqlList.add(" date_format(?,'%Y-%m-%d') ");
			paramsList.add(formPage.between.start);
			paramsList.add(formPage.between.end);
		}

		Query<StatDailyPostmanOrder> query = Ebean.getServer(GlobalDBControl.getDB()).createQuery(StatDailyPostmanOrder.class);
		query.where(StringUtils.join(sqlList, " AND "));

		int i = 1; // first param uses index 1 NOT 0!
		for (Object param : paramsList) {
		    query.setParameter(i, param);
		    i++;
		}

		PagedList<StatDailyPostmanOrder> xyzList = query.orderBy("stat_date desc").findPagedList(formPage.page, formPage.size);
		
		return xyzList;
	}

	public StatDailyPostmanOrder findStatsDailyPostmanOrderById(Long id) {
		return Ebean.getServer(GlobalDBControl.getDB()).find(StatDailyPostmanOrder.class).where().eq("id", id).findUnique();
	}

}
